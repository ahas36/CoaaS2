import org.quartz.SchedulerException;

import java.util.logging.Logger;

public class QueryGeneratingServer {
    private static Logger log = Logger.getLogger(QueryGeneratingServer.class.getName());

    static public void main (String [] args) throws SchedulerException {
        log.info("Starting Query Generation");
        QueryScheduler queryScheduler = QueryScheduler.getInstance();
        queryScheduler.fetchSchedule();
        log.info("Context Query Simulator Started!");
    }
}
