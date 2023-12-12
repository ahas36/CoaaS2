import org.quartz.SchedulerException;

import java.util.logging.Logger;

/**
 * @author shakthi
 */
public class QueryGeneratingServer {
    private static Logger log = Logger.getLogger(QueryGeneratingServer.class.getName());

    static public void main (String [] args) throws SchedulerException {

        try {
            log.info("Starting Query Generation");
            QueryScheduler queryScheduler = QueryScheduler.getInstance();
            queryScheduler.fetchSchedule();
            log.info("Context Query Simulator Started!");
        }
        catch(Exception ex){
            log.severe("Error occured: " + ex.getMessage());
            log.info(String.valueOf(ex.getStackTrace()));
        }
    }
}
