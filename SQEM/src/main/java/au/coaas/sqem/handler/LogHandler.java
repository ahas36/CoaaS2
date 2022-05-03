package au.coaas.sqem.handler;

import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.CDQLLog;
import au.coaas.sqem.proto.RegisterSituationRequest;
import au.coaas.sqem.proto.SQEMResponse;
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
    private static final Logger LOG = Logger.getLogger(LogHandler.class.getName());

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

            Document doc = new Document("query_tree", new JSONObject(queryString.getRawQuery()));
            doc.append("timestamp", System.currentTimeMillis());
            doc.append("queryId", queryString.getQueryId());

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
