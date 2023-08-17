package au.coaas.sqem.handler;

import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.ContextServiceRequest;
import au.coaas.sqem.proto.RegisterContextServiceRequest;
import au.coaas.sqem.proto.RegisterPushQuery;
import au.coaas.sqem.proto.SQEMResponse;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.logging.Logger;


public class SubscriptionHandler {
    private static final Logger log = Logger.getLogger(SubscriptionHandler.class.getName());

    public static SQEMResponse getAllSubscriptions() {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_subscription");

            MongoCollection<Document> collection = db.getCollection("subscriptions");

            JSONArray finalResultJsonArr = new JSONArray();

            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    JSONObject resultJSON = new JSONObject(document.toJson());
                    finalResultJsonArr.put(resultJSON);
                }
            };

            collection.find(new BasicDBObject()).forEach(printBlock);
            return SQEMResponse.newBuilder().setStatus("200").setBody(finalResultJsonArr.toString()).build();

        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            log.severe(e.getMessage());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    public static RegisterPushQuery registerPushQuery (String pushQueryJson){
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_subscription");
            MongoCollection<Document> collection = db.getCollection("subscriptions");

            Document query = Document.parse(pushQueryJson);
            query.put("active", true);
            query.put("updatedDate", LocalDateTime.now());
            collection.insertOne(query);

            return RegisterPushQuery.newBuilder().setStatus("200")
                    .setMessage(query.get("_id").toString()).build();
        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            log.severe(e.getMessage());
            return RegisterPushQuery.newBuilder().setStatus("500").build();
        }
    }

    public static RegisterPushQuery unsubsubscribePushQuery (String subId) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_subscription");
            MongoCollection<Document> collection = db.getCollection("subscriptions");

            BasicDBObject query = new BasicDBObject();
            query.put("_id", subId);

            BasicDBObject updateFields = new BasicDBObject(){{
                put("active", false);
                put("updatedDate", LocalDateTime.now());
            }};

            collection.updateMany(query, new Document("$set", updateFields), new UpdateOptions().upsert(false));

            return RegisterPushQuery.newBuilder().setStatus("200")
                    .setMessage(query.get("_id").toString()).build();
        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            log.severe(e.getMessage());
            return RegisterPushQuery.newBuilder().setStatus("500").build();
        }
    }
}
