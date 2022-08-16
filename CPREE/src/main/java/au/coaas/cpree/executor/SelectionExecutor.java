package au.coaas.cpree.executor;

import au.coaas.cpree.proto.ProactiveRefreshRequest;
import au.coaas.cpree.utils.Utilities;
import au.coaas.cpree.proto.CPREEResponse;
import au.coaas.cpree.utils.enums.CacheLevels;
import au.coaas.cpree.utils.enums.RefreshLogics;
import au.coaas.cpree.proto.CacheSelectionRequest;

import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.sqem.proto.CacheRequest;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.ContextProfileRequest;

import au.coaas.grpc.client.SQEMChannel;

import org.json.JSONObject;

public class SelectionExecutor {
    public static CPREEResponse execute(CacheSelectionRequest request){
        // Get Context Identifier
        String hashKey = Utilities.getHashKey(request.getReference().getParamsMap());

        // Configuring refreshing
        // Get Context Provider's Profile
        SQEMServiceGrpc.SQEMServiceBlockingStub blockingStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        SQEMResponse profile = blockingStub.getContextProviderProfile(ContextProfileRequest.newBuilder()
                        .setPrimaryKey(request.getReference().getServiceId())
                        .setHashKey(hashKey)
                        .setLevel(CacheLevels.RAW_CONTEXT.toString().toLowerCase())
                        .build());

        RefreshLogics ref_type = RefreshExecutor.resolveRefreshLogic(new JSONObject(request.getSla()),
                request.getReference().getServiceId(),hashKey,profile.getBody());

        if(ref_type.equals(RefreshLogics.PROACTIVE_SHIFT)){
            JSONObject freshReq = (new JSONObject(request.getSla())).getJSONObject("freshness");
            double fthresh = !profile.getBody().equals("NaN") ?
                    Double.valueOf(profile.getBody()) :
                    freshReq.getDouble("fthresh");

            double res_life = freshReq.getDouble("value") - Double.valueOf(profile.getMeta());
            RefreshExecutor.setProactiveRefreshing(ProactiveRefreshRequest.newBuilder()
                            .setEt(request.getReference().getEt())
                            .setRequest(request).setFthreh(fthresh)
                            .setLifetime(freshReq.getDouble("value"))
                            .setResiLifetime(res_life)
                            .setHashKey(hashKey)
                            .setRefreshPolicy(ref_type.toString().toLowerCase())
                    .build());
        }

        // TODO:
        // Evaluate the context item for caching.

        // Actual Caching
        SQEMServiceGrpc.SQEMServiceBlockingStub asyncStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        asyncStub.cacheEntity(CacheRequest.newBuilder()
                .setJson(request.getContext())
                .setRefreshLogic(ref_type.toString().toLowerCase())
                // TODO: This cache life should be saved in the eviction registry
                .setCachelife(600)
                .setReference(request.getReference()).build());

        return CPREEResponse.newBuilder().setStatus("200").build();
    }
}
