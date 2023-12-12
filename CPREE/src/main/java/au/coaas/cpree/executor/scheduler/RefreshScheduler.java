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

/**
 * @author shakthi
 */
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

        JobDetail job = JobBuilder.newJob(QueryJob.class)
                .withIdentity("context-entity-refreshing", "refreshGroup")
                .storeDurably()
                .build();
        scheduler.addJob(job, true);

        // Starting routines
        scheduleRegisterClearance();
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

        TriggerKey triggerKey = TriggerKey.triggerKey(query.getOperationId(), "refreshGroup-trigger");
        if(!scheduler.checkExists(triggerKey)) {
            LocalDateTime exe_time = LocalDateTime.now().plus(query.getInitInterval(), ChronoUnit.MILLIS);
            Date triggerTime = Date.from(exe_time.atZone(ZoneId.systemDefault()).toInstant());

            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(query.getOperationId(), "refreshGroup-trigger")
                    .forJob("context-entity-refreshing", "refreshGroup")
                    .usingJobData(query.getJobDataMap())
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInMilliseconds(query.getRefreshInterval())
                            .repeatForever())
                    .startAt(triggerTime)
                    .build();

            scheduler.scheduleJob(trigger);
            log.info("Refresh scheduled " +query.getOperationId());

            SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                    = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
            asyncStub.logSchedulerAction(SchedlerInfo.newBuilder()
                    .setContextId(query.getContextId())
                    .setJobId(query.getOperationId())
                    .setAction("set")
                    .build());
        }
        else {
            log.info("Provided refresh operation " +query.getOperationId()+ " already exists!");
        }
    }

    // Done
    public void stopRefreshing(String jobId) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobId, "refreshGroup-trigger");
        if(scheduler.checkExists(triggerKey)){
            scheduler.unscheduleJob(triggerKey);
            log.info("Refresh stopped: " + jobId);
            SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                    = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
            asyncStub.logSchedulerAction(SchedlerInfo.newBuilder()
                    .setJobId(jobId)
                    .setAction("delete")
                    .build());
        }
        else {
            log.severe("Couldn't find the trigger in stopRefreshing: " + jobId);
        }
    }

    // Done
    public void updateRefreshing(RefreshContext query) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(query.getOperationId(), "refreshGroup-trigger");
        if(scheduler.checkExists(triggerKey)){
            scheduler.unscheduleJob(triggerKey);
            SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                    = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
            asyncStub.logSchedulerAction(SchedlerInfo.newBuilder()
                    .setContextId(query.getContextId())
                    .setJobId(query.getOperationId())
                    .setAction("reset")
                        .build());

            scheduleRefresh(query);
            log.info("Refresh updated: " + query.getOperationId());
        }
        else {
            log.severe("Couldn't find the trigger in updateRefreshing: " + query.getOperationId());
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
