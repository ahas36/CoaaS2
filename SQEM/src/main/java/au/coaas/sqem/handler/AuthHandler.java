package au.coaas.sqem.handler;

import au.coaas.sqem.proto.AuthRequest;
import au.coaas.sqem.proto.AuthToken;
import au.coaas.sqem.proto.Empty;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.mongo.ConnectionPool;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.logging.Logger;

public class AuthHandler {

    private static final Logger log = Logger.getLogger(AuthHandler.class.getName());

    private static final BasicDBObject project = new BasicDBObject(){{
        put("info.name", true);
        put("info.username", true);
        put("auth.scope", true);
    }};

    public static SQEMResponse getConsumer(AuthRequest registerRequest) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");
            MongoCollection<Document> collection = db.getCollection("contextConsumer");

            Document value = collection.find(Filters.and(
                    Filters.eq("info.username",registerRequest.getUsername()),
                    Filters.eq("info.password",registerRequest.getPassword()),
                    Filters.eq("status", true)
            )).projection(project).first();

            if(value !=  null){
                return SQEMResponse.newBuilder().setStatus("200").setBody(value.toJson()).build();
            }
            return SQEMResponse.newBuilder().setStatus("404").setBody("Consumer Not Found.").build();

        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    public static Empty saveOrUpdateToken(AuthToken tokenRequest) {
        MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
        MongoDatabase db = mongoClient.getDatabase("coaas");
        MongoCollection<Document> collection = db.getCollection("consumerToken");
        MongoCollection<Document> consumerCollection = db.getCollection("contextConsumer");

        try{
            String id = tokenRequest.getId();
            if(id != null){
                BasicDBObject query = new BasicDBObject(){{
                    put("consumerId", tokenRequest.getId());
                    put("status", true);
                }};

                BasicDBObject updateFields = new BasicDBObject(){{
                    put("status", false);
                    put("updatedDate", LocalDateTime.now());
                }};

                collection.updateMany(query, new Document("$set", updateFields), new UpdateOptions().upsert(false));

                Document consumer = new Document() {{
                    put("consumerId", tokenRequest.getId());
                    put("token", tokenRequest.getToken());
                    put("status", true);
                    put("createdDate", LocalDateTime.now());
                    put("updatedDate", null);
                }};

                collection.insertOne(consumer);
            }
            else {
                Document consumer = consumerCollection.find(new BasicDBObject() {{
                    put("info.username", tokenRequest.getUsername());
                    put("status", true);
                }}).first();

                Document token = new Document() {{
                    put("consumerId", consumer.getString("_id"));
                    put("token", tokenRequest.getToken());
                    put("status", true);
                    put("createdDate", LocalDateTime.now());
                    put("updatedDate", null);
                }};

                collection.insertOne(token);
            }
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
        }

        return Empty.newBuilder().build();
    }

    public static SQEMResponse validateConsumer(String username){
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");
            MongoCollection<Document> collection = db.getCollection("contextConsumer");

            Document value = collection.find(Filters.and(
                    Filters.eq("info.username",username),
                    Filters.eq("status", true)
            )).projection(project).first();

            if(value == null || value.isEmpty())
                return SQEMResponse.newBuilder().setStatus("400").build();

            return SQEMResponse.newBuilder().setStatus("200").build();

        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }
}
