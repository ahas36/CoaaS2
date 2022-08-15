package au.coaas.cpree.executor.scheduler.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import au.coaas.cpree.executor.RefreshExecutor;

import java.util.HashMap;
import java.util.logging.Logger;

public class QueryJob implements Job {

    private static Logger log = Logger.getLogger(QueryJob.class.getName());

    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String fetchMode = dataMap.getString("fetchMode");
        String contextProvider = dataMap.getString("contextProvider");
        HashMap<String,String> params = (HashMap<String,String>) dataMap.get("params");

        String fetchResponse = null;
        switch (fetchMode){
            case "reactive":
                fetchResponse = RetrievalManager.executeFetch(contextProvider, params);
                break;
            case "proactive_shift":
                fetchResponse = RetrievalManager.executeStreamRead(contextProvider, params);
        }

        // TODO:
        // Refresh the item in cache and toggle if nessecary
        // RefreshExecutor.refreshContext(fetchResponse, contextProvider, params);
        // Schedule the next refrehsing operation.
    }
}
