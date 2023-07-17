package au.coaas.cqc.executor;

import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.CSIServiceGrpc;
import au.coaas.csi.proto.ContextMappingRequest;
import au.coaas.grpc.client.CSIChannel;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqc.proto.ExecutionRequest;
import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.sqem.proto.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ContextEntityManager {

    private static Logger log = Logger.getLogger(ContextEntityManager.class.getName());

    public static CdqlResponse registerContextEntity(ExecutionRequest request)
    {
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        JSONObject data = new JSONObject(request.getCdql());

        RegisterEntityRequest.Builder builder = RegisterEntityRequest.newBuilder();
        if(data.has("EntityType")){
            JSONObject entityType = data.getJSONObject("EntityType");
            JSONObject entity = data.getJSONObject("Attributes");
            JSONArray keys = data.getJSONArray("key");
            Long timestamp = data.has("observedTime")?
                    data.optLong("observedTime"): System.currentTimeMillis();

            builder.setJson(entity.toString())
                    .setEt(ContextEntityType.newBuilder()
                            .setVocabURI(entityType.getString("namespace"))
                            .setType(entityType.getString("type")).build())
                    .setProviderId(request.getQueryid())
                    .setObservedTime(timestamp)
                    .setKey(keys.toString())
                    .setRetLatency(0);
        }
        else {
            SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub_2
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
            SQEMResponse providerDetails = sqemStub_2.getContextServiceInfo(ContextServiceId.newBuilder()
                    .setId(request.getQueryid()).build());
            JSONObject cpsla = new JSONObject(providerDetails.getBody());
            JSONObject info = cpsla.getJSONObject("info");

            CSIServiceGrpc.CSIServiceBlockingStub csiStub
                    = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance().getChannel());
            CSIResponse entity = csiStub.mapToEntity(
                    ContextMappingRequest.newBuilder().setJsonResponse(request.getCdql())
                            .setAttributes(providerDetails.getBody()).build());

            if(entity.getStatus().equals("200"))
                builder.setJson(entity.getBody())
                        .setEt(ContextEntityType.newBuilder()
                                .setVocabURI(info.getString("graph").replaceAll("^<|>$",""))
                                .setType(info.getString("name")).build())
                        .setKey(cpsla.getJSONObject("sla").getJSONArray("key").toString())
                        .setObservedTime(System.currentTimeMillis())
                        .setProviderId(request.getQueryid())
                        .setRetLatency(0);
        }

        SQEMResponse sqemResponse = sqemStub.registerContextEntity(builder.build());
        return CdqlResponse.newBuilder().setBody(sqemResponse.getBody()).setStatus(sqemResponse.getStatus()).build();
    }

    public static CdqlResponse updateContextEntity(ExecutionRequest request)
    {
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        String rawRequest = request.getCdql();
        String providerId = request.getQueryid();

        UpdateEntityRequest.Builder updateRequestBuilder = UpdateEntityRequest.newBuilder();
        if(rawRequest.trim().startsWith("[")){
            SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub_2
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
            SQEMResponse providerDetails = sqemStub_2.getContextServiceInfo(ContextServiceId.newBuilder()
                    .setId(providerId).build());

            CSIServiceGrpc.CSIServiceBlockingStub csiStub
                    = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance().getChannel());
            CSIResponse entity = csiStub.mapToEntity(
                    ContextMappingRequest.newBuilder().setJsonResponse(rawRequest)
                            .setAttributes(providerDetails.getBody()).build());

            if(entity.getStatus().equals("200"))
                updateRequestBuilder.setJson(entity.getBody());
            else CdqlResponse.newBuilder().setStatus(entity.getStatus()).build();

        } else {
            JSONObject data = new JSONObject(rawRequest);
            if(data.has("EntityType")){
                // This means the update request has come with the nessecary metadata.
                // This can be triggered only via the event monitoring path
                JSONObject entityType = data.getJSONObject("EntityType");
                JSONObject entity = data.getJSONObject("Attributes");
                JSONArray keys = data.getJSONArray("key");
                Long timestamp = data.has("observedTime")? data.optLong("observedTime"): System.currentTimeMillis();
                updateRequestBuilder.setJson(entity.toString())
                        .setEt(ContextEntityType.newBuilder().setVocabURI(entityType.getString("namespace"))
                                .setType(entityType.getString("type")).build())
                        .setObservedTime(timestamp)
                        .setRetLatency(0)
                        .setProviderId(providerId)
                        .setKey(keys.toString());
            }
            else {
                // No metadata. Just the data about a context entity from a Context Provider.
                SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub_2
                        = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
                SQEMResponse providerDetails = sqemStub_2.getContextServiceInfo(ContextServiceId.newBuilder()
                        .setId(providerId).build());
                JSONObject cpsla = new JSONObject(providerDetails.getBody());
                JSONObject info = cpsla.getJSONObject("info");

                CSIServiceGrpc.CSIServiceBlockingStub csiStub
                        = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance().getChannel());
                CSIResponse entity = csiStub.mapToEntity(
                        ContextMappingRequest.newBuilder().setJsonResponse(rawRequest)
                                .setAttributes(providerDetails.getBody()).build());

                if(entity.getStatus().equals("200"))
                    updateRequestBuilder.setJson(entity.getBody())
                            .setEt(ContextEntityType.newBuilder()
                                    .setVocabURI(info.getString("graph").replaceAll("^<|>$",""))
                                    .setType(info.getString("name")).build())
                            .setKey(cpsla.getJSONObject("sla").getJSONArray("key").toString())
                            .setObservedTime(System.currentTimeMillis())
                            .setProviderId(providerId)
                            .setRetLatency(0);

                else CdqlResponse.newBuilder().setStatus(entity.getStatus()).build();
            }
        }

        SQEMResponse sqemResponse = sqemStub.updateContextEntity(updateRequestBuilder.build());
        return CdqlResponse.newBuilder().setBody(sqemResponse.getBody()).setStatus(sqemResponse.getStatus()).build();
    }
}
