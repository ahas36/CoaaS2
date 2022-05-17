package Jobs;

import Utils.HttpHandler;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.time.LocalDateTime;
import java.util.logging.Logger;

public class QueryJob implements Job {

    private static Logger log = Logger.getLogger(QueryJob.class.getName());

    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String contextQuery = dataMap.getString("query");
        String queryId = dataMap.getString("query-id");
        String token = dataMap.getString("token");

        HttpHandler.makeContextQuery(contextQuery, token);

        LocalDateTime time = LocalDateTime.now();
        log.info("Made the context query: " + queryId + " at " + time.toString());
    }
}
