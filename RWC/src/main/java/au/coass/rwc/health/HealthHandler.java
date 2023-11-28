package au.coass.rwc.health;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class HealthHandler {
    private static HealthHandler instance;

    private HealthHandler() throws SchedulerException {
        try {
            schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();

            scheduler.clear();
            start();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            shutdown();
        }

        JobDetail job = JobBuilder.newJob(HeartBeatJob.class)
                .withIdentity("heartbeat-job", "heartbeat-job")
                .storeDurably()
                .build();

        scheduler.addJob(job, true);
    }

    public static HealthHandler getInstance() throws SchedulerException {
        if (instance == null) {
            synchronized (HealthHandler.class) {
                if (instance == null) {
                    instance = new HealthHandler();
                }
            }
        }
        return instance;
    }

    private SchedulerFactory schedulerFactory;
    private Scheduler scheduler;

    public void startHeartBeat() throws SchedulerException {
        if (!scheduler.isStarted()) {
            throw new SchedulerException("Scheduler not started");
        }

        SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity("heartbeat", "heartbeat-job")
                .forJob("heartbeat", "heartbeat-job")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever()
                        .withIntervalInMinutes(1))
                .startNow()
                .build();
        scheduler.scheduleJob(trigger);
    }

    public void start() throws SchedulerException {
        if (!scheduler.isStarted()) {
            scheduler.start();
        }
    }

    public void shutdown() throws SchedulerException {
        if (scheduler.isStarted()) {
            scheduler.shutdown();
        }
    }
}
