package au.coaas.cpree.executor.scheduler.jobs;

import au.coaas.cpree.executor.RefreshExecutor;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.logging.Logger;

public class RegisterClearJob implements Job {

    private static Logger log = Logger.getLogger(RegisterClearJob.class.getName());

    public void execute(JobExecutionContext context) {
        try {
            RefreshExecutor.clearAttemptRegistery();
        }
        catch(Exception ex) {
            log.severe("Error clearing registers: " + ex.getMessage());
        }
    }
}
