package au.coaas.csi.fetch;

import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.ContextService;
import org.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


public class JobSchedulerManager {

    // private instance, so that it can be
    // accessed by only by getInstance() method
    private static JobSchedulerManager instance;

    private JobSchedulerManager() throws SchedulerException {
        schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();

        JobDetail job = JobBuilder.newJob(FetchJob.class)
                .withIdentity("fetch-job", "cs-fetch-job")
                .storeDurably()
                .build();
        scheduler.addJob(job,true);
    }

    public static JobSchedulerManager getInstance() throws SchedulerException {
        if (instance == null) {
            //synchronized block to remove overhead
            synchronized (JobSchedulerManager.class) {
                if (instance == null) {
                    // if instance is null, initialize
                    instance = new JobSchedulerManager();
                }

            }
        }
        return instance;
    }


    private SchedulerFactory schedulerFactory;
    private Scheduler scheduler;


    public CSIResponse registerJob(ContextService cs) throws SchedulerException {
        if (!scheduler.isStarted()) {
            throw new SchedulerException("Scheduler not started");
        }

       JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("cs",cs.getJson());

        JSONObject contextService = new JSONObject(cs.getJson());

        jobDataMap.put("key",contextService.getJSONObject("info").get("key").toString());

        String ontClass = contextService.getJSONObject("info").getString("ontClass").trim();
        ontClass = ontClass.substring(1,ontClass.length()-1);
        String[] ontClassSplit = ontClass.split("/");
        ontClass = ontClassSplit[ontClassSplit.length-1];
        jobDataMap.put("ontClass",ontClass);

        String graph = contextService.getJSONObject("info").getString("graph").trim();
        graph = graph.substring(1,graph.length()-1);
        jobDataMap.put("graph",graph);

        SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity(cs.getMongoID(), "cs-fetch-trigger")
                .forJob("fetch-job", "cs-fetch-job")
                .usingJobData(jobDataMap)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever()
                .withIntervalInSeconds(2))
                .startNow()
                .build();

        scheduler.scheduleJob(trigger);


        return CSIResponse.newBuilder().setStatus("200").build();
    }

    public CSIResponse cancelJob(ContextService cs) throws SchedulerException {
        boolean cs1 = scheduler.unscheduleJob(new TriggerKey(cs.getMongoID(), "cs-fetch-trigger"));
        return CSIResponse.newBuilder().setStatus(cs1 ? "200" : "500").build();
    }

    public CSIResponse updateJob(ContextService cs) throws SchedulerException {

        boolean cs1 = scheduler.unscheduleJob(new TriggerKey(cs.getMongoID(), "cs-fetch-trigger"));

        if (cs1) {
            return registerJob(cs);
        }

        return CSIResponse.newBuilder().setStatus("500").build();
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
