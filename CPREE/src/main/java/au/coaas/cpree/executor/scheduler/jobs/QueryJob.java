package au.coaas.cpree.executor.scheduler.jobs;

import au.coaas.cpree.proto.SimpleContainer;
import au.coaas.cpree.utils.Utilities;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.cpree.utils.ConQEngHelper;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.ContextServiceId;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.quartz.*;
import org.json.JSONObject;

import au.coaas.cpree.executor.RefreshExecutor;

import java.util.AbstractMap;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.concurrent.Executors;

/**
 * @author shakthi
 */
public class QueryJob implements Job {

    private static Logger log = Logger.getLogger(QueryJob.class.getName());

    // Done
    public void execute(JobExecutionContext context) {
        try {
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();

            String contextId = dataMap.getString("contextId"); //entity-hashkey
            String fetchMode = dataMap.getString("fetchMode");
            String entityType = dataMap.getString("entityType");
            String contextProvider = dataMap.getString("contextProvider");
            HashMap<String,String> params = new Gson().fromJson(
                    dataMap.getString("params"),
                    new TypeToken<HashMap<String, String>>() {}.getType());

            AbstractMap.SimpleEntry<String, SimpleContainer> fetchResponse = null;
            JSONObject cs = new JSONObject(contextProvider);
            boolean autoFetch = cs.getJSONObject("sla").getBoolean("autoFetch");

            switch (fetchMode){
                case "reactive":
                case "proactive_shift":
                    if(autoFetch){
                        fetchResponse = RetrievalManager.executeStreamRead(contextProvider, params,
                                cs.getJSONObject("_id").getString("$oid"), entityType);
                    }
                    else {
                        fetchResponse = RetrievalManager.executeFetch(contextProvider, params, entityType);
                    }
                    break;
            }

            if(fetchResponse != null){
                RefreshExecutor.refreshContext(contextId, fetchResponse.getKey(),
                        cs.getJSONObject("_id").getString("$oid"), entityType, fetchResponse.getValue());
                log.info("Refreshed multiple context entities in cache.");
            }
            else {
                log.info("Failed to retrieve the context for " + contextId + " to refresh.");
                // At this point, the context cached seems to not be able to refresh. This means the context would remain stale if on cache.
                // The context data in costing the CMP 'hold up costs'. Because of which,
                // 1) The context should be evicted and all schedulers removed (for proactive refreshing).
                // 2) ConQEng should be updated of this nature and not to OR minimize attempting to retrieving from this CP.
                SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                        = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                asyncStub.evictContextByHashKey(ContextServiceId.newBuilder().setId(contextId).build());

                throw new RuntimeException("Couldn't retrieve the context for refreshing. Evicted the entity as a result.");
            }
        }
        catch(Exception ex) {
            log.severe("Error retrieving: " + ex.getMessage());
        }
    }
}
