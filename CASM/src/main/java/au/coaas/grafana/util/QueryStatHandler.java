package au.coaas.grafana.util;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Logger;

public class QueryStatHandler {
    private final static String conn_string =  "mongodb://host.docker.internal:27017";
    private static MongoClient mongoClient;

    private static Logger log = Logger.getLogger(QueryStatHandler.class.getName());

    public static String getrecentQueryStats(){
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

            int hour = time.getHour();
            int minute = time.getMinute();
            String day = getDayOfWeek(time.getDayOfWeek().getValue());

            if(minute == 0){
                if(hour == 0){
                    hour = 23;
                    if(day == "Monday"){
                        day = "Sunday";
                    }
                }
                hour = hour - 1;
                minute = 59;
            }

            ArrayList<String> queries = collection.find(Filters.and(
                    Filters.eq("hour", hour),
                    Filters.eq("minute", minute),
                    Filters.eq("day", day)
            )).map(Document::toJson).into(new ArrayList<>());

            JSONArray resList = new JSONArray();
            queries.stream().parallel().forEach(item -> {
                resList.put(new JSONObject(item));
            });

            JSONObject result = new JSONObject();
            result.put("queries", resList);
            return result.toString();

        } catch (Exception e) {
            log.severe(e.getMessage());
        }

        return null;
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
