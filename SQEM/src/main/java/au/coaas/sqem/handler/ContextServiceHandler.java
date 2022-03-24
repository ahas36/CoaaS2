package au.coaas.sqem.handler;

import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.ContextServiceRequest;
import au.coaas.sqem.proto.RegisterContextServiceRequest;
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
import java.util.logging.Logger;


public class ContextServiceHandler {
    private static final Logger LOG = Logger.getLogger(ContextServiceHandler.class.getName());

    public static SQEMResponse register(RegisterContextServiceRequest registerRequest) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("mydb");

            MongoCollection<Document> collection = db.getCollection("contextService");

            Document myDoc = Document.parse(registerRequest.getJson());
            myDoc.put("status","active");

            collection.insertOne(myDoc);

            JSONObject body = new JSONObject();
            body.put("message","1 Service Registered");
            body.put("id",myDoc.get("_id").toString());

            return SQEMResponse.newBuilder().setStatus("200").setBody(body.toString()).build();
        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    public static SQEMResponse changeStatus(String id,String status) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("mydb");

            MongoCollection<Document> collection = db.getCollection("contextService");

            BasicDBObject query = new BasicDBObject();

            query.put("_id", id);

            BasicDBObject updateFields = new BasicDBObject();

            updateFields.put("status",status);

            UpdateResult ur = collection.updateMany(query, new Document("$set", updateFields), new UpdateOptions().upsert(false));

            JSONObject body = new JSONObject();
            body.put("message","1 Service updated");
            body.put("id",id);
            return SQEMResponse.newBuilder().setStatus("200").setBody(body.toString()).build();
        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    public static SQEMResponse discoverMatchingServices(ContextServiceRequest serviceRequest) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");

            MongoCollection<Document> collection = db.getCollection("contextService");

            String vocabURI = "<" + serviceRequest.getEt().getVocabURI().trim();

            if(!vocabURI.endsWith("/"))
            {
                vocabURI = vocabURI + "/";
            }

            vocabURI = vocabURI + serviceRequest.getEt().getType() + ">";

            JSONArray finalResultJsonArr = new JSONArray();

            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    JSONObject resultJSON = new JSONObject(document.toJson());
                    finalResultJsonArr.put(resultJSON);
                }
            };

            collection.find(Filters.and(
                    Filters.eq("info.ontClass",vocabURI),
                    Filters.eq("sla.cache",false),
                    Filters.all("info.params.term.label",serviceRequest.getParamsList()))).
                    forEach(printBlock);


            return SQEMResponse.newBuilder().setStatus("200").setBody(finalResultJsonArr.toString()).build();

        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    public static SQEMResponse getAllServices() {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("mydb");

            MongoCollection<Document> collection = db.getCollection("contextService");

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
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }
}
