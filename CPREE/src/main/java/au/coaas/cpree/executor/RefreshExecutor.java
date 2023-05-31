package au.coaas.cpree.executor;

import au.coaas.cpree.proto.*;
import au.coaas.cpree.proto.Empty;
import au.coaas.cpree.utils.Utilities;
import au.coaas.cpree.utils.enums.CacheLevels;
import au.coaas.cpree.utils.enums.RefreshLogics;
import au.coaas.cpree.utils.enums.MeasuredProperty;
import au.coaas.cpree.executor.scheduler.RefreshContext;
import au.coaas.cpree.executor.scheduler.RefreshScheduler;

import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.sqem.proto.*;

import au.coaas.grpc.client.SQEMChannel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.SchedulerException;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class RefreshExecutor {
    private static Logger log = Logger.getLogger(RefreshExecutor.class.getName());

    // Important registers
    private static HashMap<String,RefreshContext> prorefRegistery = new HashMap<>();
    private static HashSet<String> attemptRegistery = new HashSet<>();

    private static final RefreshScheduler refreshScheduler = RefreshScheduler.getInstance();
    private static ExecutorService executor = Executors.newScheduledThreadPool(40);

    // Routine
    public static void clearAttemptRegistery(){
        attemptRegistery.clear();
    }

    /** Adaptive Refreshing Functions */
    // Configure proactive refreshing when a context item is initially cached
    // Done
    public static void setProactiveRefreshing(ProactiveRefreshRequest refreshRequest){
        try{
            SQEMServiceGrpc.SQEMServiceBlockingStub blockingStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

            SQEMResponse cpInfo = blockingStub.getContextServiceInfo(ContextServiceId.newBuilder()
                            .setId(refreshRequest.getReference().getServiceId()).build());

            if(cpInfo.getStatus().equals("200")){
                String contextId = refreshRequest.getReference().getEt().getType() + "-" + refreshRequest.getHashKey();
                RefreshContext refObject = new RefreshContext(contextId,
                        refreshRequest.getFthreh(),
                        refreshRequest.getRefreshPolicy(),
                        refreshRequest.getResiLifetime(),
                        refreshRequest.getLifetime(),
                        refreshRequest.getReference().getParamsMap(),
                        cpInfo.getBody(),
                        refreshRequest.getEt());

                synchronized (RefreshExecutor.class){
                    prorefRegistery.put(contextId, refObject);
                }
                refreshScheduler.scheduleRefresh(refObject);
            }
            else{
                throw new RuntimeException("Couldn't find the context provider by Id: "
                        + refreshRequest.getReference().getServiceId());
            }
        }
        catch(Exception ex){
            log.severe("Could not set proactive refreshing for context!");
            log.info("Cause: " + ex.getMessage());
        }
    }

    // Setting up proactive refreshing when a context item transition from reactive to proactive
    private static void setProactiveRefreshing(ContextRefreshRequest request, String hashKey, double fthr,
                                               double resiLife, double lifetime, List<String> attributes) {
        try{
            SQEMServiceGrpc.SQEMServiceBlockingStub blockingStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

            SQEMResponse cpInfo = blockingStub.getContextServiceInfo(ContextServiceId.newBuilder()
                    .setId(request.getRequest().getReference().getServiceId()).build());

            if(cpInfo.getStatus().equals("200")){
                String contextId = request.getRequest().getReference().getEt().getType() + "-" + hashKey;
                RefreshContext refObject = new RefreshContext(contextId,
                        fthr, request.getRefreshPolicy(),
                        resiLife, lifetime,
                        request.getRequest().getReference().getParamsMap(),
                        cpInfo.getBody(),
                        request.getRequest().getReference().getEt());

                synchronized (RefreshExecutor.class) {
                    prorefRegistery.put(contextId, refObject);
                }
                refreshScheduler.scheduleRefresh(refObject);
            }
            else {
                throw new RuntimeException("Couldn't find the context provider by Id: "
                        + request.getRequest().getReference().getServiceId());
            }
        }
        catch(Exception ex){
            log.severe("Could not set to proactively refresh context!");
            log.info("Cause: " + ex.getMessage());
        }
    }

    // Refresh context for proactive refreshing with shift when automatically fetched
    public static void refreshContext(String initContextId, String contextString, String providerId,
                                      String entityType, SimpleContainer meta){
        try {
            JSONObject context = new JSONObject(contextString);
            JSONArray entityContext = context.getJSONArray("results");
            CacheLookUp.Builder lookup = CacheLookUp.newBuilder()
                    .setCheckFresh(true).setServiceId(providerId);

            for(int i=0; i<entityContext.length(); i++){
                // If the entity was set to proactively refreshed.
                JSONObject entData = entityContext.optJSONObject(i);
                String hashkey = entData.has("hashkey") ? entData.getString("hashkey") :
                        meta.getHashKeysList().get(i);
                RefreshLogics refPolicy = RefreshLogics.PROACTIVE_SHIFT;

                if(prorefRegistery.get(entityType +"-"+ hashkey) != null) {
                    RefreshContext refObj = prorefRegistery.get(entityType +"-"+ hashkey);
                    lookup.setHashKey(hashkey);
                    lookup.setEt(refObj.getEtype());

                    if(hashkey != (initContextId.split("-"))[1]){
                        // Should reset the next scheduled retrieval for the entity.
                        double residual_life = 0.0;
                        if(entData.has("age")){
                            residual_life = meta.getFreshness() - (meta.getRetLatMilis()/1000.0)
                                    - entData.getJSONObject("age").getDouble("value");
                        }
                        else {
                            residual_life = meta.getFreshness() - (meta.getRetLatMilis()/1000.0);
                        }

                        // Reset the scheduler to the next lifetime
                        synchronized (RefreshExecutor.class){
                            // Update the context item from the registry
                            refObj.setInitInterval(residual_life);
                            prorefRegistery.put(entityType +"-"+ hashkey, refObj);
                            // Update the scheduler in running
                            refreshScheduler.updateRefreshing(refObj);
                        }
                    }
                }
                else {
                    lookup.setHashKey(hashkey);
                    lookup.setEt(ContextEntityType.newBuilder().setType(entityType).build());
                }

                SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                        = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());

                asyncStub.refreshContextEntity(CacheRefreshRequest.newBuilder()
                        .setReference(lookup)
                        .setRefPolicy(refPolicy.toString().toLowerCase())
                        .setJson(entData.toString()).build());
            }
        }
        catch(Exception ex){
            log.severe("Error occured when refreshing: " + ex.getMessage());
        }
    }

    // Refresh context for both Reactive refreshing and when forcing a Shift in proactive retrieval
    // Toggling the refresh policy
    public static CPREEResponse refreshContext(ContextRefreshRequest request) {
        try {
            String refPolicy = request.getRefreshPolicy();
            // Above can be null when the at least one of the entities are invalid.

            // Get Context Provider's Profile
            String hashKey = request.getHashKey();
            String contextId = request.getRequest().getReference().getEt().getType() + "-" + hashKey;

            if(attemptRegistery.contains(contextId))
                return null;

            SQEMServiceGrpc.SQEMServiceBlockingStub blockingStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

            // Get the performance of a context provider when retrieving a specific entity.
            ContextProviderProfile profile = blockingStub.getContextProviderProfile(ContextProfileRequest.newBuilder()
                    .setPrimaryKey(request.getRequest().getReference().getServiceId())
                    .setHashKey(hashKey)
                    .setLevel(CacheLevels.ENTITY.toString().toLowerCase())
                    .build());

            if(profile.getStatus().equals("200")){
                JSONObject lifetime = Utilities.getLifetime(request.getRequest().getReference().getEt().getType());
                RefreshLogics ref_type = RefreshExecutor.resolveRefreshLogic(
                        new JSONObject(request.getRequest().getSla()), profile, lifetime.getDouble("value"));

                if(refPolicy != null){
                    // RefPolicy is not null when,
                    // 1. The context was specifically looked up using a hash key and that partially missed.
                    // 2. The context was looked up using parameters that were also it's ID keys.
                    // So, this is true when cache look up occurred for one entity only.
                    boolean isChanged = !ref_type.toString().toLowerCase().equals(refPolicy);

                    if(refPolicy.equals("proactive_shift")){
                        // Configuring refreshing
                        if(isChanged){
                            // Shift to the reactive policy
                            synchronized (RefreshExecutor.class){
                                stopProactiveRefreshing(contextId);
                                // Toggle the refresh policy in the Hashtable in SQEM
                                blockingStub.toggleRefreshLogic(RefreshUpdate.newBuilder()
                                        .setLookup(request.getRequest().getReference())
                                        .setRefreshLogic("reactive")
                                        .setHashkey(hashKey)
                                        .build());
                            }
                        }
                        else {
                            RefreshContext config = prorefRegistery.get(contextId);

                            double proffthr = profile.getExpFthr() != "NaN" ?
                                    Double.valueOf(profile.getExpFthr()) : config.getfthresh();
                            if(config.getfthresh() != proffthr){
                                // JSONObject freshReq = (new JSONObject(request.getRequest().getSla())).getJSONObject("freshness");

                                double residual_life = 0.0;
                                JSONObject context = new JSONObject(request.getRequest().getJson());
                                if(context.has("age")){
                                    residual_life = lifetime.getDouble("value") - profile.getLastRetLatency()
                                            - context.getJSONObject("age").getDouble("value");
                                }
                                else {
                                    residual_life = lifetime.getDouble("value") - profile.getLastRetLatency();
                                }

                                // Reset the scheduler with the next lifetime
                                synchronized (RefreshExecutor.class){
                                    // Update the context item from the registry
                                    RefreshContext regiItem = prorefRegistery.get(contextId);
                                    regiItem.setfthresh(proffthr, residual_life);
                                    prorefRegistery.put(contextId,regiItem);
                                    // Update the scheduler in running
                                    refreshScheduler.updateRefreshing(regiItem);
                                }
                            }
                        }

                        // TODO: Updating the scheduler trigger when lifetime changes
                        // Need to update the trigger when the lifetime expected for the context item
                        // is different to that has been used to configure the proactive refreshing.

                    }
                    else {
                        // If the current policy is reactive
                        if(isChanged){
                            // Shift to the proactive policy
                            synchronized (RefreshExecutor.class){
                                // Setup proactive refreshing
                                JSONObject sla = new JSONObject(request.getRequest().getSla());
                                double fthr = profile.getExpFthr() != "NaN" ?
                                        Double.valueOf(profile.getExpFthr()) :
                                        sla.getJSONObject("freshness").getDouble("fthresh");
                                double res_life = lifetime.getDouble("value") - profile.getLastRetLatency();

                                CacheRefreshRequest test = request.getRequest();
                                test.toBuilder().setRefPolicy("proactive_shift");
                                ContextRefreshRequest new_request = request.toBuilder()
                                        .setRequest(test).setRefreshPolicy("proactive_shift").build();

                                setProactiveRefreshing(new_request, hashKey, fthr, res_life,
                                        lifetime.getDouble("value"), request.getAttributesList());
                                // Toggle the refresh policy in the Hashtable in SQEM
                                blockingStub.toggleRefreshLogic(RefreshUpdate.newBuilder()
                                        .setHashkey(hashKey)
                                        .setLookup(request.getRequest().getReference()).setRefreshLogic("proactive_shift")
                                        .build());
                            }
                        }
                    }

                    SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                            = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                    asyncStub.refreshContextEntity(CacheRefreshRequest.newBuilder()
                                    .setJson(request.getRequest().getJson())
                                    .setRefPolicy(refPolicy)
                                    .setReference(CacheLookUp.newBuilder()
                                            .setEt(request.getRequest().getReference().getEt())
                                            .setServiceId(request.getRequest().getReference().getServiceId())
                                            .setUniformFreshness(request.getRequest().getReference().getUniformFreshness())
                                            .setSamplingInterval(request.getRequest().getReference().getSamplingInterval())
                                            .setCheckFresh(request.getRequest().getReference().getCheckFresh())
                                            .setKey(request.getRequest().getReference().getKey())
                                            .setQClass(request.getRequest().getReference().getQClass())
                                            .setHashKey(hashKey))
                                    .setObservedTime(request.getRequest().getObservedTime())
                                    .setSla(request.getRequest().getSla())
                                    .build());

                    return CPREEResponse.newBuilder().setStatus("200").build();
                }
                else {
                    // Doesn't contain a refresh policy at the moment.
                    // This occurs when the cache lookup was made using parameters, not equal to the ID keys.
                    // Some of the entities can come could already be cached, and some not.
                    // Those not cached - should be evaluated.
                    // Those cached - 1. Check what the refreshing policy is and then,
                    //                  1.1. If proactive, reschedule the next retrieval
                    //                  1.2. If reactive, just refresh in cache only
                    // Those not cached - 2. evaluate for caching.
                    SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                            = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
                    SQEMResponse response = sqemStub.simpleLookup(CacheLookUp.newBuilder()
                                    .setEt(request.getRequest().getReference().getEt())
                                    .setCheckFresh(false)
                                    .setServiceId(request.getRequest().getReference().getServiceId())
                                    .setHashKey(hashKey)
                                    .build());

                    CacheLookUpResponse lookupResponse = response.getCacheresponse();
                    if(lookupResponse.getIsCached()){
                        // Need to refresh and/or reschedule.
                        // Here, the refreshing logic won't be changed because the refresh was triggered by a
                        // reactive retriveal that as a result of having data pertaining to other entities are also being refreshed.
                        refPolicy = lookupResponse.getRefreshLogic().toLowerCase();
                        if(!refPolicy.equals("reactive")){
                            // When proactive refreshing is employed,
                            // need to refresh and reschedule.
                            JSONObject freshReq = (new JSONObject(request.getRequest().getSla())).getJSONObject("freshness");

                            double residual_life = 0.0;
                            JSONObject context = new JSONObject(request.getRequest().getJson());
                            if(context.has("age")){
                                residual_life = freshReq.getDouble("value") - profile.getLastRetLatency()
                                        - context.getJSONObject("age").getDouble("value");
                            }
                            else {
                                residual_life = freshReq.getDouble("value") - profile.getLastRetLatency();
                            }

                            // Reset the scheduler with the next lifetime
                            synchronized (RefreshExecutor.class){
                                // Update the context item from the registry
                                RefreshContext regiItem = prorefRegistery.get(contextId);
                                regiItem.setInitInterval(residual_life);
                                prorefRegistery.put(contextId,regiItem);
                                // Update the scheduler in running
                                refreshScheduler.updateRefreshing(regiItem);
                            }
                        }

                        SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                                = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());

                        asyncStub.refreshContextEntity(CacheRefreshRequest.newBuilder()
                                .setJson(request.getRequest().getJson())
                                .setRefPolicy(refPolicy)
                                .setReference(request.getRequest().getReference().toBuilder().setHashKey(hashKey))
                                .setObservedTime(request.getRequest().getObservedTime())
                                .setSla(request.getRequest().getSla())
                                .build());
                    }
                    else {
                        // Need to evaluate for caching.
                        AbstractMap.SimpleEntry<Boolean, RefreshLogics> result = SelectionExecutor
                                .evaluateAndCache(request.getRequest().getJson(),
                                request.getRequest().getReference(),
                                request.getRequest().getSla(),
                                profile, hashKey,
                                request.getComplexity());

                        if(result.getKey()){
                            // Configuring refreshing
                            if (result.getValue().equals(RefreshLogics.PROACTIVE_SHIFT)) {
                                JSONObject freshReq = (new JSONObject(request.getRequest().getSla()))
                                        .getJSONObject("freshness");
                                JSONObject sampling = (new JSONObject(request.getRequest().getSla()))
                                        .getJSONObject("updateFrequency");
                                double fthresh = !profile.getExpFthr().equals("NaN") ?
                                        Double.valueOf(profile.getExpFthr()) :
                                        freshReq.getDouble("fthresh");

                                JSONObject entData = new JSONObject(request.getRequest().getJson());
                                long zeroTime = entData.getLong("zeroTime");
                                long now = System.currentTimeMillis();
                                double res_life =  lifetime.getDouble("value") - (now - zeroTime);

                                setProactiveRefreshing(ProactiveRefreshRequest.newBuilder()
                                        .setEt(request.getRequest().getReference().getEt())
                                        .setReference(request.getRequest().getReference())
                                        .setFthreh(fthresh)
                                        .setLifetime(lifetime.getDouble("value")) // seconds
                                        .setResiLifetime(res_life) // seconds
                                        .setHashKey(hashKey)
                                        .setSamplingInterval(sampling.getDouble("value")) // seconds
                                        .setRefreshPolicy(result.getValue().toString().toLowerCase())
                                        .build());
                            }
                        }
                    }
                    return CPREEResponse.newBuilder().setStatus("200").build();
                }
            }
            else
                return CPREEResponse.newBuilder().setStatus(profile.getStatus())
                        .setBody("Failed to fetch context provider profile.").build();
        }
        catch (Exception ex) {
            log.severe("Could not refresh context!");
            log.info("Cause: " + ex.getMessage());
            return CPREEResponse.newBuilder().setStatus("500")
                    .setBody(ex.getMessage()).build();
        }
    }

    /** Refreshing Utility */
    // Resolving what the most efficient refreshing policy for the context item
    // Done
    public static RefreshLogics resolveRefreshLogic(JSONObject sla, ContextProviderProfile profile, double lifetime){
        // Resolve the best cost-efficient refreshing logic based on lifetime and sampling technique.
        String life_unit = "s";
        boolean autoFetch = sla.getBoolean("autoFetch");
        double samplingInterval = sla.getJSONObject("updateFrequency").getDouble("value");
        double reliability = !profile.getRelaibility().equals("NaN") ? Double.valueOf(profile.getRelaibility()) : 0.5;

        double fthresh = 0.0;
        if(profile.getExpFthr().startsWith("{")){
            JSONObject cp_prof = new JSONObject(!profile.getExpFthr().equals("NaN") ? profile.getExpFthr() : 0.7);
            fthresh = cp_prof.getDouble("fthresh");
        }
        else
            fthresh = !profile.getExpFthr().equals("NaN") ? Double.valueOf(profile.getExpFthr()) :
                    sla.getJSONObject("freshness").getDouble("fthresh");

        if(!life_unit.equals(sla.getJSONObject("updateFrequency").getString("unit"))) {
            samplingInterval = Utilities.unitConverter(MeasuredProperty.TIME,
                    sla.getJSONObject("updateFrequency").getString("unit"), life_unit, samplingInterval);
        }

        // TODO: There should be a better logic to resolve the reliability threshold
        if((!autoFetch && samplingInterval == 0) || reliability < 0.2){
            return RefreshLogics.PROACTIVE_SHIFT;
        }

        // The residual lifetime is calculated using the expected retrieval lifetime
        // E[RetL] = Total time spent on retrievals / Number of successful retrievals
        // Therefore, it probabilistically incoperates the reliability/unreliability of the context provider
        double exp_retl = !profile.getExpRetLatency().equals("NaN") ?
                    Double.valueOf(profile.getExpRetLatency()) : profile.getLastRetLatency();
        double resi_lifetime = lifetime - exp_retl;

        if(samplingInterval > 0){
            // Context need to be refreshed based on validity.
            if(resi_lifetime > samplingInterval) {
                if(samplingInterval == 0){
                    // CP samples adhoc for retrieval requests
                    // Therefore, the retrieval frequency can be best cost-optimized
                    // using Proactive Retrieval with Shifting (A. Medvedev, 2020)
                    return RefreshLogics.PROACTIVE_SHIFT;
                }

                double exp_prd = resi_lifetime * (1.0-fthresh);
                if(exp_prd >= samplingInterval){
                    // When expiry period is longer than the sampling interval,
                    // context can still be proactively retrieved.
                    return RefreshLogics.PROACTIVE_SHIFT;
                }
            }
        }
        // Else, context are either,
        // (1) Pushed into CoaaS via a data stream. In this case, the item should be fetched from the persistent storage into the cache.
        // (2) Sampling Interval is greater or equal to the lifetime
        // (3) Sampling interval is shorter than the lifetime, but the expiry period is shorter which creates an invalid period
        // For (2) and (3), the item should rather be fetched as and when needed for queries.

        return RefreshLogics.REACTIVE;
    }

    // Done
    public static Empty stopProactiveRefreshing(String contextId) throws SchedulerException {
        // Remove the context item from the registry
        prorefRegistery.remove(contextId);
        // Stop the scheduler from running
        refreshScheduler.stopRefreshing(contextId);
        return null;
    }
}
