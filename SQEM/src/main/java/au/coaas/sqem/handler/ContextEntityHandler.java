package au.coaas.sqem.handler;

import au.coaas.cqp.proto.ContextEntityType;

import au.coaas.sqem.proto.GetEntitiesRequest;
import au.coaas.sqem.util.ResponseUtils;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.util.CollectionDiscovery;
import au.coaas.sqem.proto.UpdateEntityRequest;
import au.coaas.sqem.proto.RegisterEntityRequest;

import au.coaas.sqem.util.Utilty;
import com.google.gson.JsonParser;
import com.microsoft.sqlserver.jdbc.StringUtils;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import com.mongodb.util.JSON;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.bson.conversions.Bson;

import javax.swing.text.Utilities;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static com.microsoft.sqlserver.jdbc.StringUtils.isNumeric;

public class ContextEntityHandler {

    public final static int BUCKET_SIZE = 200;

    public static SQEMResponse createEntity(RegisterEntityRequest registerRequest) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");

            String collectionName = CollectionDiscovery.discover(registerRequest.getEt());

            MongoCollection<Document> collection = db.getCollection(collectionName);

            Document entDoc = Document.parse(registerRequest.getJson());
            entDoc.put("providers", new String[]{registerRequest.getProviderId()});

            collection.insertOne(entDoc);

