package Jobs;

import Utils.PubSub.Event;
import Utils.PubSub.Message;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.conversions.Bson;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.sample;

public class QueryFetchJob implements Job {

    private final static String conn_string =  "mongodb://localhost:27017";
    private static MongoClient mongoClient;

    private static Logger log = Logger.getLogger(QueryFetchJob.class.getName());

    public void execute(JobExecutionContext arg) {
        log.info("Setting a context queries batch.");

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

            // filters = Filters.eq("day", getDayOfWeek(time.getDayOfWeek().getValue()));
            FindIterable<Document> queries = collection.find(filters);

            MongoDatabase db_2 = mongoClient.getDatabase("coaas");
            MongoCollection<Document> token_collection = db_2.getCollection("consumerToken");

            for(Document doc: queries){
                Document sample_token = token_collection.aggregate(Arrays.asList(Aggregates.sample(1))).first();
                String token = sample_token.getString("token");

                String day = doc.getString("day");
                int hour = doc.getInteger("hour");
                int min = doc.getInteger("minute");
                int second = doc.getInteger("second");
                String query = buildQuery(doc);
                String id = doc.getObjectId("_id").toString();

//                MongoCollection<Document> qs = db.getCollection("test-queries");
//                Document final_query = new Document();
//                final_query.put("token", token);
//                final_query.put("query", query);
//                qs.insertOne(final_query);

                // The following 3 lines are commented out temporarily.
                ContextQuery cq = new ContextQuery(day, hour, min, second, query, id, token);

                Message message = new Message(cq);
                Event.operation.publish("cq-sim", message);
            }

            log.info("Context queries batch for " + time.getDayOfWeek() + " during the "
                    + String.valueOf(time.getHour()) + "." + String.valueOf(time.getMinute())+ " hr is scheduled.");

        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    private static ArrayList<String> additionals = new ArrayList<>(Arrays.asList("height", "isOpen", "availableSlots"));
    private static ArrayList<String> defKeys = new ArrayList<>(Arrays.asList("_id", "location", "vin", "address", "distance", "day", "hour", "minute", "second", "expected_time"));

    private static String buildQuery(Document query){
        String queryString = "prefix " +
            "schema:http://schema.org " +
            "pull (targetCarpark.*) " +
            "define " +
            "entity targetLocation is from schema:Place " +
                "where targetLocation.name=\"%s\", " +
            "entity consumerCar is from schema:Vehicle " +
                "where consumerCar.vin=\"%s\", " +
            "entity targetWeather is from schema:Thing " +
                "where targetWeather.location=\"Melbourne,Australia\", " +
            "entity targetCarpark is from schema:ParkingFacility " +
                "where " +
                    "distance(targetCarpark.location, targetLocation.location)<{\"value\":%d, \"unit\":\"m\"}";

        for(String key : query.keySet()){
            if (!defKeys.contains(key))
                queryString += getAddOnToQuery(key, query.get(key));
        }

        for(String key : additionals){
            queryString += getAddOnToQuery(key, Math.random());
        }

        String finalQuery = "";
        if(query.keySet().contains("vin")){
            finalQuery = String.format(queryString,
                    query.getString("address"),
                    query.getString("vin"),
                    query.getInteger("distance"));
        }
        else {
            MongoDatabase db = mongoClient.getDatabase("acoca-experiments");
            MongoCollection<Document> collection = db.getCollection("vehicles");

            Bson filter = Filters.and(
                    Filters.eq("category.name", "13"),
                    Filters.exists("assigned", false)
                );

            Document vehicle = collection.aggregate(Arrays.asList(match(filter), sample(1))).first();

            finalQuery = String.format(queryString,
                    query.getString("address"),
                    vehicle.getString("vin"),
                    query.getInteger("distance"));
        }

        return finalQuery;
    }

    private static String getAddOnToQuery(String prop, Object value){
        switch(prop){
            case "height": {
                if(((double)value) > 0.8)
                    return " and targetCarpark.maxHeight > consumerCar.height";
                break;
            }
            case "isOpen": {
                if(((double)value) > 0.7)
                    return " and targetCarpark.isOpen = true";
                break;
            }
            case "availableSlots": {
                if(((double)value) > 0.5)
                    return " and targetCarpark.availableSlots > 0";
                break;
            }
            case "price": {
                return " and targetCarpark.price <= " + String.valueOf(value);
            }
            case "rating": return " and targetCarpark.rating >= " + String.valueOf((int) value);
            case "expected_time": {
                LocalDateTime time = LocalDateTime.now();
                long minutes = Math.round((double)value*60);
                time.plusMinutes(minutes);
                return " and isAvailable (targetCarpark.availableSlots, {\"start_time\":now(), \"end_time\":{\"value\":\""
                        + time.toString() + "\", \"unit\":\"datetime\"})";
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
