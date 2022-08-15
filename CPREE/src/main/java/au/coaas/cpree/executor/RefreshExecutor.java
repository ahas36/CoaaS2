package au.coaas.cpree.executor;

import au.coaas.cpree.executor.scheduler.RefreshContext;
import au.coaas.cpree.executor.scheduler.RefreshScheduler;
import au.coaas.cpree.utils.enums.MeasuredProperty;
import au.coaas.cpree.utils.enums.RefreshLogics;
import au.coaas.cpree.utils.Utilities;

import au.coaas.sqem.proto.CacheLookUp;
import au.coaas.sqem.proto.CacheRefreshRequest;
import au.coaas.sqem.proto.RefreshUpdate;
import au.coaas.sqem.proto.SQEMServiceGrpc;

import au.coaas.grpc.client.SQEMChannel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class RefreshExecutor {
    private static Logger log = Logger.getLogger(RefreshExecutor.class.getName());

    private static final RefreshScheduler refreshScheduler = RefreshScheduler.getInstance();

    public static void setProactiveRefreshing(String contextId, double residualLife, double fthr){
        try{
            double exp_prd = residualLife * ( 1 - fthr);
            RefreshContext refObject = new RefreshContext(contextId, exp_prd);
            refreshScheduler.scheduleRefresh(refObject);
        }
        catch(Exception ex){
            log.severe("Could not refresh context!");
        }
    }

    public static void changeRefreshLogic(JSONObject sla, String cur_type, CacheLookUp lookup){
        // Based on the sampling nature, lifetime of the context,
        // switching to the correct refreshing algorithm
        RefreshLogics candidate = resolveRefreshLogic(sla, lookup.getServiceId(),
                Utilities.getHashKey(lookup.getParamsMap()));

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

    public static RefreshLogics resolveRefreshLogic(JSONObject sla, String cpId, String hashKey){
        // Resolve the best cost-efficient refreshing logic based on lifetime and sampling technique.
        boolean autoFetch = sla.getBoolean("autoFetch");
        String life_unit = sla.getJSONObject("freshness").getString("unit");
        double lifetime = sla.getJSONObject("freshness").getDouble("value");
        // This should be the expected fthresh
        double fthresh = sla.getJSONObject("freshness").getDouble("fthresh");
        double samplingInterval = sla.getJSONObject("updateFrequency").getDouble("value");

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

    public static void refreshContext(String context, String contextProvider, HashMap<String, String> params){
        // TODO: Implement Logic

        SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
        asyncStub.refreshContextEntity(CacheRefreshRequest.newBuilder()
                // .setSla(sla.toString())
                // .setReference(lookup)
                .setJson(context).build());
    }
}
