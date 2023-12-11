package au.coaas.sqem.handler;

import au.coaas.cqc.proto.CordinatesIndex;
import au.coaas.cqp.proto.ContextEntityType;

import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.CSIServiceGrpc;
import au.coaas.csi.proto.ContextMappingRequest;
import au.coaas.csi.proto.ContextService;
import au.coaas.grpc.client.CSIChannel;
import au.coaas.grpc.client.RWCChannel;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.rwc.proto.Empty;
import au.coaas.rwc.proto.RWCResponse;
import au.coaas.rwc.proto.RWCServiceGrpc;
import au.coaas.sqem.proto.*;
import au.coaas.sqem.util.GeoIndexer;
import au.coaas.sqem.util.ResponseUtils;
import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.util.CollectionDiscovery;

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
            entDoc.put("zeroTime", registerRequest.getObservedTime());
            entDoc.put("updatedTime", LocalDateTime.now());
            entDoc.remove("ObservedTime");

            String hashkey = "";
            JSONArray key = new JSONArray(registerRequest.getKey());
            for (int i = 0; i < key.length(); i++) {
                String idKey = key.getString(i);
                Object idValue = getValueByKey(registerRequest.getJson(), idKey);
                hashkey += key.getString(i) + "@" + idValue.toString().replace("\"","") + ";";
            }
            entDoc.put("hashkey", Utilty.getHashKey(hashkey));

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
                        else {
                            // Means the observed time is already available
                            timestamp = updateRequest.getObservedTime();
                            JSONObject ageObj = new JSONObject();
                            ageObj.put("value", (System.currentTimeMillis() - timestamp)/1000.0);
                            ageObj.put("unitText", "s");
                            data.put("age", ageObj);
                        }

                        UpdateEntityRequest.Builder builder = UpdateEntityRequest.newBuilder()
                                .setJson(data.toString())
                                .setEt(updateRequest.getEt())
                                .setObservedTime(timestamp)
                                .setRetLatency(updateRequest.getRetLatency())
                                .setReportAccess(updateRequest.getReportAccess())
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
                        .setRetLatency(updateRequest.getRetLatency())
                        .setReportAccess(updateRequest.getReportAccess())
                        .setProviderId(updateRequest.getProviderId())
                        .setKey(updateRequest.getKey());

                if(updateRequest.getResolveLocation()) {
                    String ipAddress = resetLocation(response, updateRequest.getProviderId(),
                            updateRequest.getParamHash(), timestamp);
                    SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                            = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance(ipAddress).getChannel());
                    return sqemStub.updateContextEntity(builder.build());
                }

                return updateEntity(builder.build());
            }
            else {
                JSONObject ageObj = new JSONObject();
                ageObj.put("value", (System.currentTimeMillis() - updateRequest.getObservedTime())/1000.0);
                ageObj.put("unitText", "s");

                response.put("age", ageObj);

                if(updateRequest.getResolveLocation()) {
                    String ipAddress = resetLocation(response, updateRequest.getProviderId(),
                            updateRequest.getParamHash(), updateRequest.getObservedTime());
                    SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                            = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance(ipAddress).getChannel());
                    return sqemStub.updateContextEntity(updateRequest.toBuilder()
                            .setJson(response.toString()).build());
                }

                return updateEntity(updateRequest.toBuilder()
                        .setJson(response.toString()).build());
            }
        }
    }

    private static final String[] locationAttrs = new String[]{"geo", "location"};

    // Returns the IP Address to which the entity should be stored in and CSI scheduler should start to run.
    private static String resetLocation(JSONObject response, String cpId, String jobId, long observedTime) {
        // Step 1. Resolve the current location.
        Double latitude = 0.0, longitude = 0.0;
        if(response.has("latitude") && response.has("longitude")) {
            latitude = response.get("latitude") instanceof Double ?
                    response.getDouble("latitude") : Double.valueOf(response.getString("latitude"));
            longitude = response.get("longitude") instanceof Double ?
                    response.getDouble("longitude") : Double.valueOf(response.getString("longitude"));
        }
        else {
            for(String locAttr: locationAttrs){
                Object value = getValueByKey(response, locAttr);
                if(value != null) {
                    latitude = ((JSONObject) value).getDouble("latitude");
                    longitude = ((JSONObject) value).getDouble("longitude");
                    break;
                }
            }
        }

        // Step 2. Update the current SLA and CP Subscription.
        GeoIndexer indexer = GeoIndexer.getInstance();
        CordinatesIndex cpIndex = indexer.getGeoIndex(latitude, longitude);
        String ipAddress = ContextServiceHandler.changeRegisteredLocation(cpId, cpIndex.getIndex());

        // Step 3. Stop Master Scheduler and start it in the relevant edge node.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            CSIServiceGrpc.CSIServiceBlockingStub csiStub
                    = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance().getChannel());
            csiStub.cancelFetchJob(ContextService.newBuilder()
                    .setMongoID(cpId).setJobParamHash(jobId).build());

            // TODO: Adjust the time to start the scheduler using the age or observed time.
            SQEMResponse sla = ContextServiceHandler.getContextServiceInfo(cpId);
            CSIServiceGrpc.CSIServiceBlockingStub edgecsiStub
                    = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance(ipAddress).getChannel());
            ContextService.Builder csMessage = ContextService.newBuilder()
                    .setMongoID(cpId).setJson(sla.getBody())
                    .setCpIndex(cpIndex.getIndex()).setTimes(-1);
            edgecsiStub.createFetchJob(csMessage.build());
        });

        return ipAddress;
    }

    public static CPEdgeDevice edgeHopping(String res, Long lastAssIndex) {
        long idx = checkSubChange(res, lastAssIndex);
        if(idx > 0) {
            EdgeDevice sub_edge = ContextServiceHandler.resolveAttachedEdgeIndex(idx);
            RWCServiceGrpc.RWCServiceBlockingStub rwcStub
                    = RWCServiceGrpc.newBlockingStub(RWCChannel.getInstance().getChannel());
            RWCResponse sub_idx = rwcStub.getNodeIndex(Empty.newBuilder().build());
            if(sub_idx.getStatus().equals("200")) {
                if(Long.valueOf(sub_idx.getBody()) != sub_edge.getIndex()) {
                    return CPEdgeDevice.newBuilder()
                            .setEdgeDevice(sub_edge.toBuilder().setChange(true).build())
                            .setCpIndex(idx).build();
                }
            }
        }
        return CPEdgeDevice.newBuilder().build();
    }

    private static long checkSubChange(String res, Long lastAssIndex) {
        // Step 1. Resolve the current location.
        Double latitude = 0.0, longitude = 0.0;
        JSONObject response = new JSONObject(res);
        if(response.has("latitude") && response.has("longitude")) {
            latitude = response.get("latitude") instanceof Double ?
                    response.getDouble("latitude") : Double.valueOf(response.getString("latitude"));
            longitude = response.get("longitude") instanceof Double ?
                    response.getDouble("longitude") : Double.valueOf(response.getString("longitude"));
        }
        else {
            for(String locAttr: locationAttrs){
                Object value = getValueByKey(response, locAttr);
                if(value != null) {
                    latitude = ((JSONObject) value).getDouble("latitude");
                    longitude = ((JSONObject) value).getDouble("longitude");
                    break;
                }
            }
        }

        GeoIndexer indexer = GeoIndexer.getInstance();
        CordinatesIndex cpIndex = indexer.getGeoIndex(latitude, longitude);
        if(cpIndex.getIndex() == lastAssIndex) return -1;
        return cpIndex.getIndex();
    }

    private static Object getValueByKey(JSONObject item, String key) {
        Object value = item.opt(key);
        return value;
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

            Bson query = Filters.in("hashkey", entIDs.getKeysList());
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

            Document updateFields = new Document();

            JSONObject entityData = new JSONObject(updateRequest.getJson());
            String hashkey = "";

            //Matching the entities by unique keys
            for (String attributeName : entityData.keySet()) {
                Object item = entityData.get(attributeName);
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
                hashkey += key.getString(i) + "@" + idValue.toString().replace("\"","") + ";";
            }
            String hk = Utilty.getHashKey(hashkey);
            updateFields.append("hashkey", hk);
            updateFields.append("updatedTime", LocalDateTime.now());
            updateFields.append("zeroTime", updateRequest.getObservedTime());

            //execute the update
            Document queryDoc = new Document();
            queryDoc.put("$set", updateFields);
            queryDoc.put("$addToSet", new Document("providers", updateRequest.getProviderId()));
            UpdateResult ur = collection.updateMany(query, queryDoc, new UpdateOptions().upsert(false));

            if(updateRequest.getReportAccess().equals("True")){
                Executors.newCachedThreadPool().execute(() -> {
                    String contextId = updateRequest.getEt().getType() + "-" + hk;
                    double entage = entityData.getJSONObject("age").getDouble("value")*1000
                            + updateRequest.getRetLatency();
                    PerformanceLogHandler.insertAccess(contextId, "miss", entage);
                });
            }

            //todo it might be better to create a separate thread and update the historical db there instead of blocking main thread
            ContextEntityHandler.updateHistoricalDatabase(collectionName, key, entityData, query, updateRequest.getObservedTime());

            //if not row is updated, it means no matching. Create a new one.
            if (ur.getMatchedCount() == 0) {
                List<String> providers = new ArrayList<>();
                providers.add(updateRequest.getProviderId());
                updateFields.put("providers", providers);

                collection.insertOne(updateFields);
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

    public static void persistOldContext(String json, String collectionName) {
        try {
            JSONArray context = new JSONArray(json);
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("historical_db");

            MongoCollection<Document> collection = db.getCollection(collectionName);

            List<Document> oldContext = new ArrayList<>();
            for(Object con : context) {
                Document record = Document.parse(con.toString());
                record.remove("_id");
                oldContext.add(record);
            }
            // TODO: this may not store the context in the right order in time.
            collection.insertMany(oldContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
