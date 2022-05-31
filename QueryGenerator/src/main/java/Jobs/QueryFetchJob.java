package Jobs;

import Utils.PubSub.Event;
import Utils.PubSub.Message;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.conversions.Bson;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Stream;

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
            LocalDateTime newtime = time.plusMinutes(10);

            Bson filters = null;
            if(newtime.getHour() == time.getHour()){
                filters = Filters.and(
                        Filters.eq("hour", time.getHour()),
                        Filters.gte("minute", time.getMinute()),
                        Filters.lt("minute", newtime.getMinute()),
                        Filters.eq("day", getDayOfWeek(time.getDayOfWeek().getValue()))
                );
            }
            else if(newtime.getHour() == 0){
                filters = Filters.and(
                        Filters.eq("hour", time.getHour()),
                        Filters.gte("minute", time.getMinute()),
                        Filters.eq("day", getDayOfWeek(newtime.getDayOfWeek().getValue()))
                );
            }
            else{
                filters = Filters.and(
                        Filters.eq("hour", time.getHour()),
                        Filters.gte("minute", time.getMinute()),
                        Filters.eq("day", getDayOfWeek(time.getDayOfWeek().getValue()))
                );
            }

            FindIterable<Document> queries = collection.find(filters);

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

    private static String buildQuery(Document query){
        String queryString = "prefix " +
            "schema:http://schema.org , weat:https://bimerr.iot.linkeddata.es/def/weather" +
            "pull (targetCarparks.*) " +
            "define " +
            "entity targetLocation is from schema:Place " +
                "where targetLocation.name=\"%s\"" +
            "entity cosumerCar is from schema:vehicle " +
                "where consumerCar.vin=\"%s\"" +
            "entity targetWeather is from weat:WeatherProperty " +
                "where targetWeather.location=\"Melbourne, Australia\"" +
            "entity targetCarpark is from schema:ParkingFacility " +
                "where " +
                    "((distance(targetCarpark.location, targetLocation.location, \"walking\")<{\"value\":%d, \"unit\":\"m\"} and goodForWalking(targetWeather)>=0.6) or " +
                    "goodForWalking(targetWeather)>=0.9) and " +
                    "targetCarparks.height <= cosumerCar.height and " +
                    "targetCarparks.isOpen = \"true\" and " +
                    "targetCarparks.availabileSlots > 0";

        Stream<String> defKeys = Arrays.stream(new String[]{"_id", "location", "vin", "address", "distance", "day", "hour", "minute", "second"});

        for(String key : query.keySet()){
            if (defKeys.anyMatch(key::equals))
                queryString += getAddOnToQuery(key, query.get(key));
        }

        String finalQuery = String.format(queryString,
                query.getString("address"),
                query.getString("vin"),
                query.getInteger("distance"));

        return finalQuery;
    }

    private static String getAddOnToQuery(String prop, Object value){
        switch(prop){
            case "price": {
                // TODO: how do CoaaS resolve this kind of a problem?
                return "and targetCarparks.price >= " + String.valueOf((double) value);
            }
            case "rating": return "and targetCarparks.rating >= " + String.valueOf((int) value);
            case "expected_time": {
                LocalDateTime time = LocalDateTime.now();
                long minutes = Math.round((double)value*60);
                time.plusMinutes(minutes);
                return "and isAvailable (targetCarparks.availabileSlots, {\"start_time\":now(), \"end_time\":{\"value\":\""
                        + time.toString() + "\", \"unit\":\"datetime\"}";
            }
        }
        return "";
    }

    private static String getDayOfWeek(int val){
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
