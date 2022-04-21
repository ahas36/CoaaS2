package au.coaas.sqem.handler;

import au.coaas.sqem.proto.AuthRequest;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.mongo.ConnectionPool;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.model.Filters;
import org.bson.Document;

public class AuthHandler {

    private static final Document project = new Document(){{
        put("info.name", true);
        put("info.username", true);
        put("sla", true);
    }};

    public static SQEMResponse getConsumer(AuthRequest registerRequest) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");
            MongoCollection<Document> collection = db.getCollection("contextConsumer");

            Document value = collection.find(Filters.and(
                    Filters.eq("info.username",registerRequest.getUsername()),
                    Filters.eq("info.password",registerRequest.getPassword()),
                    Filters.eq("info.active", true)
            )).projection(project).first();

            return SQEMResponse.newBuilder().setStatus("200").setBody(value.toJson()).build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }
}
