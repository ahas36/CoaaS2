package au.coaas.sqem.monitor;

import au.coaas.sqem.util.Utilty;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.StorageRoutine;
import au.coaas.sqem.monitor.routines.Routine;
import au.coaas.sqem.monitor.routines.RefreshAction;
import au.coaas.sqem.monitor.routines.StatisticsAction;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author shakthi
 */
public class MonitorRoutineManager {

    private Scheduler scheduler;
    private SchedulerFactory schedulerFactory;
    private static MonitorRoutineManager instance;

    private MonitorRoutineManager() throws SchedulerException {
        schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
        scheduler.clear();
        scheduler.start();
    }

    private void SetSchedules() throws SchedulerException {
        JobDetail statJob = JobBuilder.newJob(StatisticsAction.class)
                .withIdentity("stat-actions", "monitor-job")
                .storeDurably()
                .build();
        scheduler.addJob(statJob, true);

        JobDetail evictJob = JobBuilder.newJob(RefreshAction.class)
                .withIdentity("evict-actions", "cache-job")
                .storeDurably()
                .build();
        scheduler.addJob(evictJob, true);

        JobDetail refreshJob = JobBuilder.newJob(StatisticsAction.class)
                .withIdentity("refresh-actions", "cache-job")
                .storeDurably()
                .build();
        scheduler.addJob(refreshJob, true);

        JobDetail learnJob = JobBuilder.newJob(StatisticsAction.class)
                .withIdentity("learn-actions", "cache-job")
                .storeDurably()
                .build();
        scheduler.addJob(learnJob, true);
    }

    public static MonitorRoutineManager getInstance() throws SchedulerException {
        if (instance == null) {
            synchronized (MonitorRoutineManager.class) {
                if (instance == null) {
                    instance = new MonitorRoutineManager();
                }
            }
        }
        return instance;
    }

    // Creating a new routine
    public SQEMResponse registerRoutine(Routine type, StorageRoutine routine) throws SchedulerException {
        if (!scheduler.isStarted()) {
            throw new SchedulerException("Scheduler not started");
        }

        JobDataMap routineDataMap = new JobDataMap();
        String name = type.toString().toLowerCase() == "stat" ? "performance-data-builder" :
                type.toString().toLowerCase() + "-" + routine.getIdentifier();

        TriggerBuilder<Trigger> tiggerBuilder = null;
        switch (type.toString().toLowerCase()) {
            case "stat": {
                // Add the relevant data into the routineDataMap
                tiggerBuilder = TriggerBuilder.newTrigger()
                        .withIdentity(name, "monitor-trigger")
                        .forJob("stat-actions", "monitor-job")
                        .usingJobData(routineDataMap);
                break;
            }
            case "learn": {
                // Add the relevant data into the routineDataMap
                tiggerBuilder = TriggerBuilder.newTrigger()
                        .withIdentity(name, "cache-trigger")
                        .forJob("learn-actions", "cache-job")
                        .usingJobData(routineDataMap);
                break;
            }
            case "evict": {
                // Add the relevant data into the routineDataMap
                tiggerBuilder = TriggerBuilder.newTrigger()
                        .withIdentity(name, "cache-trigger")
                        .forJob("evict-actions", "cache-job")
                        .usingJobData(routineDataMap);
                break;
            }
            case "refresh": {
                // Add the relevant data into the routineDataMap
                tiggerBuilder = TriggerBuilder.newTrigger()
                        .withIdentity(name, "cache-trigger")
                        .forJob("refresh-actions", "cache-job")
                        .usingJobData(routineDataMap);
                break;
            }
        };

        long updateFrequency = Utilty.convertTime2MilliSecond(routine.getFreqUnit(), routine.getFrequency());

        SimpleTrigger trigger = tiggerBuilder
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever()
                        .withIntervalInMilliseconds(updateFrequency))
                .startNow()
                .build();

        scheduler.scheduleJob(trigger);

        return SQEMResponse.newBuilder().setStatus("200").build();
    }

    // Cancel the routine
    public SQEMResponse cancelRoutine(Routine type, StorageRoutine routine) throws SchedulerException {

        boolean status;
        String name = type.toString().toLowerCase() + "-" + routine.getIdentifier();

        switch(type.toString().toLowerCase()){
            case "stat":{
                status = scheduler.unscheduleJob(new TriggerKey(name, "monitor-trigger"));
                break;
            }
            default:{
                status = scheduler.unscheduleJob(new TriggerKey(name, "cache-trigger"));
                break;
            }
        }

        return SQEMResponse.newBuilder().setStatus(status ? "200" : "500").build();
    }

    // Update the routine
    public SQEMResponse updateRoutine(Routine type, StorageRoutine routine) throws SchedulerException {

        boolean status;
        String name = type.toString().toLowerCase() + "-" + routine.getIdentifier();

        switch(type.toString().toLowerCase()){
            case "stat":{
                status = scheduler.unscheduleJob(new TriggerKey(name, "monitor-trigger"));
                break;
            }
            default:{
                status = scheduler.unscheduleJob(new TriggerKey(name, "cache-trigger"));
                break;
            }
        }

        if (status) {
            return registerRoutine(type, routine);
        }

        return SQEMResponse.newBuilder().setStatus("500").build();
    }

    // Start and shutting the monitoring routines
    public void start() throws Exception {
        if (scheduler == null || !scheduler.isStarted()) {
            throw new Exception("Scheduler hasn't started yet!");
        }
        this.SetSchedules();
    }

    public void shutdown() throws SchedulerException {
        if (scheduler.isStarted()) {
            scheduler.shutdown();
        }
    }

}
