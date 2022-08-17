package au.coaas.cpree.executor;

import au.coaas.cpree.utils.Utilities;
import au.coaas.cpree.proto.CPREEResponse;
import au.coaas.cpree.utils.enums.CacheLevels;
import au.coaas.cpree.utils.enums.RefreshLogics;
import au.coaas.cpree.proto.CacheSelectionRequest;
import au.coaas.cpree.proto.ProactiveRefreshRequest;

import au.coaas.sqem.proto.*;

import au.coaas.grpc.client.SQEMChannel;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class SelectionExecutor {

    private static final Logger log = Logger.getLogger(SelectionExecutor.class.getName());

    private static ExecutorService executor = Executors.newScheduledThreadPool(20);

    public static CPREEResponse execute(CacheSelectionRequest request) {
        try {
            // Get Context Identifier
            String hashKey = Utilities.getHashKey(request.getReference().getParamsMap());

            // Get Context Provider's Profile
            SQEMServiceGrpc.SQEMServiceBlockingStub blockingStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

            ContextProviderProfile profile = blockingStub.getContextProviderProfile(ContextProfileRequest.newBuilder()
                    .setPrimaryKey(request.getReference().getServiceId())
                    .setHashKey(hashKey)
                    .setLevel(CacheLevels.RAW_CONTEXT.toString().toLowerCase())
                    .build());

            if (profile.getStatus().equals("200")) {
                RefreshLogics ref_type = RefreshExecutor.resolveRefreshLogic(new JSONObject(request.getSla()),
                        request.getReference().getServiceId(), hashKey, profile.getExpFthr());

                // Evaluate and Cache if selected
                int result = evaluateAndCache(request.getContext(), request.getReference(), ref_type.toString().toLowerCase());

                // Configuring refreshing
                executor.execute(() -> {
                    if (ref_type.equals(RefreshLogics.PROACTIVE_SHIFT)) {
                        JSONObject freshReq = (new JSONObject(request.getSla())).getJSONObject("freshness");
                        double fthresh = !profile.getExpFthr().equals("NaN") ?
                                Double.valueOf(profile.getExpFthr()) :
                                freshReq.getDouble("fthresh");

                        double res_life = freshReq.getDouble("value") - profile.getLastRetLatency();
                        RefreshExecutor.setProactiveRefreshing(ProactiveRefreshRequest.newBuilder()
                                .setEt(request.getReference().getEt())
                                .setRequest(request).setFthreh(fthresh)
                                .setLifetime(freshReq.getDouble("value"))
                                .setResiLifetime(res_life)
                                .setHashKey(hashKey)
                                .setRefreshPolicy(ref_type.toString().toLowerCase())
                                .build());
                    }
                });

                return CPREEResponse.newBuilder().setStatus("200").build();
            } else
                return CPREEResponse.newBuilder().setStatus(profile.getStatus())
                        .setBody("Failed to fetch context provider profile.").build();
        }
        catch(Exception ex){
            log.severe("Could not refresh context!");
            log.info("Cause: " + ex.getMessage());
            return CPREEResponse.newBuilder().setStatus("500")
                    .setBody(ex.getMessage()).build();
        }
    }

    private static int evaluateAndCache(String context, CacheLookUp lookup, String refPolicy) {
        try {
            // TODO:
            // Evaluate the context item for caching.

            // Actual Caching
            SQEMServiceGrpc.SQEMServiceBlockingStub asyncStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
            SQEMResponse response = asyncStub.cacheEntity(CacheRequest.newBuilder()
                    .setJson(context)
                    .setRefreshLogic(refPolicy)
                    // TODO: This cache life should be saved in the eviction registry
                    .setCachelife(600)
                    .setReference(lookup).build());

            return response.getStatus().equals("200") ? 0 : 1;
        }
        catch(Exception ex){
            log.severe("Context Caching failed due to: " + ex.getMessage());
            return 1;
        }
    }
}
