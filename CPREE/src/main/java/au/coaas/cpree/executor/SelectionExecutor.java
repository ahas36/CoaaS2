package au.coaas.cpree.executor;

import au.coaas.cpree.proto.LearnedWeights;
import au.coaas.cpree.utils.LimitedQueue;
import au.coaas.cpree.utils.Utilities;
import au.coaas.cpree.proto.CPREEResponse;
import au.coaas.cpree.utils.enums.CacheLevels;
import au.coaas.cpree.utils.enums.RefreshLogics;
import au.coaas.cpree.proto.CacheSelectionRequest;
import au.coaas.cpree.proto.ProactiveRefreshRequest;

import au.coaas.sqem.proto.*;

import au.coaas.grpc.client.SQEMChannel;
import au.coaas.cpree.proto.Empty;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.AbstractMap;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class SelectionExecutor {

    private static final Logger log = Logger.getLogger(SelectionExecutor.class.getName());

    // Static Registries
    private static Hashtable<String, Double> weightThresholds = new Hashtable(){{
        // Starting with equal weights for all the weights
        weightThresholds.put("kappa", 0.2);
        weightThresholds.put("mu", 0.2);
        weightThresholds.put("pi", 0.2);
        weightThresholds.put("delta", 0.2);
        weightThresholds.put("row", 0.2);
        weightThresholds.put("threshold", 1.0);
    }};
    private static Hashtable<String, Double> cachePerfStats = new Hashtable<>();

    // Dynamic Registries
    private static Hashtable<String, LocalDateTime> delayRegistry = new Hashtable<>();
    private static Hashtable<String, Double> cacheLookupLatency;
    private static LimitedQueue<Double> valueHistory = new LimitedQueue<>(1000);

    private static ExecutorService executor = Executors.newScheduledThreadPool(20);

    public static Empty updateWeights(LearnedWeights request){
        weightThresholds.put("kappa", request.getKappa());
        weightThresholds.put("mu", request.getMu());
        weightThresholds.put("pi", request.getPi());
        weightThresholds.put("delta", request.getDelta());
        weightThresholds.put("row", request.getRow());
        weightThresholds.put("threshold", request.getThreshold() * valueHistory.average());

        return null;
    }

    public static void updateCacheRecord(){
        SQEMServiceGrpc.SQEMServiceBlockingStub blockingStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        CachePerformance result = blockingStub.getcachePerformance(au.coaas.sqem.proto.Empty.newBuilder().build());

        if(cacheLookupLatency == null) {
            cacheLookupLatency = new Hashtable() {{
                put("200", 0.0);
                put("404", 0.0);
                put("400", 0.0);
            }};
        }

        // These time related values are in Seconds, and monetary in AUD
        if(result.getStatus().equals("200")){
            // Cache performance latency registry update
            cacheLookupLatency.put("200", result.getHitLatency());
            cacheLookupLatency.put("404", result.getMissLatency());
            cacheLookupLatency.put("400", result.getPartialMissLatency());

            // Other performances
            cachePerfStats.put("cacheCost", result.getProcessCost());
            cachePerfStats.put("costPerByte", result.getCostPerByte());
            cachePerfStats.put("processCost", result.getProcessCost());
            cachePerfStats.put("cacheUtility", result.getProcessCost());
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
                            JSONObject sampling = (new JSONObject(request.getSla())).getJSONObject("updateFrequency");
                            double fthresh = !profile.getExpFthr().equals("NaN") ?
                                    Double.valueOf(profile.getExpFthr()) :
                                    freshReq.getDouble("fthresh");

                            double res_life = freshReq.getDouble("value") - profile.getLastRetLatency();

                            RefreshExecutor.setProactiveRefreshing(ProactiveRefreshRequest.newBuilder()
                                    .setEt(request.getReference().getEt())
                                    .setRequest(request).setFthreh(fthresh)
                                    .setLifetime(freshReq.getDouble("value")) // seconds
                                    .setResiLifetime(res_life) // seconds
                                    .setHashKey(hashKey)
                                    .setSamplingInterval(sampling.getDouble("value")) // seconds
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
            String contextId = lookup.getServiceId() + "-" + hashkey;
            LocalDateTime curr = LocalDateTime.now();

            // TODO:
            // Evaluate the context item for caching.
            // Since the lifetime of a context item is considered to be fixed at this phase of the implementation,
            // I can assume that the refreshing policy is also fixed.
            if(!profile.getAccessTrend().equals("NaN") &&
                    (!delayRegistry.containsKey(contextId) ||
                            (delayRegistry.containsKey(contextId) && delayRegistry.get(contextId).isAfter(curr)))) {
                SQEMServiceGrpc.SQEMServiceBlockingStub  sqemStub
                        = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

                long est_cacheLife = -1;
                long est_delayTime = 0;
                double access_trend = profile.getAccessTrend().equals("NaN") ?
                        0 : Double.valueOf(profile.getAccessTrend());

                QueryClassProfile cqc_profile = sqemStub.getQueryClassProfile(ContextProfileRequest.newBuilder()
                        .setPrimaryKey(lookup.getQClass()).build());

                if(access_trend <= 0){
                    // TODO: IMPORTANT!
                    // In the next line, it is assumed that there is only one class of context queries because, the
                    // implementation is only being tested for a single scenario (all queries are of the same class.)
                    // So, it is retrieving the stats from the performance summary for the query class of the "current query".
                    // This shouldn't be the case. I should rather retrieve the expected value considering all of CQ classes which the item is used
                    // from the context query class network.

                    if(cqc_profile.getStatus().equals("200")){
                        JSONObject slaObj = new JSONObject(sla);
                        double fthr = Double.valueOf(profile.getExpFthr());
                        double lambda = Double.valueOf(profile.getExpAR()); // per second
                        double exp_prd = slaObj.getJSONObject("freshness").getDouble("value") * (1 - fthr);

                        // The expiry period should be more than the Retrieval latency to not be stale by the time the item is retrieved.
                        // The expiry period should also be greter than the time between 2 access to the item in order to at least have > 0 chance of a hit.
                        if(exp_prd > Double.valueOf(profile.getExpRetLatency()) && exp_prd > (1/lambda)) {
                            // 1. Retrieval Efficiency
                            AbstractMap.SimpleEntry<Double,Double> ret_effficiency = getRetrievalEfficiency(lookup.getServiceId(), profile,
                                    cqc_profile, refPolicy, slaObj);
                            // 2. Reliability of retrieval
                            double reliability = profile.getRelaibility().equals("NaN") ?
                                    0.0 : Double.valueOf(profile.getRelaibility());
                            // 3. Access Rate Trend
                            double access_rate_trend = profile.getAccessTrend().equals("NaN") ?
                                    0.0 : Double.valueOf(profile.getAccessTrend());
                            // 4. Caching Efficiency
                            int contextSize = context.getBytes().length;
                            double caching_efficiency = getCacheEfficiency(contextSize, ret_effficiency.getValue(),
                                    profile.getExpRetLatency().equals("NaN") ? profile.getLastRetLatency() :
                                            Double.valueOf(profile.getExpRetLatency()));
                            // 5. Query Complexity
                            // TODO: Need to calculate using the parse query tree
                            // Using 3.5 since it is the complexity of the currently tested 'Medium' complex query.
                            double query_complexity = 3.5;

                            double cacheConfidence = (weightThresholds.get("pi") * ret_effficiency.getKey()) +
                                    (weightThresholds.get("mu") * caching_efficiency) +
                                    (weightThresholds.get("kappa") * access_rate_trend) +
                                    (weightThresholds.get("delta") * reliability) +
                                    (weightThresholds.get("row") * query_complexity);

                            valueHistory.add(cacheConfidence);

                            if(cacheConfidence < weightThresholds.get("threshold")){
                                cache = true;
                                est_cacheLife = 600000; // miliseconds
                            }
                            else {
                                // This is to evaluate an item only once per window
                                est_delayTime = 60000;
                                delayRegistry.put(contextId, curr.plus(est_delayTime, ChronoUnit.MILLIS)); // miliseconds
                            }
                        }
                    }
                }
                else {
                    // When the access trend is positive, the item would be cached for a longer period until evicted by
                    // the eviction algorithm.
                    if(cqc_profile.getStatus().equals("200")) {
                        JSONObject slaObj = new JSONObject(sla);
                        double fthr = Double.valueOf(profile.getExpFthr());
                        double lambda = Double.valueOf(profile.getExpAR()); // per second
                        double exp_prd = slaObj.getJSONObject("freshness").getDouble("value") * (1 - fthr);

                        // The expiry period should be more than the Retrieval latency to not be stale by the time the item is retrieved.
                        // The expiry period should also be greter than the time between 2 access to the item in order to at least have > 0 chance of a hit.
                        if(exp_prd > Double.valueOf(profile.getExpRetLatency()) && exp_prd > (1/lambda))
                            cache = true;
                    }
                }

                // Actual Caching
                if(cache) {
                    // Check available cache memory before caching
                    SQEMResponse response = sqemStub.cacheEntity(CacheRequest.newBuilder()
                            .setJson(context)
                            .setRefreshLogic(refPolicy)
                            // TODO: This cache life should be saved in the eviction registry
                            .setCachelife(est_cacheLife) // This is in miliseconds
                            .setReference(lookup).build());
                    return response.getStatus().equals("200") ? true : false;
                }

                // TODO:
                // Estimate the delay  (What is the lifetime is 0 or less than RetL?
                // Logging the delay time
                sqemStub.logDecisionLatency(DecisionLog.newBuilder()
                        .setLatency(est_cacheLife).setType("delay").build());
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

    private static double getCacheEfficiency(int size, double missRate, double retLatency) {
        double cacheCost = (size * cachePerfStats.get("costPerByte"))
                + cachePerfStats.get("processCost") * (((retLatency + cacheLookupLatency.get("400")) * missRate)
                + (cacheLookupLatency.get("200") * (1 - missRate)));
        double redirCost = cachePerfStats.get("processCost") * (retLatency + cacheLookupLatency.get("404"));

        return cacheCost / redirCost;
    }

    private static AbstractMap.SimpleEntry<Double,Double> getRetrievalEfficiency(String serviceId, ContextProviderProfile profile,
                                                 QueryClassProfile cqc_profile, String refPolicy, JSONObject slaObj){

        /** Initializing the variables **/
        double fthr = Double.valueOf(profile.getExpFthr());
        double lambda = Double.valueOf(profile.getExpAR()); // per second
        double rtmax = Double.valueOf(cqc_profile.getRtmax()); // seconds
        double retCost = Double.valueOf(profile.getExpCost());
        double penalty = Double.valueOf(cqc_profile.getPenalty());
        double retlatency = Double.valueOf(profile.getExpRetLatency()); // seconds
        double lifetime = slaObj.getJSONObject("freshness").getDouble("value"); // seconds
        double sampleInterval = slaObj.getJSONObject("updateFrequency").getDouble("value"); // seconds

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
                    .setLevel(CacheLevels.RAW_CONTEXT.toString().toLowerCase())
                    .setThreshold(rtmax - cacheLookupLatency.get("404")) // in seconds
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
        double hits = 0;
        double exp_mr = 0;

        switch(refPolicy){
            case "proactive_shift":
            case "reactive": {
                // Check whether all the values are of the same unit.
                double cache_penalties = 0;

                SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                        = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

                ProbDelay prob_delay = sqemStub.getProbDelay(ProbDelayRequest.newBuilder()
                        .setPrimaryKey(serviceId)
                        .setLevel(CacheLevels.RAW_CONTEXT.toString().toLowerCase())
                        .setThreshold(rtmax - cacheLookupLatency.get("400")) // in seconds
                        .build());

                if(sampleInterval == 0) {
                    double exp_prd = (lifetime - retlatency) * fthr;
                    hits = exp_prd * lambda;
                    exp_mr = 1/(hits + 1);

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
                        hits = exp_prd * lambda;
                        exp_mr = 1/(hits + 1);

                    }
                    else {
                        // Refreshing almost at the sampling rate
                        cache_cost = (retCost + cache_penalties) * (1/sampleInterval);
                        hits = sampleInterval * lambda;
                        exp_mr = 1/(hits + 1);
                    }
                }
                break;
            }
        }

        // Retrieval efficiency is the ratio between the cost of totally retrieving from the CPs in an adhoc manner
        // called the redirector mode, and the cost of serving from a cache
        return new AbstractMap.SimpleEntry(cache_cost / red_cost, exp_mr);
    }
}
