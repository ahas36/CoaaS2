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
                RefreshLogics ref_type = RefreshExecutor.resolveRefreshLogic(new JSONObject(request.getSla()), profile);

                // Evaluate and Cache if selected
                boolean result = evaluateAndCache(request.getContext(), request.getReference(),
                        ref_type.toString().toLowerCase(), profile);
                if(result){
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
                    // Using 200 considering the creation of the hash key and successfully caching.
                    return CPREEResponse.newBuilder().setStatus("200").setBody("Cached").build();
                }
                // Returning 204 (No Response) to indicate no has key to return since not cached.
                return CPREEResponse.newBuilder().setStatus("204").setBody("Not Cached").build();
            } else
                // Returning any other errors.
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

    private static boolean evaluateAndCache(String context, CacheLookUp lookup, String refPolicy,
                                            ContextProviderProfile profile) {
        try {
            // TODO:
            // Evaluate the context item for caching.
            // Since the lifetime of a context item is considered to be fixed at this phase of the implementation,
            // I can assume that the refreshing policy is also fixed.
            if(!profile.getAccessTrend().equals("NaN")) {
                SQEMServiceGrpc.SQEMServiceBlockingStub  sqemStub
                        = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

                long est_cacheLife = -1;
                double access_trend = profile.getAccessTrend().equals("NaN") ?
                        0 : Double.valueOf(profile.getAccessTrend());
                if(access_trend <= 0){
                    // Caching only for an estimated period of time until re-evaluation
                    QueryClassProfile cqc_profile = sqemStub.getQueryClassProfile(ContextProfileRequest.newBuilder()
                            .setPrimaryKey(lookup.getQClass()).build());
                    if(cqc_profile.getStatus().equals("200")){
                        // Should calculate the retrieval, and cache efficiencies
                        est_cacheLife = 600;
                    }
                }
                // When the access trend is positive, the item would be cached for a longer period until evicted by
                // the eviction algorithm.

                // Actual Caching
                SQEMResponse response = sqemStub.cacheEntity(CacheRequest.newBuilder()
                        .setJson(context)
                        .setRefreshLogic(refPolicy)
                        // TODO: This cache life should be saved in the eviction registry
                        .setCachelife(est_cacheLife)
                        .setReference(lookup).build());

                return response.getStatus().equals("200") ? true : false;
            }
            else {
                // Look at the other metrics
                return false;
            }
        }
        catch(Exception ex){
            log.severe("Context Caching failed due to: " + ex.getMessage());
            return false;
        }
    }
}
