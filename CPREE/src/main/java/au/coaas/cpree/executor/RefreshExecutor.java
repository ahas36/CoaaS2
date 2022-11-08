package au.coaas.cpree.executor;

import au.coaas.cpree.utils.Utilities;
import au.coaas.cpree.proto.CPREEResponse;
import au.coaas.cpree.utils.enums.CacheLevels;
import au.coaas.cpree.utils.enums.RefreshLogics;
import au.coaas.cpree.proto.ContextRefreshRequest;
import au.coaas.cpree.utils.enums.MeasuredProperty;
import au.coaas.cpree.proto.ProactiveRefreshRequest;
import au.coaas.cpree.executor.scheduler.RefreshContext;
import au.coaas.cpree.executor.scheduler.RefreshScheduler;

import au.coaas.sqem.proto.*;

import au.coaas.grpc.client.SQEMChannel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

public class RefreshExecutor {
    private static Logger log = Logger.getLogger(RefreshExecutor.class.getName());

    // Important registers
    private static HashMap<String,RefreshContext> prorefRegistery = new HashMap<>();
    private static HashSet<String> attemptRegistery = new HashSet<>();

    private static final RefreshScheduler refreshScheduler = RefreshScheduler.getInstance();

    // Routine
    public static void clearAttemptRegistery(){
        attemptRegistery.clear();
    }

    /** Adaptive Refreshing Functions */
    // Configure proactive refreshing when a context item is initially cached
    public static void setProactiveRefreshing(ProactiveRefreshRequest refreshRequest){
        try{
            SQEMServiceGrpc.SQEMServiceBlockingStub blockingStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

            SQEMResponse cpInfo = blockingStub.getContextServiceInfo(ContextServiceId.newBuilder()
                            .setId(refreshRequest.getRequest().getReference().getServiceId()).build());

            if(cpInfo.getStatus().equals("200")){
                String contextId = refreshRequest.getRequest().getReference().getServiceId() + "-" + refreshRequest.getHashKey();
                RefreshContext refObject = new RefreshContext(contextId,
                        refreshRequest.getFthreh(),
                        refreshRequest.getRefreshPolicy(),
                        refreshRequest.getResiLifetime(),
                        refreshRequest.getLifetime(),
                        refreshRequest.getRequest().getReference().getParamsMap(),
                        cpInfo.getBody(),
                        refreshRequest.getEt());

                synchronized (RefreshExecutor.class){
                    prorefRegistery.put(contextId, refObject);
                }
                refreshScheduler.scheduleRefresh(refObject);
            }
            else{
                throw new RuntimeException("Couldn't find the context provider by Id: "
                        + refreshRequest.getRequest().getReference().getServiceId());
            }
        }
        catch(Exception ex){
            log.severe("Could not set proactive refreshing for context!");
            log.info("Cause: " + ex.getMessage());
        }
    }

