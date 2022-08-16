package au.coaas.cpree.executor;

import au.coaas.cpree.executor.scheduler.RefreshContext;
import au.coaas.cpree.executor.scheduler.RefreshScheduler;
import au.coaas.cpree.proto.ProactiveRefreshRequest;
import au.coaas.cpree.utils.enums.MeasuredProperty;
import au.coaas.cpree.utils.enums.RefreshLogics;
import au.coaas.cpree.utils.Utilities;

import au.coaas.sqem.proto.*;

import au.coaas.grpc.client.SQEMChannel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class RefreshExecutor {
    private static Logger log = Logger.getLogger(RefreshExecutor.class.getName());

    // TODO: this need be implemented with read, update, and delete
    private static HashMap<String,RefreshContext> prorefRegistery = new HashMap<>();

    private static final RefreshScheduler refreshScheduler = RefreshScheduler.getInstance();

    public static HashMap<String,RefreshContext> getSceduledRefreshes(){
        return prorefRegistery;
    }

    public static void setProactiveRefreshing(ProactiveRefreshRequest refreshRequest){
        try{
            SQEMServiceGrpc.SQEMServiceBlockingStub blockingStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

            SQEMResponse cpInfo = blockingStub.getContextServiceInfo(ContextServiceId.newBuilder()
                            .setId(refreshRequest.getRequest().getReference().getServiceId()).build());

            if(cpInfo.getStatus() == "200"){
                String contextId = refreshRequest.getRequest().getReference().getServiceId() + "-" + refreshRequest.getHashKey();
                RefreshContext refObject = new RefreshContext(contextId,
                        refreshRequest.getFthreh(),
                        refreshRequest.getRefreshPolicy(),
                        refreshRequest.getResiLifetime(),
                        refreshRequest.getLifetime(),
                        (HashMap<String, String>) refreshRequest.getRequest().getReference().getParamsMap(),
                        cpInfo.getBody(),
                        refreshRequest.getEt());

                prorefRegistery.put(contextId, refObject);
                refreshScheduler.scheduleRefresh(refObject);
            }
            throw new RuntimeException("Couldn't find the context provider by Id: " + cpInfo.getBody());
        }
        catch(Exception ex){
            log.severe("Could not refresh context!");
            log.info("Cause: " + ex.getMessage());
        }
    }

    public static void changeRefreshLogic(JSONObject sla, String cur_type, CacheLookUp lookup){
        // Based on the sampling nature, lifetime of the context,
        // switching to the correct refreshing algorithm
        RefreshLogics candidate = resolveRefreshLogic(sla, lookup.getServiceId(),
                Utilities.getHashKey(lookup.getParamsMap()), "NaN");

        if(!cur_type.equals(candidate.toString().toLowerCase())){
            Executors.newCachedThreadPool().execute(()
                    -> toggleRefreshLogic(RefreshUpdate.newBuilder()
                                .setRefreshLogic(candidate.toString().toLowerCase())
                                .setLookup(lookup)
                                .build()));
        }
    }

    private static Runnable toggleRefreshLogic(RefreshUpdate lookup){
        // TODO: Subscribe or Unsubscribe here
        SQEMServiceGrpc.SQEMServiceBlockingStub asyncStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        asyncStub.toggleRefreshLogic(lookup);
        return null;
    }

    public static RefreshLogics resolveRefreshLogic(JSONObject sla, String cpId, String hashKey, String profile){
        // Resolve the best cost-efficient refreshing logic based on lifetime and sampling technique.
        boolean autoFetch = sla.getBoolean("autoFetch");
        String life_unit = sla.getJSONObject("freshness").getString("unit");
        double lifetime = sla.getJSONObject("freshness").getDouble("value");
        double samplingInterval = sla.getJSONObject("updateFrequency").getDouble("value");

        double fthresh = 0.0;
        if(profile.startsWith("{")){
            JSONObject cp_prof = new JSONObject(profile);
            fthresh = cp_prof.getDouble("fthresh");
        }
        else
            fthresh = !profile.equals("NaN") ? Double.valueOf(profile) :
                    sla.getJSONObject("freshness").getDouble("fthresh");

        if(life_unit != sla.getJSONObject("updateFrequency").getString("unit")) {
            samplingInterval = Utilities.unitConverter(MeasuredProperty.TIME,
                    sla.getJSONObject("updateFrequency").getString("unit"), life_unit, samplingInterval);
        }

        if(autoFetch && samplingInterval == 0){
            return RefreshLogics.PROACTIVE_SHIFT;
        }

        if(samplingInterval > 0){
            // Context need to be refreshed based on validity.
            if(lifetime > samplingInterval) {
                if(samplingInterval == 0){
                    // CP samples adhoc for retrieval requests
                    // Therefore, the retrieval frequency can be best cost-optimized
                    // using Proactive Retrieval with Shifting (A. Medvedev, 2020)
                    return RefreshLogics.PROACTIVE_SHIFT;
                }

                double exp_prd = lifetime * (1-fthresh);
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

    public static void refreshContext(String contextId, String context){
        SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());

        RefreshContext refObj = prorefRegistery.get(contextId);

        JSONObject conSer = new JSONObject(refObj.getContextProvider());
        conSer.getJSONObject("sla").getJSONObject("freshness").put("fthresh", refObj.getfthresh());

        CacheLookUp.Builder lookup = CacheLookUp.newBuilder()
                .putAllParams(refObj.getParams())
                .setCheckFresh(true)
                .setEt(refObj.getEtype())
                .setServiceId(conSer.getJSONObject("_id").getString("$oid"));

        asyncStub.refreshContextEntity(CacheRefreshRequest.newBuilder()
                .setReference(lookup)
                .setJson(context).build());
    }
}
