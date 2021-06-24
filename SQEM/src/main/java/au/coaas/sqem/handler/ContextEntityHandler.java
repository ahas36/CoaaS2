package au.coaas.sqem.handler;

import au.coaas.cqp.proto.ContextEntity;
import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.RegisterEntityRequest;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.UpdateEntityRequest;
import au.coaas.sqem.util.CollectionDiscovery;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ContextEntityHandler {


//    private static Document preprocess

    public final static int BUCKET_SIZE = 200;

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

            int numberOfIterations = (int)Math.ceil(1.0 * items.length() / numberOfItemsPerTask);

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);


            AtomicInteger error = new AtomicInteger();
            AtomicInteger success = new AtomicInteger();
            for (int factor = 0; factor < numberOfIterations; factor++) {
                int finalFactor = factor;
                executorService.submit(() -> {
                    int start = numberOfItemsPerTask * finalFactor;
                    int end = Math.min(items.length(), start + numberOfItemsPerTask);
                    for (int i = start; i < end; i++) {
                        JSONObject data = items.getJSONObject(i);
                        JSONObject entityType = data.getJSONObject("EntityType");
                        JSONObject entity = data.getJSONObject("Attributes");
                        JSONArray keys = data.getJSONArray("key");
                        UpdateEntityRequest.Builder builder = UpdateEntityRequest.newBuilder().setJson(entity.toString())
                                .setEt(ContextEntityType.newBuilder().setVocabURI(entityType.getString("namespace")).setType(entityType.getString("type")).build())
                                .setKey(keys.toString());

                        SQEMResponse sqemResponse = updateEntity(builder.build());
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

    //this method will get an update request as input and either update the entities that matches the provided key or create a new one
    public static SQEMResponse updateEntity(UpdateEntityRequest updateRequest) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("mydb");

            //it finds the collection based on the entity type
            String collectionName = CollectionDiscovery.discover(updateRequest.getEt());

            MongoCollection<Document> collection = db.getCollection(collectionName);

            BasicDBObject updateFields = new BasicDBObject();

            JSONObject attributes = new JSONObject(updateRequest.getJson());

            //form a query to find all the matching entities that needs to be updated
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

            //execute the update
            UpdateResult ur = collection.updateMany(query, new Document("$set", updateFields), new UpdateOptions().upsert(false));

            //todo it might be better to create a separate thread and update the historical db there instead of blocking main thread
            ContextEntityHandler.updateHistoricalDatabase(collectionName, key, attributes, query);

            //if not row is updated, it means no matching. Create a new one.
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

    //this function will store the update in mongodb based on the Size-based bucketing policy
    private static SQEMResponse updateHistoricalDatabase(String collectionName, JSONArray keys, JSONObject attributes, BasicDBObject query) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("historical_db");

            MongoCollection<Document> collection = db.getCollection(collectionName);

            String todayDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            query.put("coaas:meta:day", todayDate);

            query.put("coaas:meta:nsamples", Document.parse("{$lt: " + ContextEntityHandler.BUCKET_SIZE + "}"));

            JSONObject updateStatement = new JSONObject();

            int samplesInUpdate = 0;

            //todo for now, we are considering the time of arrival only. Needed to be change to consider the time coming in the data
            long currentTime = System.currentTimeMillis();

            //used to create the entity if no matching entities found
            JSONObject entity = new JSONObject();
            entity.put("coaas:meta:day", todayDate);

            JSONObject min = new JSONObject();
            JSONObject max = new JSONObject();
            JSONObject inc = new JSONObject();
            JSONObject samples = new JSONObject();

            for (String attributeName : attributes.keySet()) {
                Object attributeValue = attributes.get(attributeName);
                //to check if the attributeName is a key, then skip
                if (keys != null) {
                    boolean stopFlag = false;
                    for (int i = 0; i < keys.length(); i++) {
                        if (keys.optString(i, "").equals(attributeName)) {
                            stopFlag = true;
                            break;
                        }
                    }
                    if (stopFlag) {
                        entity.put(attributeName, attributeValue);
                        continue;
                    }
                }

                JSONObject entityAttr = new JSONObject();

                samplesInUpdate++;

                min.put(attributeName + ".coaas:meta:first", currentTime);
                entityAttr.put("coaas:meta:start", currentTime);


                max.put(attributeName + ".coaas:meta:last", currentTime);
                entityAttr.put("coaas:meta:end", currentTime);


                inc.put(attributeName + ".coaas:meta:nsamples", 1);
                entityAttr.put("coaas:meta:nsamples", 1);


                JSONObject itemValue = new JSONObject();
                itemValue.put("coaas:meta:val", attributeValue);
                itemValue.put("coaas:meta:time", currentTime);
                samples.put(attributeName + ".coaas:meta:samples", itemValue);
                JSONArray samplesValue = new JSONArray();
                samplesValue.put(itemValue);
                entityAttr.put("coaas:meta:samples", samplesValue);

                entity.put(attributeName, entityAttr);
            }

            inc.put("coaas:meta:nsamples", samplesInUpdate);
            entity.put("coaas:meta:nsamples", samplesInUpdate);

            updateStatement.put("$push", samples);
            updateStatement.put("$inc", inc);
            updateStatement.put("$max", max);
            updateStatement.put("$min", min);

            Document updateDocument = Document.parse(updateStatement.toString());

            UpdateResult updateResult = collection.updateMany(query, updateDocument, new UpdateOptions().upsert(false));

            if (updateResult.getMatchedCount() == 0) {
                Document myDoc = Document.parse(entity.toString());
                collection.insertOne(myDoc);
                return SQEMResponse.newBuilder().setStatus("200").setBody(String.valueOf(1)).build();
            }

            return SQEMResponse.newBuilder().setStatus("200").setBody(String.valueOf(updateResult.getMatchedCount())).build();
        } catch (
                Exception e) {
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
