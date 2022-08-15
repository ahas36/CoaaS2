package au.coaas.cpree.executor.scheduler;

import au.coaas.cpree.executor.scheduler.jobs.RefreshContext;
import au.coaas.cpree.executor.scheduler.jobs.QueryJob;
import au.coaas.cpree.utils.PubSub.Event;
import org.quartz.*;

import java.util.Date;
import java.time.LocalDateTime;
import java.util.logging.Logger;
import java.time.temporal.ChronoUnit;

public class RefreshScheduler {

    private static RefreshScheduler queryScheduler;
    private static SchedulerFactory scheFactory;
    private static Scheduler scheduler;

    Subscriber subscriber = new Subscriber("refresh-scheduler");

    private static Logger log = Logger.getLogger(RefreshScheduler.class.getName());

    private RefreshScheduler() throws SchedulerException {
        if(scheFactory == null){
            scheFactory = new org.quartz.impl.StdSchedulerFactory();
            scheduler = scheFactory.getScheduler();
        }
        Event.operation.subscribe("cq-sim", subscriber);
        scheduler.start();
    }

    public static RefreshScheduler getInstance(){
        try{
            if(queryScheduler == null){
                queryScheduler = new RefreshScheduler();
            }
        }
        catch(Exception ex) {
            log.severe("Query generator failed to start. " + ex.getMessage());
        }
        return queryScheduler;
    }

    public void scheduleRefresh(RefreshContext query) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(QueryJob.class)
                .withIdentity(query.getContextId(), "refreshGroup")
                .usingJobData("data", query.getData())
                .usingJobData("contextId", query.getContextId())
                .build();

        LocalDateTime exe_time = LocalDateTime.now().plus(query.getRefreshInterval(), ChronoUnit.MILLIS);
        Date triggerTime = new Date(
                exe_time.getYear(),
                exe_time.getMonthValue(),
                exe_time.getDayOfMonth(),
                exe_time.getHour(),
                exe_time.getMinute(),
                exe_time.getSecond()
                );

        SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity(query.getContextId(), "refreshGroup")
                .startAt(triggerTime)
                .build();

        scheduler.scheduleJob(job, trigger);
    }
}