    // Setting up proactive refreshing when a context item transition from reactive to proactive
    private static void setProactiveRefreshing(ContextRefreshRequest request, String hashKey, double fthr,
                                               double resiLife, double lifetime) {
        try{
            SQEMServiceGrpc.SQEMServiceBlockingStub blockingStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

            SQEMResponse cpInfo = blockingStub.getContextServiceInfo(ContextServiceId.newBuilder()
                    .setId(request.getRequest().getReference().getServiceId()).build());

            if(cpInfo.getStatus() == "200"){
                String contextId = request.getRequest().getReference().getServiceId() + "-" + hashKey;
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
    public static void refreshContext(String contextId, String context){
        try {
            RefreshContext refObj = prorefRegistery.get(contextId);

            JSONObject conSer = new JSONObject(refObj.getContextProvider());
            conSer.getJSONObject("sla").getJSONObject("freshness").put("fthresh", refObj.getfthresh());

            CacheLookUp.Builder lookup = CacheLookUp.newBuilder()
                    .putAllParams(refObj.getParams())
                    .setCheckFresh(true)
                    .setEt(refObj.getEtype())
                    .setServiceId(conSer.getJSONObject("_id").getString("$oid"));

            SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                    = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());

            asyncStub.refreshContextEntity(CacheRefreshRequest.newBuilder()
                    .setReference(lookup)
                    .setJson(context).build());
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

            // Get Context Provider's Profile
            String hashKey = Utilities.getHashKey(request.getRequest().getReference().getParamsMap());
            String contextId = request.getRequest().getReference().getServiceId() + "-" + hashKey;

            if(attemptRegistery.contains(contextId))
                return null;

            SQEMServiceGrpc.SQEMServiceBlockingStub blockingStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

            ContextProviderProfile profile = blockingStub.getContextProviderProfile(ContextProfileRequest.newBuilder()
                    .setPrimaryKey(request.getRequest().getReference().getServiceId())
                    .setHashKey(hashKey)
                    .setLevel(CacheLevels.RAW_CONTEXT.toString().toLowerCase())
                    .build());

            if(profile.getStatus().equals("200")){
                RefreshLogics ref_type = RefreshExecutor.resolveRefreshLogic(
                        new JSONObject(request.getRequest().getSla()), profile, request.getRequest().getReference().getServiceId());

                boolean isChanged = !ref_type.toString().toLowerCase().equals(refPolicy);

                if(refPolicy.equals("proactive_shift")){
                    // Configuring refreshing
                    if(isChanged){
                        // Shift to the reactive policy
                        synchronized (RefreshExecutor.class){
                            // Remove the context item from the registry
                            prorefRegistery.remove(contextId);
                            // Stop the scheduler from running
                            refreshScheduler.stopRefreshing(contextId);
                            // Toggle the refresh policy in the Hashtable in SQEM
                            blockingStub.toggleRefreshLogic(RefreshUpdate.newBuilder()
                                    .setLookup(request.getRequest().getReference()).setRefreshLogic("reactive")
                                    .build());
                        }
                    }
                    else {
                        RefreshContext config = prorefRegistery.get(contextId);

                        double proffthr = profile.getExpFthr() != "NaN" ?
                                Double.valueOf(profile.getExpFthr()) : config.getfthresh();
                        if(config.getfthresh() != proffthr){
                            JSONObject freshReq = (new JSONObject(request.getRequest().getSla())).getJSONObject("freshness");
                            double res_life = freshReq.getDouble("value") - profile.getLastRetLatency();
                            // Reset the scheduler with the next lifetime
                            synchronized (RefreshExecutor.class){
                                // Update the context item from the registry
                                RefreshContext regiItem = prorefRegistery.get(contextId);
                                regiItem.setfthresh(proffthr, res_life);
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
                        // Shift to the reactive policy
                        synchronized (RefreshExecutor.class){
                            // Setup proactive refreshing
                            JSONObject sla = new JSONObject(request.getRequest().getSla());
                            double fthr = profile.getExpFthr() != "NaN" ?
                                    Double.valueOf(profile.getExpFthr()) :
                                    sla.getJSONObject("freshness").getDouble("fthresh");
                            double res_life = sla.getJSONObject("freshness").getDouble("value") - profile.getLastRetLatency();
                            double lifetime = sla.getJSONObject("freshness").getDouble("value");
                            setProactiveRefreshing(request, hashKey, fthr, res_life, lifetime);
                            // Toggle the refresh policy in the Hashtable in SQEM
                            blockingStub.toggleRefreshLogic(RefreshUpdate.newBuilder()
                                    .setLookup(request.getRequest().getReference()).setRefreshLogic("proactive_shift")
                                    .build());
                        }
                    }
                }

                SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                        = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                asyncStub.refreshContextEntity(request.getRequest());

                return CPREEResponse.newBuilder().setStatus("200").build();
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
    public static RefreshLogics resolveRefreshLogic(JSONObject sla, ContextProviderProfile profile, String serviceId){
        // Resolve the best cost-efficient refreshing logic based on lifetime and sampling technique.
        boolean autoFetch = sla.getBoolean("autoFetch");
        String life_unit = sla.getJSONObject("freshness").getString("unit");
        double lifetime = sla.getJSONObject("freshness").getDouble("value");
        double samplingInterval = sla.getJSONObject("updateFrequency").getDouble("value");
        double reliability = !profile.getRelaibility().equals("NaN") ? Double.valueOf(profile.getRelaibility()) : 0.5;

        double fthresh = 0.0;
        if(profile.getExpFthr().startsWith("{")){
            JSONObject cp_prof = new JSONObject(profile.getExpFthr());
            fthresh = cp_prof.getDouble("fthresh");
        }
        else
            fthresh = !profile.getExpFthr().equals("NaN") ? Double.valueOf(profile.getExpFthr()) :
                    sla.getJSONObject("freshness").getDouble("fthresh");

        if(life_unit != sla.getJSONObject("updateFrequency").getString("unit")) {
            samplingInterval = Utilities.unitConverter(MeasuredProperty.TIME,
                    sla.getJSONObject("updateFrequency").getString("unit"), life_unit, samplingInterval);
        }

        // TODO: There should be a better logic to resolve the reliability threshold
        if((autoFetch && samplingInterval == 0) || reliability < 0.2){
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

                double exp_prd = resi_lifetime * (1-fthresh);
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
}
