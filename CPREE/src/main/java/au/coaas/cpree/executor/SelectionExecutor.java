package au.coaas.cpree.executor;

import au.coaas.cpree.proto.*;
import au.coaas.cpree.proto.Empty;
import au.coaas.cpree.utils.LimitedQueue;
import au.coaas.cpree.utils.LookUps;
import au.coaas.cpree.utils.Utilities;
import au.coaas.cpree.utils.enums.CacheLevels;
import au.coaas.cpree.utils.enums.DynamicRegistry;
import au.coaas.cpree.utils.enums.RefreshLogics;

import au.coaas.sqem.proto.*;

import au.coaas.grpc.client.SQEMChannel;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.AbstractMap;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import com.google.common.util.concurrent.ListenableFuture;

public class SelectionExecutor {

    private static final Logger log = Logger.getLogger(SelectionExecutor.class.getName());

    // Static Queue
    private static LimitedQueue<Double> valueHistory = new LimitedQueue<>(1000);

    // Static Registries
    private static Hashtable<String, Double> weightThresholds = new Hashtable();
    private static Hashtable<String, Double> cachePerfStats = new Hashtable<>();
    private static Hashtable<String, Double> cacheLookupLatency = new Hashtable<>();

    private static ExecutorService executor = Executors.newScheduledThreadPool(20);

    private static void defaultWeights(){
        weightThresholds.put("kappa", 0.2);
        weightThresholds.put("mu", 0.2);
        weightThresholds.put("pi", 0.2);
        weightThresholds.put("delta", 0.2);
        weightThresholds.put("row", 0.2);
        weightThresholds.put("teta", 0.5);
        weightThresholds.put("threshold",  valueHistory.reverse(0.5));
    }

    public static Empty updateWeights(LearnedWeights request){
        weightThresholds.put("kappa", request.getKappa());
        weightThresholds.put("mu", request.getMu());
        weightThresholds.put("pi", request.getPi());
        weightThresholds.put("delta", request.getDelta());
        weightThresholds.put("row", request.getRow());
        weightThresholds.put("teta", request.getThreshold());
        weightThresholds.put("threshold",  valueHistory.reverse(request.getThreshold()));

        return null;
    }

