package au.coaas.sqem.handler;

import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.RegisterEntityRequest;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.UpdateEntityRequest;
import au.coaas.sqem.util.CollectionDiscovery;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ContextEntityHandler {


//    private static Document preprocess

    public static SQEMResponse createEntity(RegisterEntityRequest registerRequest) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("mydb");

            String collectionName = CollectionDiscovery.discover(registerRequest.getEt());

            MongoCollection<Document> collection = db.getCollection(collectionName);

            Document myDoc = Document.parse(registerRequest.getJson());

            collection.insertOne(myDoc);

            return SQEMResponse.newBuilder().setStatus("200").setBody("1 entity created").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    public static SQEMResponse updateEntities(UpdateEntityRequest updateRequest) {
        String json = updateRequest.getJson();
        if (json.trim().startsWith("[")) {
            JSONArray items = new JSONArray(json);
            int numberOfThreads = 10;

            int numberOfItemsPerTask = 100;

            int numberOfIterations = (int) (items.length() / numberOfItemsPerTask);

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

            UpdateEntityRequest.Builder builder = UpdateEntityRequest.newBuilder().setKey(updateRequest.getKey()).setEt(updateRequest.getEt());

            AtomicInteger error = new AtomicInteger();
            AtomicInteger success = new AtomicInteger();
            for (int factor = 0; factor < numberOfIterations; factor++) {
                int finalFactor = factor;
                executorService.submit(() -> {
                    int start = numberOfItemsPerTask * finalFactor;
                    int end = Math.min(items.length(), start + numberOfItemsPerTask);
                    for (int i = start; i < end; i++) {
                        SQEMResponse sqemResponse = updateEntity(builder.setJson(items.getJSONObject(i).toString()).build());
                        if (sqemResponse.getStatus().equals("200")) {
                            success.getAndIncrement();
                        } else {
                            error.getAndIncrement();
                        }
                    }
                });
            }

            executorService.shutdown();

            try {
                executorService.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {

            }
            JSONObject body = new JSONObject();
            body.put("success", success.intValue());
            body.put("error", error.intValue());
            return SQEMResponse.newBuilder().setStatus("200").setBody(body.toString()).build();
        } else {
            return updateEntity(updateRequest);
        }
    }


    private static Object getValueByKey(String item, String key) {
        String[] keys = key.split("\\.");
        Object value = new JSONObject(item);
        for (String k : keys) {
            if (k.matches("\\d+")) {
                value = ((JSONArray) value).get(Integer.valueOf(k));
            } else {
                value = ((JSONObject) value).get(k);
            }
        }
        return value;
    }

    public static SQEMResponse updateEntity(UpdateEntityRequest updateRequest) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("mydb");

            String collectionName = CollectionDiscovery.discover(updateRequest.getEt());

            MongoCollection<Document> collection = db.getCollection(collectionName);

            BasicDBObject updateFields = new BasicDBObject();

            JSONObject attributes = new JSONObject(updateRequest.getJson());

            for (String attributeName : attributes.keySet()) {
                Object item = attributes.get(attributeName);
                if (item instanceof JSONArray || item instanceof JSONObject) {
                    updateFields.append(attributeName, Document.parse(item.toString()));
                } else {
                    updateFields.append(attributeName, item);
                }
            }

            BasicDBObject query = new BasicDBObject();

            JSONArray key = new JSONArray(updateRequest.getKey());

            for (int i = 0; i < key.length(); i++) {
                query.put(key.getString(i), getValueByKey(updateRequest.getJson(), key.getString(i)));
            }

            UpdateResult ur = collection.updateMany(query, new Document("$set", updateFields), new UpdateOptions().upsert(false));

            if (ur.getMatchedCount() == 0) {
                Document myDoc = Document.parse(updateRequest.getJson());
                collection.insertOne(myDoc);
                return SQEMResponse.newBuilder().setStatus("200").setBody("1 entity created").build();
            } else {
                return SQEMResponse.newBuilder().setStatus("200").setBody(ur.getMatchedCount() + " entity updated").build();
            }
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }

    }


    public static SQEMResponse remove(String name) {
        MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
        MongoDatabase db = mongoClient.getDatabase("mydb");
        MongoCollection<Document> collection = db.getCollection(name);
        collection.drop();
        return SQEMResponse.newBuilder().setStatus("200").build();
    }

    public static SQEMResponse clear(String name) {
        MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
        MongoDatabase db = mongoClient.getDatabase("mydb");
        MongoCollection<Document> collection = db.getCollection(name);
        DeleteResult deleteResult = collection.deleteMany(new BasicDBObject());
        JSONObject jo = new JSONObject();
        jo.put("DeletedCount", deleteResult.getDeletedCount());
        return SQEMResponse.newBuilder().setStatus("200").setBody(jo.toString()).build();
    }

    public static SQEMResponse getAllTypes() {
        MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
        MongoDatabase db = mongoClient.getDatabase("mydb");
        MongoIterable<String> collection = db.listCollectionNames();
        MongoCursor<String> cursor = collection.iterator();
        JSONArray ja = new JSONArray();
        while (cursor.hasNext()) {
            String table = cursor.next();
            if (table.equals("contextService"))
                continue;
            ja.put(table);
        }
        return SQEMResponse.newBuilder().setBody(ja.toString()).setStatus("200").build();
    }
}
