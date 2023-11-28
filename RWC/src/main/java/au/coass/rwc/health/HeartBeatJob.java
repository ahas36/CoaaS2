package au.coass.rwc.health;

import au.coass.rwc.executor.SubscriptionHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class HeartBeatJob implements Job {
    public void execute(JobExecutionContext context) {
        try {
            SubscriptionHandler.sendHeartBeats();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
