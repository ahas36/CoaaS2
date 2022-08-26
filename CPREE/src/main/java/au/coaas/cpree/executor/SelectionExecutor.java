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

import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class SelectionExecutor {

    private static final Logger log = Logger.getLogger(SelectionExecutor.class.getName());
    private static Hashtable<String, Double> cacheLookupLatency;

    private static ExecutorService executor = Executors.newScheduledThreadPool(20);

    public static void updateCacheRecord(){
        SQEMServiceGrpc.SQEMServiceBlockingStub blockingStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        CachePerformance result = blockingStub.getcachePerformance(Empty.newBuilder().build());

        if(cacheLookupLatency == null) {
            cacheLookupLatency = new Hashtable() {{
                cacheLookupLatency.put("200", 0.0);
                cacheLookupLatency.put("404", 0.0);
                cacheLookupLatency.put("400", 0.0);
            }};
        }

        // These values are in Seconds
        if(result.getStatus().equals("200")){
            cacheLookupLatency.put("200", result.getHitLatency());
            cacheLookupLatency.put("404", result.getMissLatency());
            cacheLookupLatency.put("400", result.getPartialMissLatency());
        }
    }

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
                boolean result = evaluateAndCache(request.getContext(), request.getReference(), request.getSla(),
                        ref_type.toString().toLowerCase(), profile, hashKey);
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

    private static boolean evaluateAndCache(String context, CacheLookUp lookup, String refPolicy, String sla,
                                            ContextProviderProfile profile, String hashkey) {
        try {
            boolean cache = false;
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
                    // TODO: IMPORTANT!
                    // In the next line, it is assumed that there is only one class of context queries because, the
                    // implementation is only being tested for a single scenario (all queries are of the same class.)
                    // So, it is retrieving the stats from the performance summary for the query class of the "current query".
                    // This shouldn't be the case. I should rather retrieve the expected value considering all of CQ classes which the item is used
                    // from the context query class network.
                    QueryClassProfile cqc_profile = sqemStub.getQueryClassProfile(ContextProfileRequest.newBuilder()
                            .setPrimaryKey(lookup.getQClass()).build());

                    if(cqc_profile.getStatus().equals("200")){
                        JSONObject slaObj = new JSONObject(sla);

                        // 1. Retrieval Efficiency
                        double ret_effficiency = getRetrievalEfficiency(lookup.getServiceId(), hashkey, profile,
                                cqc_profile, refPolicy, slaObj);
                        double reliability = profile.getRelaibility().equals("NaN") ?
                                    0.0 : Double.valueOf(profile.getRelaibility());
                        // 2. Caching Efficiency
                        double caching_efficiency = getCacheEfficiency();

                        // This is temporary
                        if(ret_effficiency < 1){
                            cache = true;
                            est_cacheLife = 600;
                        }
                    }
                }
                // When the access trend is positive, the item would be cached for a longer period until evicted by
                // the eviction algorithm.

                // Actual Caching
                if(cache) {
                    SQEMResponse response = sqemStub.cacheEntity(CacheRequest.newBuilder()
                            .setJson(context)
                            .setRefreshLogic(refPolicy)
                            // TODO: This cache life should be saved in the eviction registry
                            .setCachelife(est_cacheLife)
                            .setReference(lookup).build()); // This is in miliseconds
                    return response.getStatus().equals("200") ? true : false;
                }
            }
            // TODO:
            // else {
                // Look at the other metrics
            // }

            return false;
        }
        catch(Exception ex){
            log.severe("Context Caching failed due to: " + ex.getMessage());
            return false;
        }
    }

    private static double getCacheEfficiency() {
        return 0.0;
    }

    private static double getRetrievalEfficiency(String serviceId, String hashkey, ContextProviderProfile profile,
                                                 QueryClassProfile cqc_profile, String refPolicy, JSONObject slaObj){

        /** Initializing the variables **/
        double fthr = Double.valueOf(profile.getExpFthr());
        double lambda = Double.valueOf(profile.getExpAR());
        double rtmax = Double.valueOf(cqc_profile.getRtmax());
        double retCost = Double.valueOf(profile.getExpCost());
        double penalty = Double.valueOf(cqc_profile.getPenalty());
        double retlatency = Double.valueOf(profile.getExpRetLatency());
        double lifetime = slaObj.getJSONObject("freshness").getDouble("value");
        double sampleInterval = slaObj.getJSONObject("updateFrequency").getDouble("value");

        /** Redirector Mode **/
        // Total cost of retrieval
        double red_ret_cost = retCost;
        // Penalties if not cached
        double red_penalties = 0;
        if(retlatency >= rtmax) red_penalties = penalty;
        else {
            SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
            // This method provides the probability that the retrieval latency only exceeds the rtmax.
            // TODO:
            // Here too, I'm only considering exp_rtmax of a single query class (since we have only 1 for testing 1 scenario).
            ProbDelay prob_delay = sqemStub.getProbDelay(ProbDelayRequest.newBuilder()
                    .setPrimaryKey(serviceId)
                    .setHashKey(hashkey)
                    .setLevel(CacheLevels.RAW_CONTEXT.toString().toLowerCase())
                    .setThreshold(rtmax - cacheLookupLatency.get("404"))
                    .build());
            // TODO:
            // Should also consider the retrieval latencies of other entities in the context query class as well.
            // Add them with the retl of the current item in order to calculate the total retrieval latency which is actually what is
            // results in delay for the query. So, assuming that retrieval latency > processing latency and predominant.
            red_penalties = prob_delay.getValue() * penalty;
        }
        double red_cost = (red_ret_cost + red_penalties) * lambda;

        /** Retrieval costs if cached **/
        double cache_cost = 0;
        switch(refPolicy){
            case "proactive_shift":
            case "reactive": {
                // Check whether all the values are of the same unit.
                double cache_penalties = 0;

                SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                        = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

                ProbDelay prob_delay = sqemStub.getProbDelay(ProbDelayRequest.newBuilder()
                        .setPrimaryKey(serviceId)
                        .setHashKey(hashkey)
                        .setLevel(CacheLevels.RAW_CONTEXT.toString().toLowerCase())
                        .setThreshold(rtmax - cacheLookupLatency.get("400"))
                        .build());

                if(sampleInterval == 0) {
                    double exp_prd = (lifetime - retlatency) * fthr;
                    double hits = exp_prd * lambda;
                    double exp_mr = 1/(hits + 1);

                    // Penalties when needing to refresh
                    if(retlatency >= rtmax) cache_penalties = penalty;
                    else cache_penalties = (prob_delay.getValue()) * penalty;

                    cache_cost = (retCost + cache_penalties) * lambda * exp_mr;
                }
                else {
                    if(lifetime > sampleInterval){
                        // Refreshing at a slightly lower rate than the sampling
                        double exp_prd = sampleInterval + ((lifetime - sampleInterval) * fthr);
                        double ref_rate = 1 / exp_prd;
                        cache_cost = (retCost + cache_penalties) * ref_rate;

                    }
                    else {
                        // Refreshing almost at the sampling rate
                        cache_cost = (retCost + cache_penalties) * (1/sampleInterval);
                    }
                }
                break;
            }
        }

        // Retrieval efficiency is the ratio between the cost of totally retrieving from the CPs in an adhoc manner
        // called the redirector mode, and the cost of serving from a cache
        return cache_cost / red_cost;
    }
}