    public static void updateCacheRecord(){
        SQEMServiceGrpc.SQEMServiceBlockingStub blockingStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        CachePerformance result = blockingStub.getcachePerformance(au.coaas.sqem.proto.Empty.newBuilder().build());

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
                // Evaluate and Cache if selected
                AbstractMap.SimpleEntry<Boolean, RefreshLogics> result = evaluateAndCache(request.getContext(),
                        request.getReference(), request.getSla(),profile, hashKey);

                if(result.getKey()){
                    // Configuring refreshing
                    executor.execute(() -> {
                        if (result.getValue().equals(RefreshLogics.PROACTIVE_SHIFT)) {
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
                                    .setRefreshPolicy(result.getValue().toString().toLowerCase())
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
            log.severe("Error occured when evaluating to cache!");
            log.info("Cause: " + ex.getMessage());
            return CPREEResponse.newBuilder().setStatus("500")
                    .setBody(ex.getMessage()).build();
        }
    }

    private static AbstractMap.SimpleEntry<Boolean, RefreshLogics> evaluateAndCache(String context, CacheLookUp lookup, String sla,
                             ContextProviderProfile profile, String hashkey) {
        try {
            boolean cache = false;

            if(cacheLookupLatency.isEmpty()) updateCacheRecord();

            RefreshLogics ref_type;
            String contextId = lookup.getServiceId() + "-" + hashkey;
            LocalDateTime curr = LocalDateTime.now();

            SQEMServiceGrpc.SQEMServiceFutureStub sqemStub
                    = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());

            ListenableFuture<ContextProfile> c_profile = sqemStub.getContextProfile(ContextProfileRequest.newBuilder()
                    .setPrimaryKey(contextId).build());

            double lambda = c_profile.get().getExpAR().equals("NaN") ? 1.0/60 : Double.valueOf(c_profile.get().getExpAR()); // per second

            // TODO:
            // Evaluate the context item for caching.
            // Since the lifetime of a context item is considered to be fixed at this phase of the implementation,
            // I can assume that the refreshing policy is also fixed.
            if(!profile.getAccessTrend().equals("NaN") &&
                    LookUps.lookup(DynamicRegistry.DELAYREGISTRY, contextId, curr) &&
                    LookUps.lookup(DynamicRegistry.INDEFDELAYREGISTRY, contextId, lambda)) {

                ref_type = RefreshExecutor.resolveRefreshLogic(new JSONObject(sla), profile, lookup.getServiceId());

                double lambda_conf = 0;
                long est_delayTime = 0;
                long est_cacheLife = -1;
                boolean indefinite = false;

                ListenableFuture<QueryClassProfile> cqc_profile = sqemStub.getQueryClassProfile(ContextProfileRequest.newBuilder()
                        .setPrimaryKey(lookup.getQClass()).build());

                double access_trend = c_profile.get().getTrend().equals("NaN") ?
                        0.0 : Double.valueOf(c_profile.get().getTrend());

                if(access_trend < 0){
                    // TODO: IMPORTANT!
                    // In the next line, it is assumed that there is only one class of context queries because, the
                    // implementation is only being tested for a single scenario (all queries are of the same class.)
                    // So, it is retrieving the stats from the performance summary for the query class of the "current query".
                    // This shouldn't be the case. I should rather retrieve the expected value considering all of CQ classes which the item is used
                    // from the context query class network.

                    if(cqc_profile.get().getStatus().equals("200")){
                        JSONObject slaObj = new JSONObject(sla);
                        double fthr = profile.getExpFthr().equals("NaN") ? Double.valueOf(profile.getExpFthr()) :
                                slaObj.getJSONObject("freshness").getDouble("fthresh");
                        double sampleInterval = slaObj.getJSONObject("updateFrequency").getDouble("value");
                        double lifetime = slaObj.getJSONObject("freshness").getDouble("value");
                        double intercept = c_profile.get().getIntercept().equals("NaN") ? 0 : Double.valueOf(c_profile.get().getIntercept());
                        double exp_prd = lifetime * (1 - fthr);

                        // The expiry period should be more than the Retrieval latency to not be stale by the time the item is retrieved.
                        // The expiry period should also be greater than the time between 2 access to the item in order to at least have > 0 chance of a hit.
                        if((sampleInterval == 0 && exp_prd > Double.valueOf(profile.getExpRetLatency()) && exp_prd > (1/lambda)) ||
                                (sampleInterval > 0 && ((sampleInterval > lifetime && sampleInterval > (1/lambda)) ||
                                        (sampleInterval <= lifetime && (sampleInterval + (lifetime - sampleInterval) * (1 - fthr)) > (1/lambda))))) {
                            // 1. Retrieval Efficiency
                            RetrievalEfficiency ret_effficiency = getRetrievalEfficiency(lookup.getServiceId(), profile, lambda,
                                    cqc_profile.get(), ref_type.toString().toLowerCase(), slaObj);
                            // 2. Reliability of retrieval
                            double reliability = profile.getRelaibility().equals("NaN") ?
                                    0.0 : Double.valueOf(profile.getRelaibility());
                            // 3. Caching Efficiency
                            int contextSize = context.getBytes().length;
                            double caching_efficiency = getCacheEfficiency(contextSize, ret_effficiency.getExpMR(),
                                    profile.getExpRetLatency().equals("NaN") ? profile.getLastRetLatency() :
                                            Double.valueOf(profile.getExpRetLatency()));
                            // 4. Query Complexity
                            // TODO: Need to calculate using the parse query tree
                            // Using 3.5 since it is the complexity of the currently tested 'Medium' complex query.
                            double query_complexity = 3.5;

                            if(weightThresholds.isEmpty()) defaultWeights();

                            double nonRetrievalConfidence = (weightThresholds.get("mu") * caching_efficiency) +
                                    (weightThresholds.get("kappa") * access_trend) +
                                    (weightThresholds.get("delta") * reliability) +
                                    (weightThresholds.get("row") * query_complexity);
                            double cacheConfidence = (weightThresholds.get("pi") * ret_effficiency.getEfficiecy()) +
                                    nonRetrievalConfidence;

                            valueHistory.add(cacheConfidence);

                            if(cacheConfidence > weightThresholds.get("threshold")){
                                cache = true;
                                // Solving AR for conf(i) = 0
                                if(weightThresholds.get("pi") > 0 && ret_effficiency.getType() != 1){
                                    // Type 1 is where lambda cancels out in the equation
                                    // If retrieval efficiency weight is not zero (if the parameter is important)
                                    double redRatio = -nonRetrievalConfidence/weightThresholds.get("pi");
                                    lambda_conf = ret_effficiency.getCacheCost()/(ret_effficiency.getRedCost() * redRatio);
                                }
                                est_cacheLife = lambda_conf < 0 ?
                                        Math.round(((-intercept)/access_trend) * 1000) :
                                        Math.round(((lambda_conf - intercept)/access_trend) * 1000);
                            }
                            else {
                                // Delay until the AR of the item is at least that could break-even the gain.
                                // Solving AR for conf(i) = 0
                                if(weightThresholds.get("pi") > 0){
                                    if(ret_effficiency.getType() != 1){
                                        // Type 1 is where lambda cancels out in the equation
                                        // If retrieval efficiency weight is not zero (if the parameter is important)
                                        double redRatio = -nonRetrievalConfidence/weightThresholds.get("pi");
                                        lambda_conf = ret_effficiency.getCacheCost()/(ret_effficiency.getRedCost() * redRatio);
                                    }
                                    else {
                                        double top = (ret_effficiency.getCacheCost()/ret_effficiency.getExpMR()) * weightThresholds.get("pi");
                                        double bot = ret_effficiency.getRedCost() * (-nonRetrievalConfidence);
                                        lambda_conf = ((top/bot) - 1) * (1 / ret_effficiency.getExpPrd());
                                    }
                                    // Indefinite delaying until the item reach a better AR
                                    indefinite = true;
                                    LookUps.write(DynamicRegistry.INDEFDELAYREGISTRY, contextId, lambda_conf);
                                }
                                else {
                                    est_delayTime = 60 * 1000; // default delay
                                    LookUps.write(DynamicRegistry.DELAYREGISTRY, contextId, curr.plus(est_delayTime, ChronoUnit.MILLIS)); // miliseconds
                                }
                            }
                        }
                    }
                }
                else {
                    // When the access trend is positive, the item would be cached for a longer period until evicted by
                    // the eviction algorithm.
                    if(cqc_profile.get().getStatus().equals("200")) {
                        JSONObject slaObj = new JSONObject(sla);
                        double fthr = profile.getExpFthr().equals("NaN") ? Double.valueOf(profile.getExpFthr()) :
                                slaObj.getJSONObject("freshness").getDouble("fthresh");
                        double sampleInterval = slaObj.getJSONObject("updateFrequency").getDouble("value");
                        double lifetime = slaObj.getJSONObject("freshness").getDouble("value");
                        double intercept = profile.getArIntercept().equals("NaN") ? 0 : Double.valueOf(profile.getArIntercept());
                        double exp_prd = lifetime * (1 - fthr);

                        // The expiry period should be more than the Retrieval latency to not be stale by the time the item is retrieved.
                        // The expiry period should also be greater than the time between 2 access to the item in order to at least have > 0 chance of a hit.
                        if((sampleInterval == 0 && exp_prd > Double.valueOf(profile.getExpRetLatency()) && exp_prd > (1/lambda)) ||
                                (sampleInterval > 0 && ((sampleInterval > lifetime && sampleInterval > (1/lambda)) ||
                                        (sampleInterval <= lifetime && (sampleInterval + (lifetime - sampleInterval) * (1 - fthr)) > (1/lambda))))) {
                            // 1. Retrieval Efficiency
                            RetrievalEfficiency ret_effficiency = getRetrievalEfficiency(lookup.getServiceId(), profile, lambda,
                                    cqc_profile.get(), ref_type.toString().toLowerCase(), slaObj);
                            // 2. Reliability of retrieval
                            double reliability = profile.getRelaibility().equals("NaN") ?
                                    0.0 : Double.valueOf(profile.getRelaibility());
                            // 3. Caching Efficiency
                            int contextSize = context.getBytes().length;
                            double caching_efficiency = getCacheEfficiency(contextSize, ret_effficiency.getExpMR(),
                                    profile.getExpRetLatency().equals("NaN") ? profile.getLastRetLatency() :
                                            Double.valueOf(profile.getExpRetLatency()));
                            // 4. Query Complexity
                            // TODO: Need to calculate using the parse query tree
                            // Using 3.5 since it is the complexity of the currently tested 'Medium' complex query.
                            double query_complexity = 3.5;

                            if(weightThresholds.isEmpty()) defaultWeights();

                            double nonRetrievalConfidence = (weightThresholds.get("mu") * caching_efficiency) +
                                    (weightThresholds.get("kappa") * access_trend) +
                                    (weightThresholds.get("delta") * reliability) +
                                    (weightThresholds.get("row") * query_complexity);
                            double cacheConfidence = (weightThresholds.get("pi") * ret_effficiency.getEfficiecy()) +
                                    nonRetrievalConfidence;

                            valueHistory.add(cacheConfidence);

                            if(cacheConfidence > weightThresholds.get("threshold")){
                                // No change to estimated lifetime.
                                // Wait for eviction at least until the AR is below a threshold.
                                cache = true;
                                indefinite = true;
                                if(weightThresholds.get("pi") > 0 && ret_effficiency.getType() != 1){
                                    // Type 1 is where lambda cancels out in the equation
                                    // If retrieval efficiency weight is not zero (if the parameter is important)
                                    double redRatio = -nonRetrievalConfidence/weightThresholds.get("pi");
                                    lambda_conf = ret_effficiency.getCacheCost()/(ret_effficiency.getRedCost() * redRatio);
                                }
                            }
                            else {
                                // Need to delay until the AR is such that it could at least break-even the cost.
                                if(weightThresholds.get("pi") > 0){
                                    if(ret_effficiency.getType() != 1){
                                        // Type 1 is where lambda cancels out in the equation
                                        // If retrieval efficiency weight is not zero (if the parameter is important)
                                        double redRatio = -nonRetrievalConfidence/weightThresholds.get("pi");
                                        lambda_conf = ret_effficiency.getCacheCost()/(ret_effficiency.getRedCost() * redRatio);
                                    }
                                    else {
                                        double top = (ret_effficiency.getCacheCost()/ret_effficiency.getExpMR()) * weightThresholds.get("pi");
                                        double bot = ret_effficiency.getRedCost() * (-nonRetrievalConfidence);
                                        lambda_conf = ((top/bot) - 1) * (1 / ret_effficiency.getExpPrd());
                                    }
                                    est_delayTime = Math.round(((lambda_conf - intercept)/access_trend) * 1000);
                                }
                                else {
                                    est_delayTime = 60 * 1000; // default delay
                                }
                                LookUps.write(DynamicRegistry.DELAYREGISTRY, contextId, curr.plus(est_delayTime, ChronoUnit.MILLIS)); // miliseconds
                            }
                        }

                        // The expiry period should be more than the Retrieval latency to not be stale by the time the item is retrieved.
                        // The expiry period should also be greater than the time between 2 access to the item in order to at least have > 0 chance of a hit.
                        // if((sampleInterval == 0 && exp_prd > Double.valueOf(profile.getExpRetLatency()) && exp_prd > (1/lambda)) ||
                        //         (sampleInterval > 0 && ((sampleInterval > lifetime && sampleInterval > (1/lambda)) ||
                        //                 (sampleInterval <= lifetime && (sampleInterval + (lifetime - sampleInterval) * (1 - fthr)) > (1/lambda)))))
                        //     cache = true;
                    }
                }

                // Actual Caching
                if(cache) {
                    // Check available cache memory before caching
                    ListenableFuture<SQEMResponse> response = sqemStub.cacheEntity(CacheRequest.newBuilder()
                            .setJson(context)
                            .setRefreshLogic(ref_type.toString().toLowerCase())
                            // TODO: This cache life should be saved in the eviction registry
                            .setCachelife(est_cacheLife) // This is in miliseconds
                            .setLambdaConf(lambda_conf)
                            .setIndefinite(indefinite)
                            .setReference(lookup).build());
                    return new AbstractMap.SimpleEntry<>(response.get().getStatus().equals("200") ? true : false,
                            ref_type);
                }

                // TODO:
                // Estimate the delay  (What is the lifetime is 0 or less than RetL? <-- check forcedRedirector)
                // Logging the delay time
                sqemStub.logDecisionLatency(DecisionLog.newBuilder()
                        .setLatency(est_delayTime).setLambdaConf(lambda_conf)
                        .setIndefinite(indefinite)
                        .setType("delay").build());
            }
        }
        catch(Exception ex){
            log.severe("Context Caching failed due to: " + ex.getMessage());
        }

        return new AbstractMap.SimpleEntry<>(false, null);
    }

    private static double getCacheEfficiency(int size, double missRate, double retLatency) {
        double cacheCost = (size * cachePerfStats.get("costPerByte"))
                + cachePerfStats.get("processCost") * (((retLatency + cacheLookupLatency.get("400")) * missRate)
                + (cacheLookupLatency.get("200") * (1 - missRate)));
        double redirCost = cachePerfStats.get("processCost") * (retLatency + cacheLookupLatency.get("404"));

        return cacheCost / redirCost;
    }

    private static RetrievalEfficiency getRetrievalEfficiency(String serviceId, ContextProviderProfile profile, double expLambda,
                                                 QueryClassProfile cqc_profile, String refPolicy, JSONObject slaObj){
        /** Initializing the variables **/
        double fthr = profile.getExpFthr().equals("NaN") ? Double.valueOf(profile.getExpFthr()) :
                slaObj.getJSONObject("freshness").getDouble("fthresh");
        double lambda = expLambda; // per second
        double rtmax = Double.valueOf(cqc_profile.getRtmax()); // seconds
        double retCost = Double.valueOf(profile.getExpCost());
        double penalty = Double.valueOf(cqc_profile.getPenalty());
        double retlatency = Double.valueOf(profile.getExpRetLatency()); // seconds
        double lifetime = slaObj.getJSONObject("freshness").getDouble("value"); // seconds
        double sampleInterval = slaObj.getJSONObject("updateFrequency").getDouble("value"); // seconds

        RetrievalEfficiency.Builder response = RetrievalEfficiency.newBuilder();

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
        response.setRedCost(red_ret_cost + red_penalties);

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
                    double exp_prd = (lifetime - retlatency) * (1 - fthr);
                    response.setExpPrd(exp_prd);
                    hits = exp_prd * lambda;
                    exp_mr = 1/(hits + 1);

                    // Penalties when needing to refresh
                    if(retlatency >= rtmax) cache_penalties = penalty;
                    else cache_penalties = (prob_delay.getValue()) * penalty;

                    cache_cost = (retCost + cache_penalties) * lambda * exp_mr;
                    response.setType(1);
                }
                else {
                    if(lifetime > sampleInterval){
                        // Refreshing at a slightly lower rate than the sampling
                        double exp_prd = sampleInterval + ((lifetime - sampleInterval) * (1 - fthr));
                        double ref_rate = 1 / exp_prd;

                        cache_cost = (retCost + cache_penalties) * ref_rate;
                        response.setType(2);
                        hits = exp_prd * lambda;
                        exp_mr = 1/(hits + 1);

                    }
                    else {
                        // Refreshing almost at the sampling rate
                        cache_cost = (retCost + cache_penalties) * (1/sampleInterval);
                        response.setType(3);
                        hits = sampleInterval * lambda;
                        exp_mr = 1/(hits + 1);
                    }
                }
                break;
            }
        }

        // Retrieval efficiency is the ratio between the cost of totally retrieving from the CPs in an adhoc manner
        // called the redirector mode, and the cost of serving from a cache
        return response.setEfficiecy(cache_cost / red_cost).setCacheCost(cache_cost).setExpMR(exp_mr).build();
    }
}
