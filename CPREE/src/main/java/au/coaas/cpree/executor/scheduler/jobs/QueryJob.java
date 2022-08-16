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
                    fetchResponse = RetrievalManager.executeFetch(contextProvider, params);
                    break;
                case "proactive_shift":
                    fetchResponse = RetrievalManager.executeStreamRead(contextProvider, params);
            }

            if(fetchResponse != null){
                RefreshExecutor.refreshContext(contextId, fetchResponse);
            }

            throw new RuntimeException("Couldn't retrieve the context for refreshing.");
        }
        catch(Exception ex) {
            log.severe("Error retrieving: " + ex.getMessage());
        }
    }
}
