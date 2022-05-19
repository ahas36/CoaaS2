import Jobs.ContextQuery;
import Jobs.QueryFetchJob;
import Jobs.QueryJob;
import Utils.PubSub.Event;
import Utils.PubSub.Subscriber;
import org.quartz.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;


public class QueryScheduler {

    private static QueryScheduler queryScheduler;
    private static SchedulerFactory scheFactory;
    private static Scheduler scheduler;

    Subscriber subscriber = new Subscriber("query-scheduler");

    private static Logger log = Logger.getLogger(QueryScheduler.class.getName());

    private QueryScheduler() throws SchedulerException {
        if(scheFactory == null){
            scheFactory = new org.quartz.impl.StdSchedulerFactory();
            scheduler = scheFactory.getScheduler();
        }
        Event.operation.subscribe("cq-sim", subscriber);
        scheduler.start();
    }

    public static QueryScheduler getInstance(){
        try{
            if(queryScheduler == null){
                queryScheduler = new QueryScheduler();
            }
        }
        catch(Exception ex) {
            log.severe("Query generator failed to start. " + ex.getMessage());
        }
        return queryScheduler;
    }

    public void scheduleQuery(ContextQuery query) throws SchedulerException {
        String queryId = UUID.randomUUID().toString();

        JobDetail job = JobBuilder.newJob(QueryJob.class)
                .withIdentity(queryId, "queryGen")
                .usingJobData("query-id", query.getQueryId())
                .usingJobData("query", query.getQuery())
                .usingJobData("token", query.getToken())
                .build();

        LocalDateTime now = LocalDateTime.now();
        Date startTime = new Date(now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                query.hour, query.minute, query.second);

        SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity(queryId+":Trigger", "queryGen")
                .startAt(startTime)
                .build();

        log.info("Scheduling context query: " + queryId);
        scheduler.scheduleJob(job, trigger);
    }

    public void fetchSchedule() throws SchedulerException {
        JobDetail job = JobBuilder.newJob(QueryFetchJob.class)
                .withIdentity("queryFetch", "queryGen")
                .build();

        SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity("fetchTrigger", "queryGen")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInMinutes(10))
                .build();

        log.info("Started job to fetch hourly queries");
        scheduler.scheduleJob(job, trigger);
    }
}
