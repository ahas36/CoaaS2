package Jobs;

import Utils.Event;
import Utils.Message;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.time.LocalDateTime;
import java.util.logging.Logger;

public class QueryFetchJob implements Job {

    private final static String conn_string =  "mongodb://localhost:27017";
    private static MongoClient mongoClient;

    // The following key is temporary to test with a single context consumer.
    // Ideally, the token should be saved in the query repository relevant to the consumer.
    private final String token = "";

    private static Logger log = Logger.getLogger(QueryFetchJob.class.getName());

    public void execute(JobExecutionContext arg) {
        if(mongoClient == null){
            MongoClientOptions.Builder options = MongoClientOptions.builder()
                    .connectionsPerHost(400)
                    .maxConnectionIdleTime((60 * 1_000))
                    .maxConnectionLifeTime((120 * 1_000));
            MongoClientURI connectionString = new MongoClientURI(conn_string, options);
            mongoClient = new MongoClient(connectionString);
        }

        try {
            MongoDatabase db = mongoClient.getDatabase("acoca-experiments");
            MongoCollection<Document> collection = db.getCollection("query-params");

            LocalDateTime time = LocalDateTime.now();

            FindIterable<Document> queries = collection.find(Filters.and(
                    Filters.eq("hour", time.getHour()),
                    Filters.eq("day", getDayOfWeek(time.getDayOfWeek().getValue()))
            ));

            for(Document doc: queries){
                ContextQuery cq = new ContextQuery(doc.getString("day"), doc.getInteger("hour"),
                        doc.getInteger("minute"), doc.getInteger("second"),
                        buildQuery(doc), doc.getString("_id"),
                        token
                        );

                Message message = new Message(cq);
                Event.operation.publish("cq-sim", message);
            }

            log.info("Context queries batch for " + time.getDayOfWeek() + " during the "
                    + String.valueOf(time.getHour()) + " hour is scheduled.");

        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    private String buildQuery(Document query){
        //TODO: Query String Generation
        return "query";
    }

    private String getDayOfWeek(int val){
        switch(val){
            case 1: return "Monday";
            case 2: return "Tuesday";
            case 3: return "Wednesday";
            case 4: return "Thursday";
            case 5: return "Friday";
            case 6: return "Saturday";
            default: return "Sunday";
        }
    }
}
