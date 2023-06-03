package au.coaas.cpree.executor.scheduler;

import au.coaas.cpree.executor.scheduler.jobs.QueryJob;
import au.coaas.cpree.executor.scheduler.jobs.RegisterClearJob;
import au.coaas.cpree.utils.PubSub.Event;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.CacheRefreshRequest;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.SchedlerInfo;
import org.quartz.*;

import java.time.ZoneId;
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

    // Done
    public void scheduleRefresh(RefreshContext query) throws SchedulerException {
        if (!scheduler.isStarted()) {
            throw new SchedulerException("Scheduler not started");
        }

        TriggerKey triggerKey = TriggerKey.triggerKey(query.getOperationId(), "refreshGroup");
        Trigger lookUpTrigger = scheduler.getTrigger(triggerKey);
        if(lookUpTrigger == null) {
            JobDetail job = JobBuilder.newJob(QueryJob.class)
                    .withIdentity(query.getOperationId(), "refreshGroup")
                    .usingJobData(query.getJobDataMap())
                    .build();

            LocalDateTime exe_time = LocalDateTime.now().plus(query.getInitInterval(), ChronoUnit.MILLIS);
            Date triggerTime = Date.from(exe_time.atZone(ZoneId.systemDefault()).toInstant());

            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(query.getOperationId(), "refreshGroup")
                    .startAt(triggerTime)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInMilliseconds(query.getRefreshInterval())
                            .repeatForever())
                    .build();

            scheduler.scheduleJob(job, trigger);

            SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                    = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
            asyncStub.logSchedulerAction(SchedlerInfo.newBuilder()
                    .setContextId(query.getContextId())
                    .setJobId(query.getOperationId())
                    .setAction("set")
                    .build());
        }
        else {
            log.info("Provided refresh operation " +query.getContextId()+ " already exists!");
        }
    }

    // Done
    public void stopRefreshing(String jobId) throws SchedulerException {
        JobKey jobkey = new JobKey(jobId,"refreshGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey(jobId, "refreshGroup");
        Trigger trigger = scheduler.getTrigger(triggerKey);
        if(trigger != null){
            scheduler.unscheduleJob(triggerKey);
            if(scheduler.getJobDetail(jobkey) != null){
                scheduler.interrupt(jobkey);
                scheduler.deleteJob(jobkey);

                SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                        = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                asyncStub.logSchedulerAction(SchedlerInfo.newBuilder()
                        .setJobId(jobId)
                        .setAction("delete")
                        .build());
            }
        }
    }

    // Done
    public void updateRefreshing(RefreshContext query) throws SchedulerException {
        JobKey jobkey = new JobKey(query.getOperationId(),"refreshGroup");
        TriggerKey triggerKey = TriggerKey.triggerKey(query.getOperationId(), "refreshGroup");

        Trigger trigger = scheduler.getTrigger(triggerKey);
        if(trigger != null){
            scheduler.unscheduleJob(triggerKey);
            if(scheduler.getJobDetail(jobkey) != null){
                scheduler.interrupt(jobkey);
                scheduler.deleteJob(jobkey);
                SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                        = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                asyncStub.logSchedulerAction(SchedlerInfo.newBuilder()
                        .setContextId(query.getContextId())
                        .setJobId(query.getOperationId())
                        .setAction("reset")
                        .build());
            }
            scheduleRefresh(query);
        }
    }

    // Done
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
