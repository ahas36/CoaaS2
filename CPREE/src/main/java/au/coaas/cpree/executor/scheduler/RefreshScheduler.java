package au.coaas.cpree.executor.scheduler;

import au.coaas.cpree.executor.scheduler.jobs.QueryJob;
import au.coaas.cpree.executor.scheduler.jobs.RegisterClearJob;
import au.coaas.cpree.utils.PubSub.Event;
import org.quartz.*;

import java.util.Date;
import java.time.LocalDateTime;
import java.util.logging.Logger;
import java.time.temporal.ChronoUnit;

public class RefreshScheduler {

    private static RefreshScheduler refreshScheduler;
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

        // Starting routines
        scheduleRegisterClearance();
    }

    public static void stopRefreshing() throws SchedulerException {
        scheduler.shutdown();
    }

    public static RefreshScheduler getInstance(){
        try{
            if(refreshScheduler == null){
                refreshScheduler = new RefreshScheduler();
            }
        }
        catch(Exception ex) {
            log.severe("Query generator failed to start. " + ex.getMessage());
        }
        return refreshScheduler;
    }

    public void scheduleRefresh(RefreshContext query) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(QueryJob.class)
                .withIdentity(query.getContextId(), "refreshGroup")
                .usingJobData(query.getJobDataMap())
                .build();

        LocalDateTime exe_time = LocalDateTime.now().plus(query.getInitInterval(), ChronoUnit.MILLIS);
        Date triggerTime = new Date(
                exe_time.getYear(),
                exe_time.getMonthValue(),
                exe_time.getDayOfMonth(),
                exe_time.getHour(),
                exe_time.getMinute(),
                exe_time.getSecond()
                );

        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(query.getContextId(), "refreshGroup")
                .startAt(triggerTime)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(query.getRefreshInterval())
                        .repeatForever())
                .build();

        scheduler.scheduleJob(job, trigger);
    }

    public void stopRefreshing(String jobId) throws SchedulerException {
        JobKey jobkey = new JobKey(jobId,"refreshGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey(jobId, "refreshGroup");
        Trigger trigger = scheduler.getTrigger(triggerKey);
        if(trigger != null){
            scheduler.unscheduleJob(triggerKey);
            if(scheduler.getJobDetail(jobkey) != null){
                scheduler.interrupt(jobkey);
                scheduler.deleteJob(jobkey);
            }
        }
    }

    public void updateRefreshing(RefreshContext query) throws SchedulerException {
        JobKey jobkey = new JobKey(query.getContextId(),"refreshGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey(query.getContextId(), "refreshGroup");

        Trigger trigger = scheduler.getTrigger(triggerKey);
        if(trigger != null){
            scheduler.unscheduleJob(triggerKey);
            if(scheduler.getJobDetail(jobkey) != null){
                scheduler.interrupt(jobkey);
                scheduler.deleteJob(jobkey);
            }
            scheduleRefresh(query);
        }
    }

    public void scheduleRegisterClearance() throws SchedulerException {
        JobDetail job = JobBuilder.newJob(RegisterClearJob.class)
                .withIdentity("registerClear", "routineGroup")
                .storeDurably()
                .build();

        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("registerClear", "routineGroup")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(1)
                        .repeatForever())
                .build();

        scheduler.scheduleJob(job, trigger);
    }
}
