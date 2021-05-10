[Ivy]
1787D3F58FD61134 9.2.0 #module
>Proto >Proto Collection #zClass
Ho0 HubspotContatsDemo Big #zClass
Ho0 B #cInfo
Ho0 #process
Ho0 @AnnotationInP-0n ai ai #zField
Ho0 @TextInP .type .type #zField
Ho0 @TextInP .processKind .processKind #zField
Ho0 @TextInP .xml .xml #zField
Ho0 @TextInP .responsibility .responsibility #zField
Ho0 @StartRequest f0 '' #zField
Ho0 @EndTask f1 '' #zField
Ho0 @RestClientCall f6 '' #zField
Ho0 @PushWFArc f5 '' #zField
Ho0 @PushWFArc f2 '' #zField
>Proto Ho0 Ho0 HubspotContatsDemo #zField
Ho0 f0 outLink start.ivp #txt
Ho0 f0 inParamDecl '<> param;' #txt
Ho0 f0 requestEnabled true #txt
Ho0 f0 triggerEnabled false #txt
Ho0 f0 callSignature start() #txt
Ho0 f0 caseData businessCase.attach=true #txt
Ho0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>start.ivp</name>
    </language>
</elementInfo>
' #txt
Ho0 f0 @C|.responsibility Everybody #txt
Ho0 f0 81 49 30 30 -22 17 #rect
Ho0 f1 337 49 30 30 0 15 #rect
Ho0 f6 clientId eed55212-87d9-4d61-ad77-4c11f66adadb #txt
Ho0 f6 path /crm/v3/objects/contacts/search #txt
Ho0 f6 method POST #txt
Ho0 f6 bodyInputType ENTITY #txt
Ho0 f6 bodyObjectType com.hubapi.api.client.PublicObjectSearchRequest #txt
Ho0 f6 bodyObjectMapping 'param.after=0;
param.filterGroups=[];
param.limit=10;
param.properties=[];
param.sorts=["name"];
' #txt
Ho0 f6 resultType com.hubapi.api.client.CollectionResponseWithTotalSimplePublicObjectForwardPaging #txt
Ho0 f6 responseCode ivy.log.info(result.results); #txt
Ho0 f6 clientErrorCode ivy:error:rest:client #txt
Ho0 f6 statusErrorCode ivy:error:rest:client #txt
Ho0 f6 168 42 112 44 0 -8 #rect
Ho0 f5 111 64 168 64 #arcP
Ho0 f2 280 64 337 64 #arcP
>Proto Ho0 .type ch.ivyteam.connector.hubspot.demo.Data #txt
>Proto Ho0 .processKind NORMAL #txt
>Proto Ho0 0 0 32 24 18 0 #rect
>Proto Ho0 @|BIcon #fIcon
Ho0 f0 mainOut f5 tail #connect
Ho0 f5 head f6 mainIn #connect
Ho0 f6 mainOut f2 tail #connect
Ho0 f2 head f1 mainIn #connect
