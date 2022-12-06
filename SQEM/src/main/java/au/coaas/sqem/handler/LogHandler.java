package au.coaas.sqem.handler;

import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.*;
import com.google.protobuf.util.JsonFormat;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Logger;


public class LogHandler {
    private static final Logger log = Logger.getLogger(LogHandler.class.getName());

    public static SQEMResponse getAllLogs() {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");

            MongoCollection<Document> collection = db.getCollection("query");

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

    public static SQEMResponse logQuery(CDQLLog queryString) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");

            MongoCollection<Document> collection = db.getCollection("query");

            Document doc = new Document("raw_query", queryString.getRawQuery())
                    .append("timestamp", System.currentTimeMillis())
                    .append("queryId", queryString.getQueryId());

            collection.insertOne(doc);

            JSONObject body = new JSONObject();
            body.put("message","a new log has been saved!");
            return SQEMResponse.newBuilder().setStatus("200").setBody(body.toString()).build();
        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    public static SQEMResponse logQueryTree(CDQLLog queryString) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");

            MongoCollection<Document> collection = db.getCollection("queryTree");

            Document doc = new Document();
            Document queryPlan = Document.parse(queryString.getRawQuery());
            doc.append("query_tree", queryPlan);
            doc.append("timestamp", System.currentTimeMillis());
            doc.append("queryId", queryString.getQueryId());
            doc.append("complexity", queryString.getComplexity());

            collection.insertOne(doc);

            JSONObject body = new JSONObject();
            body.put("message","a new query tree has been saved in logs!");
            return SQEMResponse.newBuilder().setStatus("200").setBody(body.toString()).build();
        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    // Logs all the interactions with ConQEng
    public static Empty logConQEng(ConQEngLog request) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");
            MongoCollection<Document> collection = db.getCollection("external_service_log");

            Document doc = new Document();
            doc.append("service", "ConQEng");
            doc.append("id", request.getId());
            doc.append("cr", request.getCr());
            doc.append("status", request.getStatus());
            doc.append("message", request.getMessage());
            doc.append("timestamp", System.currentTimeMillis());

            collection.insertOne(doc);
        }
        catch (Exception e) {
            log.severe("Exception in recording external system interaction: " + e.getMessage());
        }
        return null;
    }

    /**
     * The following method need to be implemented for the purposes of:
     * (1) Retrieving parsed queries to save in cache in for prioritizing similar query process
     * (2) Retrieving parsed queries to learn and modify the collaborative filter.
     * */
    public static SQEMResponse getParsedQueries() {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");

            MongoCollection<Document> collection = db.getCollection("queryTree");

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
