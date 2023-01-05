package au.coaas.cpree.executor.scheduler.jobs;

import au.coaas.grpc.client.SQEMChannel;
import au.coaas.cpree.utils.ConQEngHelper;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.ContextServiceId;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.quartz.*;
import org.json.JSONObject;

import au.coaas.cpree.executor.RefreshExecutor;

import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.concurrent.Executors;

public class QueryJob implements Job {

    private static Logger log = Logger.getLogger(QueryJob.class.getName());

    public void execute(JobExecutionContext context) {
        try {
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();

            String contextId = dataMap.getString("contextId");
            String fetchMode = dataMap.getString("fetchMode");
            String contextProvider = dataMap.getString("contextProvider");
            HashMap<String,String> params = new Gson().fromJson(
                    dataMap.getString("params"),
                    new TypeToken<HashMap<String, String>>() {}.getType());

            SimpleTrigger trigger = (SimpleTrigger) context.getTrigger();

            // Periodically checking if priority has changed for the context provider.
            if(trigger.getTimesTriggered() % 10 == 0){
                Executors.newCachedThreadPool().execute(() -> {
                    JSONObject conqEngSort = new JSONObject();
                    conqEngSort.put("clatitude", "x");
                    conqEngSort.put("clongitude", "y");
                    conqEngSort.put("cetype", dataMap.getString("entityType"));
                    // Not sure whether this next line would actually work.
                    conqEngSort.put("cCa", (List<String>) dataMap.get("attributes"));

                    conqEngSort.put("pid", dataMap.getString("cpId"));

                    // The following are example SLA values given to ConQEng as reference.
                    JSONObject fcp = new JSONObject(contextProvider);
                    JSONObject cpQoS = fcp.getJSONObject("sla").getJSONObject("qos");

                    conqEngSort.put("cost", cpQoS.getDouble("rate"));
                    conqEngSort.put("ctimeliness", cpQoS.getDouble("rtmax"));
                    conqEngSort.put("pen_timeliness", cpQoS.getDouble("penPct"));

                    if(!ConQEngHelper.verifyCPOrder(conqEngSort)) {
                        log.info("Context Provider priority has changed for " + contextId + ". Evicting for updated priority.");
                        SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                                = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                        String hashkey = (contextId.split("-"))[1];
                        asyncStub.evictContextEntityByHashKey(ContextServiceId.newBuilder().setId(hashkey).build());
                    }
                });
            }

            String fetchResponse = null;
            switch (fetchMode){
                case "reactive":
                case "proactive_shift":
                    fetchResponse = RetrievalManager.executeFetch(contextProvider, params);
                    // TODO: Need to refresh from the stream or the Mongo DB as well
                    // fetchResponse = RetrievalManager.executeStreamRead(contextProvider, params);
                    break;
            }

            if(fetchResponse != null){
                RefreshExecutor.refreshContext(contextId, fetchResponse);
                log.info("Refreshed " + contextId + " in context cache.");
            }
            else {
                log.info("Failed to retrieve the context for " + contextId + " to refresh.");
                // At this point, the context cached seems to not be able to refresh. This means the context would remain stale if on cache.
                // The context data in costing the CMP 'hold up costs'. Because of which,
                // 1) The context should be evicted and all schedulers removed (for proactive refreshing).
                // 2) ConQEng should be updated of this nature and not to OR minimize attempting to retrieving from this CP.
                SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                        = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                String hashkey = (contextId.split("-"))[1];
                asyncStub.evictContextEntityByHashKey(ContextServiceId.newBuilder().setId(hashkey).build());

                // Sending feedback of unavailable CP to ConQEng
                Executors.newCachedThreadPool().execute(() -> {
                    JSONObject conqEngFB = new JSONObject();
                    conqEngFB.put("cctype", "AC");
                    conqEngFB.put("latitude", "x");
                    conqEngFB.put("longitude", "y");
                    conqEngFB.put("ccid", (contextId.split("-"))[0]);
                    conqEngFB.put("etype", dataMap.getString("entityType"));
                    // Not sure whether this next line would actually work.
                    conqEngFB.put("Ca", (List<String>) dataMap.get("attributes"));

                    conqEngFB.put("pid", dataMap.getString("cpId"));

                    JSONObject fcp = new JSONObject(contextProvider);
                    JSONObject cpQoS = fcp.getJSONObject("sla").getJSONObject("qos");

                    conqEngFB.put("price", cpQoS.getDouble("rate"));
                    conqEngFB.put("timeliness", cpQoS.getDouble("rtmax"));
                    // This value indicates the CP is currently unavailable.
                    conqEngFB.put("RRunit", 0);

                    ConQEngHelper.reportFeedback(conqEngFB);
                });

                throw new RuntimeException("Couldn't retrieve the context for refreshing. Evicted the entity as a result.");
            }
        }
        catch(Exception ex) {
            log.severe("Error retrieving: " + ex.getMessage());
        }
    }
}
