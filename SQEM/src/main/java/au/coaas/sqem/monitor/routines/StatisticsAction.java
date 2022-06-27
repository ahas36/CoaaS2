package au.coaas.sqem.monitor.routines;

import au.coaas.sqem.handler.PerformanceLogHandler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.logging.Logger;

public class StatisticsAction implements Job {

    private static Logger log = Logger.getLogger(StatisticsAction.class.getName());

    @Override
    public void execute(JobExecutionContext jobExecutionContext){
        try {
            PerformanceLogHandler.summarize();
            PerformanceLogHandler.clearOldRecords(60);
        }catch (Exception e){
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }

}