            return SQEMResponse.newBuilder().setStatus("200").setBody("One " +
                    registerRequest.getEt().getType() +" entity created").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    public static SQEMResponse updateEntities(UpdateEntityRequest updateRequest) {
        String json = updateRequest.getJson();
        JSONObject response = new JSONObject(json.trim());

        if (response.has("results")) {
            JSONArray items = response.getJSONArray("results");
            int numberOfThreads = 10;

            int numberOfItemsPerTask = 100;

            int numberOfIterations = (int) Math.ceil(1.0 * items.length() / numberOfItemsPerTask);

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

            Stack<String> hashkeys = new Stack<>();
            AtomicInteger error = new AtomicInteger();
            AtomicInteger success = new AtomicInteger();
            for (int factor = 0; factor < numberOfIterations; factor++) {
                int finalFactor = factor;
                executorService.submit(() -> {
                    int start = numberOfItemsPerTask * finalFactor;
                    int end = Math.min(items.length(), start + numberOfItemsPerTask);
                    for (int i = start; i < end; i++) {
                        JSONObject data = items.getJSONObject(i);
                        // Observed time calculation for each entity (more specific than the avgAge in response).
                        Long timestamp = new java.util.Date().getTime();
                        if(data.has("age")){
                            long age = 0;
                            JSONObject age_obj = data.getJSONObject("age");

                            String unit = age_obj.getString("unitText");
                            long value = age_obj.getLong("value");

                            // Age here is considered in miliseconds
                            switch(unit){
                                case "ms": age = value; break;
                                case "s": age = value*1000; break;
                                case "h": age = value*60*1000; break;
                            }
                            timestamp -= (age + updateRequest.getRetLatency());
                        }

                        UpdateEntityRequest.Builder builder = UpdateEntityRequest.newBuilder()
                                .setJson(data.toString())
                                .setEt(updateRequest.getEt())
                                .setObservedTime(timestamp)
                                .setProviderId(updateRequest.getProviderId())
                                .setKey(updateRequest.getKey());

                        SQEMResponse sqemResponse = updateEntity(builder.build());
                        if (sqemResponse.getStatus().equals("200")) {
                            success.getAndIncrement();
                            hashkeys.push((new JSONObject(sqemResponse.getBody()).getString("hashkey")));
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

            List<String> hashList = new ArrayList<>();
            while(!hashkeys.isEmpty()){
                hashList.add(hashkeys.pop());
            }

            body.put("hashkeys", hashList);
            return SQEMResponse.newBuilder().setStatus("200").setBody(body.toString()).build();
        }
        else {
            long age = 0;
            Long timestamp = new java.util.Date().getTime();

            if(response.has("age")){
                JSONObject age_obj = response.getJSONObject("age");

                String unit = age_obj.getString("unitText");
                long value = age_obj.getLong("value");

                // Age here is considered in miliseconds
                switch(unit){
                    case "ms": age = value; break;
                    case "s": age = value*1000; break;
                    case "h": age = value*60*1000; break;
                }
                timestamp -= (age + updateRequest.getRetLatency());

                UpdateEntityRequest.Builder builder = UpdateEntityRequest.newBuilder()
                        .setJson(response.toString())
                        .setEt(updateRequest.getEt())
                        .setObservedTime(timestamp)
                        .setProviderId(updateRequest.getProviderId())
                        .setKey(updateRequest.getKey());

                return updateEntity(builder.build());
            }

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

    public static SQEMResponse getEntities(GetEntitiesRequest entIDs) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");

            //it finds the collection based on the entity type
            String collectionName = CollectionDiscovery.discover(entIDs.getEntityType());

            MongoCollection<Document> collection = db.getCollection(collectionName);

            BasicDBObject query = new BasicDBObject();
            query.put("hashkey", entIDs.getKeysList());
            MongoCursor<Document> cursor = collection.find(query).cursor();
            JSONArray ja = new JSONArray();
            while (cursor.hasNext()) {
                ja.put(cursor.next());
            }
            JSONObject result = new JSONObject();
            result.put("results", ja);

            return SQEMResponse.newBuilder().setBody(result.toString()).setStatus("200").build();
        }
        catch(Exception ex){
            return SQEMResponse.newBuilder().setStatus("500").setBody(ex.getMessage()).build();
        }
    }

    //this method will get an update request as input and either update the entities that matches the provided key or create a new one
    public static SQEMResponse updateEntity(UpdateEntityRequest updateRequest) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");

            //it finds the collection based on the entity type
            String collectionName = CollectionDiscovery.discover(updateRequest.getEt());

            MongoCollection<Document> collection = db.getCollection(collectionName);

            BasicDBObject updateFields = new BasicDBObject();

            JSONObject attributes = new JSONObject(updateRequest.getJson());
            String hashkey = "";

            //Matching the entities by unique keys
            for (String attributeName : attributes.keySet()) {
                Object item = attributes.get(attributeName);
                String stringItem = item.toString();

                if(stringItem.startsWith("{")){
                    updateFields.append(attributeName, Document.parse(stringItem));
                }
                else if(stringItem.startsWith("[")) {
                    updateFields.append(attributeName, Document.parse(stringItem));
                }
                else if (item instanceof JSONArray || item instanceof JSONObject) {
                    updateFields.append(attributeName, Document.parse(stringItem));
                } else if(isNumeric(stringItem)){
                    if(StringUtils.isInteger(stringItem))
                        updateFields.append(attributeName, Integer.valueOf(stringItem));
                    else
                        updateFields.append(attributeName, Double.valueOf(stringItem));
                }
                else if(Boolean.parseBoolean(stringItem)){
                    updateFields.append(attributeName, Boolean.valueOf(stringItem));
                }
                else {
                    updateFields.append(attributeName, item);
                }
            }

            BasicDBObject query = new BasicDBObject();

            JSONArray key = new JSONArray(updateRequest.getKey());

            for (int i = 0; i < key.length(); i++) {
                String idKey = key.getString(i);
                Object idValue = getValueByKey(updateRequest.getJson(), idKey);
                query.put(idKey, idValue);
                hashkey += key.getString(i) + "@" + idValue.toString() + ";";
            }
            updateFields.append("hashkey", Utilty.getHashKey(hashkey));
            updateFields.append("updatedTime", LocalDateTime.now());

            //execute the update
            Document queryDoc = new Document();
            queryDoc.put("$set", updateFields);
            queryDoc.put("$addToSet", new Document("providers", updateRequest.getProviderId()));
            UpdateResult ur = collection.updateMany(query, queryDoc, new UpdateOptions().upsert(false));

            //todo it might be better to create a separate thread and update the historical db there instead of blocking main thread
            ContextEntityHandler.updateHistoricalDatabase(collectionName, key, attributes, query, updateRequest.getObservedTime());

            //if not row is updated, it means no matching. Create a new one.
            if (ur.getMatchedCount() == 0) {
                Document myDoc = Document.parse(updateRequest.getJson());
                collection.insertOne(myDoc);
                JSONObject res = new JSONObject();
                res.put("message", "Created a new context entity");
                res.put("hashkey", Utilty.getHashKey(hashkey));
                return SQEMResponse.newBuilder().setStatus("200").setBody(res.toString()).build();
            } else {
                JSONObject res = new JSONObject();
                res.put("message", ur.getMatchedCount() + " entity updated");
                res.put("hashkey", Utilty.getHashKey(hashkey));
                return SQEMResponse.newBuilder().setStatus("200").setBody(res.toString()).build();
            }
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    //this function will store the update in mongodb based on the Size-based bucketing policy
    private static SQEMResponse updateHistoricalDatabase(String collectionName, JSONArray keys, JSONObject attributes, BasicDBObject query, Long observedTime) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("historical_db");

            MongoCollection<Document> collection = db.getCollection(collectionName);

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(observedTime));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Date todayDate = cal.getTime();

            query.put("coaas:day", todayDate);

            query.put("coaas:nsamples", Document.parse("{$lt: " + ContextEntityHandler.BUCKET_SIZE + "}"));

            long currentTime = System.currentTimeMillis();

            JSONObject keysObject = new JSONObject();

            String keySet = "";
            if(keys != null){
                for(int i=0;i<keys.length();i++){
                    String key = keys.getString(i);
                    keysObject.put(key,attributes.opt(key));
                    keySet+=attributes.optString(key, key+"NA")+":";
//                    attributes.remove(key);
                }
            }

            Document updateDocument = new Document();
            updateDocument.put("coaas:time",observedTime);
            updateDocument.put("coaas:arrivalTime",currentTime);
            updateDocument.put("coaas:value", ResponseUtils.convertJSONObject2Document(attributes));

            List<Bson> updateStatement = new ArrayList<>();

            updateStatement.add(Updates.set("coaas:day",todayDate));
            updateStatement.add(Updates.set("coaas:key",keySet));
            updateStatement.add(Updates.push("coaas:samples",updateDocument));
            updateStatement.add(Updates.min("coaas:first",observedTime));
            updateStatement.add(Updates.max("coaas:last",observedTime));
            updateStatement.add(Updates.inc("coaas:nsamples",1));


            UpdateResult updateResult = collection.updateMany(query, Updates.combine(updateStatement), new UpdateOptions().upsert(true));

//            if (updateResult.getMatchedCount() == 0) {
//                Document myDoc = convertJSONObject2Document(entity);
//                collection.insertOne(myDoc);
//                return SQEMResponse.newBuilder().setStatus("200").setBody(String.valueOf(1)).build();
//            }


            return SQEMResponse.newBuilder().setStatus("200").setBody(String.valueOf(updateResult.getMatchedCount())).build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }

    }

    public static SQEMResponse remove(String name) {
        MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
        MongoDatabase db = mongoClient.getDatabase("coaas");
        MongoCollection<Document> collection = db.getCollection(name);
        collection.drop();
        MongoDatabase historicalDb = mongoClient.getDatabase("historical_db");
        collection = historicalDb.getCollection(name);
        collection.drop();
        return SQEMResponse.newBuilder().setStatus("200").build();
    }

    public static SQEMResponse clear(String name) {
        MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
        MongoDatabase db = mongoClient.getDatabase("coaas");
        MongoCollection<Document> collection = db.getCollection(name);
        DeleteResult deleteResult = collection.deleteMany(new BasicDBObject());
        JSONObject jo = new JSONObject();
        long deletedCount = deleteResult.getDeletedCount();

        MongoDatabase historicalDb = mongoClient.getDatabase("historical_db");
        collection = historicalDb.getCollection(name);
        deleteResult = collection.deleteMany(new BasicDBObject());
        jo.put("DeletedCount", deletedCount + deleteResult.getDeletedCount());
        return SQEMResponse.newBuilder().setStatus("200").setBody(jo.toString()).build();
    }

    public static SQEMResponse getAllTypes() {
        MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
        MongoDatabase db = mongoClient.getDatabase("coaas");
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
