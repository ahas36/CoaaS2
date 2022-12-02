package au.coaas.cpree.executor.scheduler.jobs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.quartz.*;

import au.coaas.cpree.executor.RefreshExecutor;

import java.util.HashMap;
import java.util.logging.Logger;

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

                throw new RuntimeException("Couldn't retrieve the context for refreshing.");
            }
        }
        catch(Exception ex) {
            log.severe("Error retrieving: " + ex.getMessage());
        }
    }
}
