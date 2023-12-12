package au.coaas.sqem.handler;

import au.coaas.sqem.proto.AuthToken;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.RegisterContextConsumerRequest;

import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * @author shakthi
 */
public class ContextConsumerHandler {
    private static final Logger log = Logger.getLogger(ContextConsumerHandler.class.getName());

    private static final BasicDBObject project = new BasicDBObject(){{
        put("_id", true);
        put("sla", true);
    }};

    private static final BasicDBObject idProject = new BasicDBObject(){{
        put("_id", true);
    }};

    public static SQEMResponse register(RegisterContextConsumerRequest registerRequest) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");

            MongoCollection<Document> collection = db.getCollection("contextConsumer");

            JSONObject u_fetch = new JSONObject(registerRequest.getJson());

            Document active_consumers = collection.find(Filters.and(
                    Filters.eq("info.username", u_fetch.getJSONObject("info").getString("username")),
                    Filters.eq("status", true)
            )).projection(project).first();

            if(active_consumers == null || active_consumers.isEmpty()){
                Document consumer = Document.parse(registerRequest.getJson());

                consumer.put("status", true);
                consumer.put("createdDate", LocalDateTime.now());
                consumer.put("updatedDate", null);

                collection.insertOne(consumer);

                JSONObject body = new JSONObject(){{
                    put("id",consumer.get("_id").toString());
                    put("message","Context Consumer Registered.");
                }};

                return SQEMResponse.newBuilder().setStatus("200").setBody(body.toString()).build();
            }

            return SQEMResponse.newBuilder().setStatus("404").setBody("Username already taken.").build();

        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            log.severe(e.getMessage());

            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    public static SQEMResponse changeStatus(String id, Boolean status) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");
            MongoCollection<Document> collection = db.getCollection("contextConsumer");

            BasicDBObject query = new BasicDBObject();
            query.put("_id", id);

            BasicDBObject updateFields = new BasicDBObject(){{
                put("status", status);
                put("updatedDate", LocalDateTime.now());
            }};

            collection.updateMany(query, new Document("$set", updateFields), new UpdateOptions().upsert(false));

            JSONObject body = new JSONObject(){{
                put("id",id);
                put("message","Context Consumer Updated.");
            }};

            return SQEMResponse.newBuilder().setStatus("200").setBody(body.toString()).build();
        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());

            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    public static SQEMResponse retrieveSLA(AuthToken authToken){
        try{
            String token;
            if(!authToken.getUsername().equals(null)) {
                // SLA retrieval using subscription Id
                SQEMResponse res = SubscriptionHandler.getSubscription(authToken.getUsername());
                if(!res.getStatus().equals("200"))
                    return SQEMResponse.newBuilder().setStatus("404")
                        .setBody("Couldn't find an consumer by the subscription Id.").build();
                token = res.getMeta();
            }
            else token = authToken.getToken();
            // SLA retrieval using auth token.
            String json = getConsumerSLA(token, false);
            if(json != null){
                return SQEMResponse.newBuilder()
                        .setStatus("200").setBody(json).build();
            }

            return SQEMResponse.newBuilder().setStatus("404")
                    .setBody("Couldn't find an consumer by the token").build();
        }
        catch(Exception e){
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause() != null ? e.getCause().toString() : "Error in Retrieve Consumer SLA.");

            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    protected static String getConsumerSLA(String authToken, boolean idOnly) {
        MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
        MongoDatabase db = mongoClient.getDatabase("coaas");

        MongoCollection<Document> tokenCollection = db.getCollection("consumerToken");
        Document value = tokenCollection.find(Filters.and(
                Filters.eq("status", true),
                Filters.eq("token", authToken)
        )).first();

        if(value != null){
            MongoCollection<Document> consumerCollection = db.getCollection("contextConsumer");
            String id = value.getString("consumerId");
            FindIterable<Document> slaSet = consumerCollection.find(Filters.and(
                    Filters.eq("_id", new ObjectId(id)),
                    Filters.eq("status", true)
            ));

            Document sla;
            if(idOnly) sla = slaSet.projection(idProject).first();
            else sla = slaSet.projection(project).first();

            return sla.toJson();
        }
        return null;
    }
}
