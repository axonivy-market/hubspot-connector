package ch.ivyteam.connecort.hubspot.auth.auth;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import ch.ivyteam.ivy.bpm.error.BpmPublicErrorBuilder;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.rest.client.FeatureConfig;
import ch.ivyteam.ivy.rest.client.oauth2.OAuth2BearerFilter;
import ch.ivyteam.ivy.rest.client.oauth2.OAuth2RedirectErrorBuilder;
import ch.ivyteam.ivy.rest.client.oauth2.OAuth2TokenRequester.AuthContext;
import ch.ivyteam.ivy.rest.client.oauth2.uri.OAuth2CallbackUriBuilder;
import ch.ivyteam.ivy.rest.client.oauth2.uri.OAuth2UriProperty;

public class HubspotApiKeyOrOAuth2Feature implements Feature
{
  private static final String VARIABLE_BASE = "Hubspot.";

  public static interface Property
  {
    String API_KEY = "Auth.apikey";
    String CLIENT_ID = "Auth.clientId";
    String CLIENT_SECRET = "Auth.clientSecret";
    String SCOPE = "Auth.scope";
    String AUTH_BASE_URI = "Auth.baseUri";
  }

  @Override
  public boolean configure(FeatureContext context)
  {
    var config = new FeatureConfig(context.getConfiguration(), HubspotApiKeyOrOAuth2Feature.class);
    var apiKey = config(config, Property.API_KEY);
    if (apiKey.isEmpty())
    {
      registerOAuth(context, config);
    }
    else
    {
      context.register(new ApiKeyUrlFeature(apiKey));
    }
    return true;
  }
  
  private static class ApiKeyUrlFeature implements ClientRequestFilter
  {
    private final String apiKey;

    private ApiKeyUrlFeature(String apiKey)
    {
      this.apiKey = apiKey;
    }

    @Override
    public void filter(ClientRequestContext context) throws IOException
    {
      var builder = UriBuilder.fromUri(context.getUri());
      builder.queryParam("hapikey", apiKey);
      context.setUri(builder.build());
    }
  }

  private void registerOAuth(FeatureContext context, FeatureConfig config)
  {
    var tokenUri = new OAuth2UriProperty(config, Property.AUTH_BASE_URI,
            "https://api.hubapi.com/oauth/v1");
    var authUri = new OAuth2UriProperty(config, Property.AUTH_BASE_URI,
            "https://app.hubspot.com/oauth");
    var oauth2 = new OAuth2BearerFilter(
            ctxt -> requestToken(ctxt, authUri),
            tokenUri);
    context.register(oauth2, Priorities.AUTHORIZATION);
  }

  private static Response requestToken(AuthContext ctxt, OAuth2UriProperty uriFactory)
  {
    FeatureConfig config = ctxt.config;
    var authCode = ctxt.authCode();
    var refreshToken = ctxt.refreshToken();
    if (authCode.isEmpty() && refreshToken.isEmpty())
    {
      authError(config, uriFactory)
              .withMessage("missing permission from user to act in his name.")
              .throwError();
    }

    Form form = createTokenPayload(config, authCode, refreshToken);
    var response = ctxt.target.request()
            .accept(MediaType.WILDCARD)
            .post(Entity.form(form));
    return response;
  }

  private static Form createTokenPayload(FeatureConfig config, Optional<String> authCode,
          Optional<String> refreshToken)
  {
    Form form = new Form();
    form.param("client_id", clientId(config));
    form.param("scope", scope(config));
    if (authCode.isPresent())
    {
      form.param("code", authCode.get());
      form.param("grant_type", "authorization_code");
    }
    if (refreshToken.isPresent())
    {
      form.param("refresh_token", refreshToken.get());
      form.param("grant_type", "refresh_token");
    }
    form.param("redirect_uri", OAuth2CallbackUriBuilder.create().toUri().toASCIIString());
    form.param("client_secret", config(config, Property.CLIENT_SECRET));
    return form;
  }

  private static BpmPublicErrorBuilder authError(FeatureConfig config, OAuth2UriProperty uriFactory)
  {
    var uri = createMsAuthCodeUri(config, uriFactory);
    return OAuth2RedirectErrorBuilder
            .create(uri)
            .withMessage("Missing permission from user to act in his name.");
  }

  private static URI createMsAuthCodeUri(FeatureConfig config, OAuth2UriProperty uriFactory)
  {
    return UriBuilder.fromUri(uriFactory.getUri("authorize"))
            .queryParam("client_id", clientId(config))
            .queryParam("scope", scope(config))
            .queryParam("redirect_uri", OAuth2CallbackUriBuilder.create().toUri())
            .queryParam("response_type", "code")
            .queryParam("response_mode", "query")
            .build();
  }

  private static String clientId(FeatureConfig config)
  {
    return config(config, Property.CLIENT_ID);
  }

  private static String scope(FeatureConfig config)
  {
    return config.readMandatory((Property.SCOPE));
  }

  private static String config(FeatureConfig config, String propertyKey)
  {
    return config.read(propertyKey).orElse(Ivy.var().get(VARIABLE_BASE + propertyKey));
  }
}
