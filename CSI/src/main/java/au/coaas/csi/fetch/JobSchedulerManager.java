package au.coaas.csi.fetch;

import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.ContextService;
import au.coaas.csi.utils.Utils;
import org.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobSchedulerManager {

    // private instance, so that it can be
    // accessed by only by getInstance() method
    private static JobSchedulerManager instance;

    private JobSchedulerManager() throws SchedulerException {
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

        JobDetail job = JobBuilder.newJob(FetchJob.class)
                .withIdentity("fetch-job", "cs-fetch-job")
                .storeDurably()
                .build();

        scheduler.addJob(job, true);
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


    private long convertTime2MilliSecond(JSONObject timeObject) {
        switch (timeObject.getString("unit")) {
            case "s":
                return timeObject.getInt("value") * 1000;
            case "min":
                return timeObject.getInt("value") * 1000 * 60;
            case "hour":
                return timeObject.getInt("value") * 1000 * 60 * 60;
            case "day":
                return timeObject.getInt("value") * 1000 * 60 * 60 * 24;
            default:
                return timeObject.getLong("value");
        }
    }

    public CSIResponse registerJob(ContextService cs) throws SchedulerException {
        if (!scheduler.isStarted()) {
            throw new SchedulerException("Scheduler not started");
        }

        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("cs", cs.getJson());

        JSONObject contextService = new JSONObject(cs.getJson());

        // This key is the unique identifier of the entity
        jobDataMap.put("key", contextService.getJSONObject("sla").get("key").toString());

        String ontClass = contextService.getJSONObject("info").getString("ontClass").trim();
        ontClass = ontClass.substring(1, ontClass.length() - 1);
        String[] ontClassSplit = ontClass.split("/");
        ontClass = ontClassSplit[ontClassSplit.length - 1];
        jobDataMap.put("ontClass", ontClass);
        jobDataMap.put("reportAccess", cs.getReportAccess().equals("True")? true: false);

        String graph = contextService.getJSONObject("info").getString("graph").trim();

        long updateFrequency = this.convertTime2MilliSecond(
                contextService.getJSONObject("sla").getJSONObject("updateFrequency"));

        graph = graph.substring(1, graph.length() - 1);
        jobDataMap.put("graph", graph);

        jobDataMap.put("params", (new JSONObject(cs.getParamsMap())).toString());
        jobDataMap.put("providerId", cs.getMongoID());

        String jobId = cs.getMongoID() + "-" + Utils.getHashKey(cs.getParamsMap());

        if(cs.getTimes() < 1){
            SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                    .withIdentity(jobId, "cs-fetch-trigger")
                    .forJob("fetch-job", "cs-fetch-job")
                    .usingJobData(jobDataMap)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever()
                            .withIntervalInMilliseconds(updateFrequency))
                    .startNow()
                    .build();
            scheduler.scheduleJob(trigger);
        }
        else if(cs.getTimes() > 1) {
            SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                    .withIdentity(jobId, "cs-fetch-trigger")
                    .forJob("fetch-job", "cs-fetch-job")
                    .usingJobData(jobDataMap)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withRepeatCount(cs.getTimes())
                            .withIntervalInMilliseconds(updateFrequency))
                    .startNow()
                    .build();
            scheduler.scheduleJob(trigger);
        }
        else {
            // When times == 1.
            // Only this block is blocking.
            FetchJob tempFetchJob = new FetchJob();
            List<String> result = tempFetchJob.execute(jobDataMap);
            return CSIResponse.newBuilder().setStatus("200")
                    .addAllHashkeys(result).build();
        }

        return CSIResponse.newBuilder().setStatus("200").build();
    }

    public CSIResponse cancelJob(ContextService cs) throws SchedulerException {
        String jobId = cs.getMongoID() + "-" + Utils.getHashKey(cs.getParamsMap());
        boolean cs1 = scheduler.unscheduleJob(new TriggerKey(jobId, "cs-fetch-trigger"));
        return CSIResponse.newBuilder().setStatus(cs1 ? "200" : "500").build();
    }

    public CSIResponse updateJob(ContextService cs) throws SchedulerException {
        boolean cs1 = true;
        String hashkey = Utils.getHashKey(cs.getParamsMap());
        String jobId = cs.getMongoID() + "-" + hashkey;
        TriggerKey triggerKey = new TriggerKey(jobId, "cs-fetch-trigger");

        // Checking if any retrievals had happend in between.
        Date lastFire = scheduler.getTrigger(triggerKey).getPreviousFireTime();
        long duration = System.currentTimeMillis() - lastFire.getTime(); // Time since the last retrieval in ms

        // Following is a simple hack.
        // This should have fetched the last retrieval time, the age as well and then make the retrieval decision.
        // In this hack, only the last update has happend is checked.
        JSONObject csObj = new JSONObject(cs.getJson());
        double updateFreq = csObj.getJSONObject("sla").getJSONObject("updateFrequency")
                .getDouble("value") * 1000; // assuming seconds and converting to ms

        if(duration > updateFreq) {
            if(scheduler.checkExists(triggerKey)){
                cs1 = scheduler.unscheduleJob(triggerKey);
            }

            if (cs1) {
                return registerJob(cs);
            }

            return CSIResponse.newBuilder().setStatus("500").build();
        }
        else {
            // Returns that a fetch has happend in a recent history. Sufficient lifetime should remain in storage.
            return CSIResponse.newBuilder().setStatus("200")
                    .addHashkeys(hashkey).build();
        }
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
