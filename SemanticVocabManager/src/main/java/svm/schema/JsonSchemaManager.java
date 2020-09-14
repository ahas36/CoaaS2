package svm.schema;

import au.coaas.sqem.proto.SQEMResponse;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import svm.conifg.Config;
import svm.mongo.ConnectionPool;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JsonSchemaManager {

    private static Logger log = Logger.getLogger(JsonSchemaManager.class.getName());


    public static List<String> getClasses(String graphName) {
        MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
        MongoDatabase db = mongoClient.getDatabase(Config.MONGO_DB_NAME);

        BasicDBObject projection = new BasicDBObject();
        projection.put("@key", 1);

        List<String> result = new ArrayList<>();


        Block<Document> printBlock = new Block<Document>() {
            @Override
            public void apply(final Document document) {
                result.add("<"+graphName+"/"+document.getString("@key")+">");
            }
        };

        db.getCollection("<"+graphName+">").find().projection(projection).forEach(printBlock);

        return result;
    }

    public static List<String> getGraphs() {
        try {
            List<String> result = new ArrayList<>();

            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase(Config.MONGO_DB_NAME);
            MongoIterable<String> collection = db.listCollectionNames();
            MongoCursor<String> cursor = collection.iterator();
            while (cursor.hasNext()) {
                String table = cursor.next();
                result.add(table);
            }

            return result;
        } catch (Exception e) {
            log.info(e.getMessage());
            log.info(e.toString());
            e.printStackTrace();
        }
        return null;
    }



    public static SQEMResponse register(String graphName,String schema,String key) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase(Config.MONGO_DB_NAME);

            MongoCollection<Document> collection = db.getCollection(graphName);

            BasicDBObject query = new BasicDBObject();

            query.put("@key", key);

            collection.deleteOne(query);

            Document d = Document.parse(schema.replaceAll("\"\\$","\""));
            collection.insertOne(d);

            JSONObject body = new JSONObject();
            body.put("message", "1 schema created");
            body.put("id", d.get("_id").toString());
            return SQEMResponse.newBuilder().setStatus("200").setBody(body.toString()).build();
        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message", e.getMessage());
            body.put("cause", e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }


    public static void init(){
        FiwareSchema.register();
    }

    public static JSONObject getTerms(String graphName, String ontologyClass) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase(Config.MONGO_DB_NAME);

            MongoCollection<Document> collection = db.getCollection("<"+graphName+">");

            BasicDBObject query = new BasicDBObject();

            query.put("@key", ontologyClass);

            JSONObject item = new JSONObject(collection.find(query).first().toJson());

            return item.getJSONObject("properties");
        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message", e.getMessage());
            body.put("cause", e.getCause().toString());
            return null;
        }
    }
}
