package au.coaas.sqem.handler;

import au.coaas.cqc.proto.CQCServiceGrpc;
import au.coaas.cqc.proto.PathRequest;
import au.coaas.cqp.proto.SituationFunction;
import au.coaas.cqp.proto.SituationFunctionResponse;
import au.coaas.grpc.client.CQCChannel;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.*;
import au.coaas.cpree.proto.Lookup;
import au.coaas.grpc.client.CPREEChannel;
import au.coaas.sqem.redis.ConnectionPool;
import au.coaas.cpree.proto.CPREEServiceGrpc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.bson.Document;

import org.json.JSONArray;
import org.redisson.api.*;
import org.json.JSONObject;

import java.util.*;
import java.time.Instant;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import au.coaas.sqem.util.Utilty;
import au.coaas.sqem.util.PubSub.Event;
import au.coaas.sqem.util.CacheDataRegistry;
import au.coaas.sqem.util.PubSub.Subscriber;
import au.coaas.sqem.util.enums.ScheduleTasks;
import au.coaas.sqem.util.enums.PerformanceStats;
import au.coaas.sqem.util.enums.DelayCacheLatency;

import static au.coaas.sqem.handler.SituationHandler.findSituationByTitle;
import static java.lang.Character.isDigit;

public class ContextCacheHandler {

    private static final boolean traditionalCaching = false; // Should be false
    private static Logger log = Logger.getLogger(ContextCacheHandler.class.getName());
    private static CacheDataRegistry registry = CacheDataRegistry.getInstance();
    private static final String regex = "^[\\d\\.]*[BGKM%]{0,1}$";
    private static Hashtable<String, Object> currentPerf;

    private static final int numberOfThreads = 10;
    private static final int numberOfItemsPerTask = 100;

    // Considering a free tier node is used
    private static final double cache_node_cost = 0.0;
    private static final double cache_cost_per_gb = 0.3; // In AWS

    public static void updatePerfRegister(double success, double par_miss, double miss) {
        if(currentPerf == null) currentPerf = new Hashtable<>();
        currentPerf.put("200", success);
        currentPerf.put("400", par_miss);
        currentPerf.put("404", miss);
    }

    public static void updatePerfRegister(String key, double value) {
        if(currentPerf == null) currentPerf = new Hashtable<>();
        currentPerf.put(key,value);
    }

    public static Object getCachePerfStat(String key){
        return currentPerf.containsKey(key) ? currentPerf.get(key) : 0.0;
    }

    // Caches different types of context information.
    public static SQEMResponse cacheContext(CacheRequest registerRequest){
        switch(registerRequest.getCacheLevel().toLowerCase()){
            case "situ_function": return cacheSituation(registerRequest);
            case "attribute": return cachePredictiveAttribute(registerRequest);
            case "entity":
            default: return cacheEntity(registerRequest);
        }
    }

    // Caches the predictive context attributes.
    private static SQEMResponse cachePredictiveAttribute(CacheRequest registerRequest) {
        try {
            Document predAttrData = Document.parse(registerRequest.getJson());
            CacheLookUp.Builder temp = registerRequest.getReference().toBuilder();
            temp.setZeroTime(predAttrData.getLong("zeroTime"));

            // registry.updateRegistry(temp.build(), registerRequest.getRefreshLogic());
            // Considering the returned cached lifetime is in seconds and that only if there already exists the cached entity.
            // -1 if ghost entity and to refer infinitely caching.
            double cacheLife = registry.attributeRegistryEntry(registerRequest);

            // The following hashkey is an extension to the entity hashkey indicating what is the additional attribute.
            String entity_hash = registerRequest.getReference().getEt().getType() + "-"
                    + registerRequest.getReference().getHashKey();
            String contextId = entity_hash + "-" + registerRequest.getHashkey();

            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            // Preparing Cache Object
            Document attrJson = new Document();
            attrJson.put("data", predAttrData);
            attrJson.put("entity", entity_hash);
            attrJson.put("ref_attribute", registerRequest.getReference().getKey());

            RBucket<Document> attribute = cacheClient.getBucket(contextId);
            synchronized (ContextCacheHandler.class){
                if(cacheLife>0){
                    // Cached with definite lifetime
                    attribute.set(attrJson, (long) (cacheLife * 1000), TimeUnit.MILLISECONDS);
                    PerformanceLogHandler.logDecisionLatency("cachelife", registerRequest.getCachelife(), DelayCacheLatency.DEFINITE);
                }
                else {
                    // Cached with an indefinite lifetime
                    attribute.set(attrJson);
                    PerformanceLogHandler.logDecisionLatency("cachelife", Long.MAX_VALUE, DelayCacheLatency.INDEFINITE);
                }
            }

            PerformanceLogHandler.logCacheActions(ScheduleTasks.CACHE, contextId, registerRequest.getRefreshLogic());
            return SQEMResponse.newBuilder().setStatus("200").setBody("Predictive attribute cached.").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    // Caches the situation and the confidence.
    private static SQEMResponse cacheSituation(CacheRequest registerRequest){
        try {
            Document contextData = Document.parse(registerRequest.getJson());
            SituationLookUp lookup = registerRequest.getSituReference();
            if (lookup.getSituation() == null) {
                SituationFunctionResponse temp_situ = findSituationByTitle(lookup.getFunction().getFunctionName());
                registerRequest = registerRequest.toBuilder()
                        .setSituReference(lookup.toBuilder().setSituation(temp_situ.getSFunction()))
                        .build();
            }

            registry.updateRegistry(registerRequest.getSituReference());
            String contextId = registerRequest.getSituReference().getFunction().getFunctionName() + "-"
                    + registerRequest.getHashkey();

            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            // Preparing Cache Object
            Document entityJson = new Document();
            entityJson.put("data", contextData);
            entityJson.put("situName", registerRequest.getSituReference().getFunction().getFunctionName());

            RBucket<Document> ent = cacheClient.getBucket(contextId);
            synchronized (ContextCacheHandler.class){
                if(registerRequest.getIndefinite()){
                    // Cached with an indefinite lifetime
                    ent.set(entityJson);
                    if(!traditionalCaching){ // This prevents preemptive evictions as done in context caching
                        Subscriber subscriber = new Subscriber(contextId, registerRequest.getLambdaConf());
                        Event.operation.subscribe(contextId, subscriber);
                    }
                    PerformanceLogHandler.logDecisionLatency("cachelife", Long.MAX_VALUE, DelayCacheLatency.INDEFINITE);
                }
                else {
                    // Cached with definite lifetime
                    ent.set(entityJson, registerRequest.getCachelife(), TimeUnit.MILLISECONDS);
                    PerformanceLogHandler.logDecisionLatency("cachelife", registerRequest.getCachelife(), DelayCacheLatency.DEFINITE);
                }

                // Also caching the situation definition to access it faster.
                RBucket<Document> situation = cacheClient.getBucket(registerRequest.getSituReference().getFunction().getFunctionName());
                if(!situation.isExists()){
                    String situJson = JsonFormat.printer().print(registerRequest.getSituReference().getSituation());
                    situation.set(Document.parse(situJson));
                }
            }
            PerformanceLogHandler.logCacheActions(ScheduleTasks.CACHE, contextId, "reactive");
            return SQEMResponse.newBuilder().setStatus("200").setBody("Situation Cached").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    private static SQEMResponse cacheEntity(CacheRequest registerRequest){
        try {
            Document contextData = Document.parse(registerRequest.getJson());
            CacheLookUp.Builder temp = registerRequest.getReference().toBuilder();
            temp.setZeroTime(contextData.getLong("zeroTime"));

            registry.updateRegistry(temp.build(), registerRequest.getRefreshLogic());
            String contextId = registerRequest.getReference().getEt().getType() + "-"
                    + registerRequest.getReference().getHashKey();

            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            // Preparing Cache Object
            Document entityJson = new Document();
            entityJson.put("data", contextData);
            entityJson.put("entityType", registerRequest.getReference().getEt().getType());
            entityJson.put("serviceId", registerRequest.getReference().getServiceId());

            RBucket<Document> ent = cacheClient.getBucket(contextId);
            synchronized (ContextCacheHandler.class){
                if(!registerRequest.getIndefinite()){
                    // Cached with definite lifetime
                    ent.set(entityJson, registerRequest.getCachelife(), TimeUnit.MILLISECONDS);
                    PerformanceLogHandler.logDecisionLatency("cachelife", registerRequest.getCachelife(), DelayCacheLatency.DEFINITE);
                }
                else {
                    // Cached with an indefinite lifetime
                    ent.set(entityJson);
                    if(!traditionalCaching){ // This prevents preemptive evictions as done in context caching
                        Subscriber subscriber = new Subscriber(contextId, registerRequest.getLambdaConf());
                        Event.operation.subscribe(contextId, subscriber);
                    }
                    PerformanceLogHandler.logDecisionLatency("cachelife", Long.MAX_VALUE, DelayCacheLatency.INDEFINITE);
                }
            }

            PerformanceLogHandler.logCacheActions(ScheduleTasks.CACHE, contextId, registerRequest.getRefreshLogic());
            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity cached.").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    // Updating cached context for an entity
    // Done
    public static SQEMResponse refreshContext(CacheRefreshRequest updateRequest) {
        switch(updateRequest.getContextLevel().toLowerCase()){
            case "situ_function": return refreshSituation(updateRequest);
            case "attribute": return refreshAttribute(updateRequest);
            case "entity":
            default: return refreshEntity(updateRequest);
        }
    }

    private static SQEMResponse refreshAttribute(CacheRefreshRequest updateRequest) {
        try {
            String entity_hash = updateRequest.getReference().getEt().getType() + "-"
                    + updateRequest.getReference().getHashKey();
            String contextId = entity_hash + "-" + updateRequest.getAddAttributeName();

            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
            Document data =  Document.parse(updateRequest.getJson());
            RBucket<Document> ent = cacheClient.getBucket(contextId);

            RLock lock;
            RFuture<Document> refreshStatus;

            synchronized (ContextCacheHandler.class){
                lock = cacheClient.getFairLock("refreshLock");
                Document attrJson = ent.get();
                attrJson.replace("data", data);
                Double cacheLife = Double.valueOf(registry.updateRegistry(updateRequest));
                if(cacheLife < 0) {
                    refreshStatus = ent.getAndSetAsync(attrJson);
                } else refreshStatus = ent.getAndSetAsync(attrJson, (long) (cacheLife * 1000), TimeUnit.MILLISECONDS);
            }

            refreshStatus.whenCompleteAsync((res, exception) -> {
                lock.unlockAsync();
            });

            PerformanceLogHandler.logCacheActions(ScheduleTasks.REFRESH, contextId, updateRequest.getRefPolicy());
            return SQEMResponse.newBuilder().setStatus("200").setBody("Attribute refreshed.").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    public static void initiateAttrRefresh (CacheRefreshRequest request, String refAttr) {
        CQCServiceGrpc.CQCServiceBlockingStub cqcStub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        String contextId = request.getReference().getEt().getType() + "-" + request.getReference().getHashKey();
        JSONObject cacheRes = new JSONObject(simpleRetrieval(contextId).toJson());

        PathRequest.Builder path_request = PathRequest.newBuilder()
                .setHeading(cacheRes.getDouble("heading"))
                .setSpeed(cacheRes.getDouble("speed"))
                .setRequest(request);

        if(cacheRes.has(refAttr)) {
            path_request.setLatitude(cacheRes.getJSONObject(refAttr).getDouble("latitude"));
            path_request.setLongitude(cacheRes.getJSONObject(refAttr).getDouble("longitude"));
        }
        else {
            path_request.setLatitude(cacheRes.getDouble("latitude"));
            path_request.setLongitude(cacheRes.getDouble("longitude"));
        }

        cqcStub.refreshPredictedPath(path_request.build());
    }

    private static SQEMResponse refreshEntity(CacheRefreshRequest updateRequest) {
        try {
            String contextId = updateRequest.getReference().getEt().getType() + "-"
                    + updateRequest.getReference().getHashKey();

            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
            Document data =  Document.parse(updateRequest.getJson());
            RBucket<Document> ent = cacheClient.getBucket(contextId);

            RLock lock;
            RFuture<Document> refreshStatus;

            synchronized (ContextCacheHandler.class){
                lock = cacheClient.getFairLock("refreshLock");
                Document cacheObject = ent.get();
                cacheObject.replace("data", data);
                refreshStatus = ent.getAndSetAsync(cacheObject);
                registry.updateRegistry(updateRequest.getReference().toBuilder()
                        .setZeroTime(data.getLong("zeroTime")).build());
            }

            refreshStatus.whenCompleteAsync((res, exception) -> {
                lock.unlockAsync();
            });

            PerformanceLogHandler.logCacheActions(ScheduleTasks.REFRESH, contextId, updateRequest.getRefPolicy());
            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity refreshed.").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    private static SQEMResponse refreshSituation(CacheRefreshRequest updateRequest) {
        try {
            String contextId = updateRequest.getSituReference().getFunction().getFunctionName() + "-"+
                    updateRequest.getSituReference().getUniquehashkey();
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
            Document data =  Document.parse(updateRequest.getJson());
            RBucket<Document> situ = cacheClient.getBucket(contextId);

            RLock lock;
            RFuture<Document> refreshStatus;

            synchronized (ContextCacheHandler.class){
                lock = cacheClient.getFairLock("refreshLock");
                Document cacheObject = situ.get();
                cacheObject.replace("data", data.get("outcome"));
                refreshStatus = situ.getAndSetAsync(cacheObject);
                registry.updateRegistry(updateRequest.getSituReference().toBuilder()
                        .setZeroTime(data.getLong("zeroTime")).build());
            }

            refreshStatus.whenCompleteAsync((res, exception) -> {
                lock.unlockAsync();
            });

            PerformanceLogHandler.logCacheActions(ScheduleTasks.REFRESH, contextId, "reactive");
            return SQEMResponse.newBuilder().setStatus("200").setBody("Situation refreshed.").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    public static boolean updateSituation(String entType, String entHash, String situName, String situHash) {
        return registry.setMissingEntityRefs(entType, entHash, situName, situHash);
    }

    // Done
    public static Empty toggleRefreshLogic(RefreshUpdate request) {
        try {
            synchronized (ContextCacheHandler.class) {
                registry.changeRefreshLogic(request);
                String contextId = request.getLookup().getEt().getType() + "-" + request.getHashkey();
                PerformanceLogHandler.logCacheActions(ScheduleTasks.TOGGLE, contextId, request.getRefreshLogic());
            }
        } catch (Exception e) {
            log.severe("Couldn't toggle refresh logic: " + e.getMessage());
        }
        return null;
    }

    // Evicts an entity after a delay (remaining lifetime) set by the next method.
    public static void fullyEvict(ScheduleTask request){
        // Remove from registry
        String contextId = request.getLookup().getEt().getType() + "-"
                + request.getHashkey();

        synchronized (ContextCacheHandler.class) {
            CPREEServiceGrpc.CPREEServiceBlockingStub stub
                    = CPREEServiceGrpc.newBlockingStub(CPREEChannel.getInstance().getChannel());
            stub.stopRefreshing(Lookup.newBuilder().setContextId(contextId).build());

            registry.removeFromRegistry(request.getLookup());
        }
        RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

        RReadWriteLock rwLock = cacheClient.getReadWriteLock("evictLock");
        RLock lock = rwLock.writeLock();

        cacheClient.getKeys().deleteAsync(contextId);
        RFuture<Boolean> evictStatus = cacheClient.getBucket(contextId).deleteAsync();

        evictStatus.whenCompleteAsync((res, exception) -> {
            lock.unlockAsync();
        });

        PerformanceLogHandler.logCacheActions(ScheduleTasks.EVICT, contextId, null);
    }

    // Evicts an entity by lookup.
    public static SQEMResponse evictEntity(CacheLookUp request) {
        // Ideally the initial evict should push the context into ghost cache
        CacheLookUpResponse result= registry.lookUpRegistry(request);

        if(result.getIsCached()){
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            RReadWriteLock rwLock = cacheClient.getReadWriteLock("evictLock");
            RLock lock = rwLock.writeLock();
            RFuture<Boolean> evictStatus;

            if(result.getRemainingLife() > 0){
                // TODO:
                // Check whether there are queries remaining in queue to access this context
                Instant future = Instant.now().plusSeconds(result.getRemainingLife());
                evictStatus = cacheClient.getBucket(request.getEt().getType() + "-" +
                                request.getHashKey())
                        .expireAsync(future);

                Utilty.schedulTask(ScheduleTasks.EVICT,
                        ScheduleTask.newBuilder()
                                .setLookup(request)
                                .setHashkey(request.getHashKey()).build(),
                        result.getRemainingLife() < 0 ? 0 : result.getRemainingLife());
            }
            else {
                String contextId = request.getEt().getType() + "-" + request.getHashKey();
                synchronized (ContextCacheHandler.class){
                    CPREEServiceGrpc.CPREEServiceBlockingStub stub
                            = CPREEServiceGrpc.newBlockingStub(CPREEChannel.getInstance().getChannel());

                    stub.stopRefreshing(Lookup.newBuilder().setContextId(contextId).build());
                    registry.removeFromRegistry(request);
                }
                cacheClient.getKeys().deleteAsync(contextId);
                evictStatus = cacheClient.getBucket(contextId).deleteAsync();
                PerformanceLogHandler.logCacheActions(ScheduleTasks.EVICT, contextId, null);
            }

            evictStatus.whenCompleteAsync((res, exception) -> {
                lock.unlockAsync();
            });

            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity evicted.").build();
        }

        return SQEMResponse.newBuilder().setStatus("404").setBody("Nothing to Evict.").build();
    }

    // Evicts an entity by hashkey.
    // This is called to evict an entity when it fails to refresh it.
    public static SQEMResponse evictContext(String hashkey) {
        return evictContext(hashkey, !Event.operation.checkChannel(hashkey));
    }

    // Evicts any context by hashkey.
    // Execution point for keyspace events, AR less than a threshold event, or from above.
    public static SQEMResponse evictContext(String contextId, boolean definite) {
        // Entity Eviction
        if(Utilty.isEntity(contextId)) {
            // This hashkey is entityType-hash format.
            // This function assumes that the hash key is unique across all the contexts (irrespective of the context service, etc.)
            // Ideally the initial evict should push the context into ghost cache
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            RBucket<Object> bucket = cacheClient.getBucket(contextId);
            Document cacheObject = (Document) bucket.get();

            RReadWriteLock rwLock = cacheClient.getReadWriteLock("evictLock");
            RLock lock = rwLock.writeLock();
            RFuture<Boolean> evictStatus;

            synchronized (ContextCacheHandler.class){
                CPREEServiceGrpc.CPREEServiceBlockingStub stub
                        = CPREEServiceGrpc.newBlockingStub(CPREEChannel.getInstance().getChannel());
                stub.stopRefreshing(Lookup.newBuilder()
                        .setContextId(contextId).build());

                registry.removeFromRegistry(cacheObject.getString("serviceId"),
                        (contextId.split("-"))[1], cacheObject.getString("entityType"));
                if(!definite) Event.operation.deleteChannel(contextId);
            }

            cacheClient.getKeys().deleteAsync(contextId);
            evictStatus = bucket.deleteAsync();

            evictStatus.whenCompleteAsync((res, exception) -> {
                lock.unlockAsync();
            });

            PerformanceLogHandler.logCacheActions(ScheduleTasks.EVICT, contextId, null);
            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity evicted.").build();
        }
        // Situation Eviction
        else {
            // This hashkey is situationName-hash format.
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            RBucket<Object> bucket = cacheClient.getBucket(contextId);
            Document cacheObject = (Document) bucket.get();

            RReadWriteLock rwLock = cacheClient.getReadWriteLock("evictLock");
            RLock lock = rwLock.writeLock();
            RFuture<Boolean> evictStatus;

            boolean evictType;
            synchronized (ContextCacheHandler.class){
                evictType = registry.removeFromRegistry(cacheObject.getString("situName"), (contextId.split("-"))[1]);
                if(!definite) Event.operation.deleteChannel(contextId);
            }

            cacheClient.getKeys().deleteAsync(contextId);
            evictStatus = bucket.deleteAsync();

            // Evicting the situation type
            if(evictType) {
                RBucket<Object> defBucket = cacheClient.getBucket(cacheObject.getString("situName"));
                cacheClient.getKeys().deleteAsync(cacheObject.getString("situName"));
                defBucket.deleteAsync();
            }

            evictStatus.whenCompleteAsync((res, exception) -> {
                lock.unlockAsync();
            });

            PerformanceLogHandler.logCacheActions(ScheduleTasks.EVICT, contextId, null);
            return SQEMResponse.newBuilder().setStatus("200").setBody("Situation evicted.").build();
        }
    }

    // Lookup in  context registry whether the entity is cached
    // Done
    public static SQEMResponse lookUp(CacheLookUp request){
        CacheLookUpResponse result = registry.lookUpRegistry(request);
        return SQEMResponse.newBuilder().setStatus("200")
                .setCacheresponse(result).build();
    }

    // Retrieve entity context from cache
    // Done

    private static Document simpleRetrieval (String contextId) {
        RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
        RReadWriteLock rwLock = cacheClient.getReadWriteLock("readLock");
        RLock lock = rwLock.readLock();
        RBucket<Document> ent = cacheClient.getBucket(contextId);
        return ent.get().get("data", Document.class);
    }

    public static SQEMResponse retrieveFromCache(CacheLookUp request) {
        long startTime = System.currentTimeMillis();
        CacheLookUpResponse result = registry.lookUpRegistry(request);

        // Hit from Cache Registry
        if(!result.getHashkey().isEmpty() && result.getIsCached() && result.getIsValid()){
            // This means that either the entity or all entities created using a context provider are cached and valid.
            try{
                RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

                RReadWriteLock rwLock = cacheClient.getReadWriteLock("readLock");
                RLock lock = rwLock.readLock();

                List<Document> entityContext = Collections.synchronizedList(new ArrayList<>());
                Map<String, String> paramList = request.getParamsMap();
                ArrayList<String> keys = new ArrayList<>(paramList.keySet());

                List<String> hitkeys = Collections.synchronizedList(new ArrayList<>());
                List<String> misskeys = Collections.synchronizedList(new ArrayList<>());

                if(result.getHashkey().startsWith("entity")){
                    RBucket<Document> ent = cacheClient.getBucket((result.getHashkey().split(":"))[1]);
                    // Should check if conditions meet
                    Document conEntity = ent.get().get("data", Document.class);
//                    for(int i=0; i < keys.size(); i++){
//                        if(conEntity.containsKey(keys.get(i)) &&
//                                conEntity.get(keys.get(i)).toString()
//                                        .equals(paramList.get(keys.get(i)).replace("\"",""))){
//                            continue;
//                        }
//                        else {
//                            lock.unlockAsync();
//                            long endTime = System.currentTimeMillis();
//                            Executors.newCachedThreadPool().submit(()
//                                    -> logCacheSearch("404", request.getServiceId(),
//                                    request.getUniformFreshness(), endTime - startTime));
//
//                            return SQEMResponse.newBuilder().setStatus("404")
//                                    .setHashKey(result.getHashkey())
//                                    .setBody("Entity not found.").build();
//                        }
//                    }
                    entityContext.add(conEntity);
                    hitkeys.add(result.getHashkey());
                }
                else {
                    int entitiesCached = result.getHashKeysCount();
                    int numberOfIterations = (int)(entitiesCached/numberOfItemsPerTask) + 1;
                    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

                    for (int factor = 0; factor<numberOfIterations; factor++)
                    {
                        int finalFactor = factor;
                        executorService.submit(() -> {
                            int start = numberOfItemsPerTask * finalFactor;
                            int end =  Math.min(entitiesCached, start + numberOfItemsPerTask);

                            // Sending cache hit or miss record to performance monitor.
                            for(int i = start; i < end; i++){
                                RBucket<Document> ent = cacheClient.getBucket(
                                        (result.getHashKeys(i).split(":"))[1]);
                                // Should check if conditions meet
                                boolean isValid = true;
                                Document conEntity = ent.get().get("data", Document.class);
                                List<String> ops = request.getOperatorsList();
                                
                                for(int j=0; j < keys.size(); j++){
                                    if(conEntity.containsKey(keys.get(j))) {
                                        Object entValue = conEntity.get(keys.get(j));
                                        String paramValue = paramList.get(keys.get(j)).replace("\"", "");
                                        switch(ops.get(j)){
                                            case ">":
                                                if(Double.valueOf(entValue.toString()) > Double.valueOf(paramValue)) continue;
                                                break;
                                            case ">=":
                                                if(Double.valueOf(entValue.toString()) >= Double.valueOf(paramValue)) continue;
                                                break;
                                            case "<":
                                                if(Double.valueOf(entValue.toString()) < Double.valueOf(paramValue)) continue;
                                                break;
                                            case "<=":
                                                if(Double.valueOf(entValue.toString()) <= Double.valueOf(paramValue)) continue;
                                                break;
                                            case "=":
                                            default:
                                                if(entValue.toString().equals(paramValue)) continue;
                                                break;
                                        }
                                    }

                                    isValid = false;
                                    misskeys.add(result.getHashKeys(i));
                                    break;
                                }

                                if(isValid) {
                                    entityContext.add(conEntity);
                                    hitkeys.add(result.getHashKeys(i));
                                }
                            }
                        });
                    }
                    executorService.shutdown();

                    try {
                        executorService.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
                    } catch (InterruptedException e) {
                        log.severe("Executor failed to execute with error: " + e.getMessage());
                    }
                }

                lock.unlockAsync();

                long endTime = System.currentTimeMillis();

                if(entityContext.size() > 0 && misskeys.isEmpty()){
                    Executors.newCachedThreadPool().submit(()
                            -> logCacheSearch("200", request.getServiceId(),
                            request.getUniformFreshness(), endTime - startTime));
                    // Rest of the meta data is not needed when the context is returned from cache.
                    return SQEMResponse.newBuilder().setStatus("200")
                            .setHashKey(hitkeys.stream().collect(Collectors.joining(",", "", "")))
                            .setBody((new JSONArray(entityContext)).toString()).build();
                }
                else if(hitkeys.size() > 0 && misskeys.size()>0){
                    // Partial Miss
                    Executors.newCachedThreadPool().submit(()
                            -> logCacheSearch("400", request.getServiceId(),
                            request.getUniformFreshness(), endTime - startTime));
                    return SQEMResponse.newBuilder().setStatus("400")
                            .setHashKey(hitkeys.stream().collect(Collectors.joining(",", "", "")))
                            .setMisskeys(misskeys.stream().collect(Collectors.joining(",", "", "")))
                            .setBody("Entity not found.").build();
                }
                else {
                    // Fill miss
                    Executors.newCachedThreadPool().submit(()
                            -> logCacheSearch("404", request.getServiceId(),
                            request.getUniformFreshness(), endTime - startTime));
                    return SQEMResponse.newBuilder().setStatus("404")
                            .setHashKey(misskeys.stream().collect(Collectors.joining(",", "", "")))
                            .setBody("Entity not found.").build();
                }
            }
            catch(Exception ex){
                return SQEMResponse.newBuilder().setStatus("500").build();
            }
        }
        // Partial Miss from Cache Registry
        else if(!result.getHashkey().equals("") && result.getIsCached() && !result.getIsValid()){
            // There is some record in cache. But bot valid - Partial Miss.
            long endTime = System.currentTimeMillis();
            Executors.newCachedThreadPool().submit(()
                    -> logCacheSearch("400", request.getServiceId(),
                    request.getUniformFreshness(), endTime - startTime));
            return SQEMResponse.newBuilder().setStatus("400")
                    .setMeta(result.getRefreshLogic()) // This can be null if some entities under a CS were invalid.
                    .setMisskeys(result.getMissKeysList().isEmpty() ? "" : result.getMissKeysList().stream().collect(Collectors.joining(",", "", "")))
                    .setHashKey(result.getHashkey().startsWith("entity")? result.getHashkey():
                                    result.getHashKeysList().isEmpty() ? "" :
                                    result.getHashKeysList().stream()
                                            .collect(Collectors.joining(",", "", "")))
                    .build(); // Remember the hash has a prefix.
        }

        // Complete Miss
        long endTime = System.currentTimeMillis();
        Executors.newCachedThreadPool().submit(()
                -> logCacheSearch("404", request.getServiceId(),
                request.getUniformFreshness(), endTime - startTime));
        return SQEMResponse.newBuilder().setStatus("404")
                .setHashKey(result.getHashkey()).build();
    }

    // Retrieve situation from cache
    public static SQEMResponse retrieveFromCache(SituationLookUp request) throws InvalidProtocolBufferException {
        long startTime = System.currentTimeMillis();
        CacheLookUpResponse result = registry.lookUpSituation(request);
        String hashkey = request.getFunction().getFunctionName() + "-" + request.getUniquehashkey();

        // Hit from Cache Registry
        if(result.getIsCached() && result.getIsValid()){
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
            RReadWriteLock rwLock = cacheClient.getReadWriteLock("readLock");

            RLock lock = rwLock.readLock();
            RBucket<Document> situ = cacheClient.getBucket(hashkey);
            Document conSituation = situ.get().get("data", Document.class);
            lock.unlockAsync();

            long endTime = System.currentTimeMillis();

            Executors.newCachedThreadPool().submit(()
                    -> logCacheSearch("200", request.getFunction().getFunctionName(),
                    null, endTime - startTime));
            // Rest of the metadata is not needed when the context is returned from cache.
            return SQEMResponse.newBuilder().setStatus("200")
                    .setHashKey(hashkey)
                    .setBody(conSituation.toString()).build();
        }
        else if(result.getIsCached() && !result.getIsValid()) {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
            RReadWriteLock rwLock = cacheClient.getReadWriteLock("readLock");

            RLock lock = rwLock.readLock();
            RBucket<Document> situ = cacheClient.getBucket(request.getFunction().getFunctionName());

            SQEMResponse.Builder response = SQEMResponse.newBuilder().setStatus("400")
                    .setMisskeys(hashkey)
                    .setBody("Situation is not up to date.");

            long endTime = System.currentTimeMillis();
            if(situ.isExists()){
                String json = situ.get().toJson();
                SituationFunction.Builder structBuilder = SituationFunction.newBuilder();
                JsonFormat.parser().ignoringUnknownFields().merge(json, structBuilder);
                response.setSituation(structBuilder);
            }
            lock.unlockAsync();

            Executors.newCachedThreadPool().submit(()
                    -> logCacheSearch("400", request.getFunction().getFunctionName(),
                    null, endTime - startTime));

            return response.build();
        }

        // Complete Miss
        RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
        RReadWriteLock rwLock = cacheClient.getReadWriteLock("readLock");

        RLock lock = rwLock.readLock();
        RBucket<Document> situ = cacheClient.getBucket(request.getFunction().getFunctionName());

        SQEMResponse.Builder response = SQEMResponse.newBuilder().setStatus("404")
                .setMisskeys(hashkey)
                .setBody("Situation is not cached.");

        long endTime = System.currentTimeMillis();
        if(situ.isExists()){
            String json = situ.get().toJson();
            SituationFunction.Builder structBuilder = SituationFunction.newBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(json, structBuilder);
            response.setSituation(structBuilder);
        }
        lock.unlockAsync();

        Executors.newCachedThreadPool().submit(()
                -> logCacheSearch("404", request.getFunction().getFunctionName(),
                null, endTime - startTime));

        return response.build();
    }

    // Done
    private static void logCacheSearch(String status, String csId, String freshness, long responseTime){
        Statistic stat;
        if(freshness != null){
            JSONObject frsh = new JSONObject(freshness);
            stat = Statistic.newBuilder().setMethod("cacheSearch").setStatus(status).setTime(responseTime)
                    .setIdentifier(csId).setEarning(frsh.getDouble("fthresh")).build();

        }
        else {
            stat = Statistic.newBuilder().setMethod("cacheSearch").setStatus(status).setTime(responseTime)
                    .setIdentifier(csId).setEarning(0.0).build(); // here csId is the function name.
        }
        PerformanceLogHandler.coassPerformanceRecord(stat);
    }

    // Done
    public static void updatePerformanceStats(Map perfMetrics, PerformanceStats key) {
        try {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
            RMap<String, Object> map = cacheClient.getMap(key.toString());
            
            synchronized (ContextCacheHandler.class){
                RLock lock = cacheClient.getFairLock("refreshLock");
                map.putAll(perfMetrics);
                lock.unlockAsync();
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    // Done
    public static SQEMResponse getPerformanceStats(String statKey, PerformanceStats perf_stats){
        try {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            RReadWriteLock rwLock = cacheClient.getReadWriteLock("readLock");
            RLock lock = rwLock.readLock();

            RMap<String, Object> map = cacheClient.getMap(perf_stats.toString());
            Object stat = map.get(statKey);
            lock.unlockAsync();

            return SQEMResponse.newBuilder()
                    .setStatus("200").setBody(stat.toString())
                    .build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder()
                    .setStatus("500").setBody(e.getMessage())
                    .build();
        }
    }

    // Clears the context cache
    // Done
    public static SQEMResponse clearCache() {
        RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
        cacheClient.getKeys().flushdb();
        log.info("Cleared the context Cache");

        return SQEMResponse.newBuilder().setStatus("200").setBody("Cleared context cache").build();
    }

    // Done
    public static Document getMemoryUtility() {
        try {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
            String script = "return redis.pcall('info','memory')";
            String result = cacheClient.getScript().eval(RScript.Mode.READ_ONLY,
                    script, RScript.ReturnType.STATUS);

            String[] resultset = result.split("\r\n");
            Document cachestats = new Document();

            for(int i = 1; i<resultset.length; i++){
                String[] keyval = resultset[i].split(":");
                if(keyval[1].matches(regex)){
                    char lastChar = keyval[1].charAt(keyval[1].length()-1);
                    String unit = isDigit(lastChar)? "Number": convertToUnit(lastChar);
                    String value = unit.equals("Number") ? keyval[1] :
                            keyval[1].substring(0, keyval[1].length()-1);

                    Document setValue = new Document();
                    setValue.put("value", value.contains(".") ? Double.parseDouble(value) : Long.parseLong(value));
                    setValue.put("unit", unit);

                    if(keyval[0].equals("used_memory_dataset")){
                        ContextCacheHandler.updatePerfRegister("cacheUtility", Long.parseLong(value)); // Bytes
                        double cacheCost = (cache_node_cost/60);
                        if(Long.parseLong(value) > 354334802){
                            cacheCost += (Long.parseLong(value) * (cache_cost_per_gb / Math.pow(1024,3))) ;
                        }
                        ContextCacheHandler.updatePerfRegister("cacheCost", cacheCost);
                        ContextCacheHandler.updatePerfRegister("costPerByte", 0);
                    }

                    cachestats.put(keyval[0],setValue);
                }
            }

            return cachestats;

        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    // Done
    public static CachePerformance getCachePerformance(){
        if(currentPerf != null){
            // All values returned are in Seconds
            return CachePerformance.newBuilder()
                    .setStatus("200")
                    .setHitLatency(currentPerf.contains("200") ? (double)currentPerf.get("200")/1000.0 : 0.0)
                    .setMissLatency(currentPerf.contains("404") ? (double)currentPerf.get("404")/1000.0 : 0.0)
                    .setPartialMissLatency(currentPerf.contains("400") ? (double)currentPerf.get("400")/1000.0 : 0.0)
                    .setCacheCost(currentPerf.contains("cacheCost") ? (double)currentPerf.get("cacheCost") : 0.0)
                    .setCostPerByte(currentPerf.contains("costPerByte") ? (double)currentPerf.get("costPerByte") : 0.0)
                    .setProcessCost(currentPerf.contains("processCost") ? (double)currentPerf.get("processCost") : 0.0)
                    .setCacheUtility(currentPerf.contains("cacheUtility") ? (double)currentPerf.get("cacheUtility") : 0.0).build();
        }
        else return CachePerformance.newBuilder().setStatus("404").build();
    }

    // Done
    public static Empty logCacheDecisionLatency(DecisionLog request){
        PerformanceLogHandler.logDecisionLatency(request.getType(), request.getLatency(),
                request.getIndefinite()? DelayCacheLatency.INDEFINITE : DelayCacheLatency.DEFINITE);
        return null;
    }

    // Done
    public static Empty logCacheDecision(ContextCacheDecision json){
        PerformanceLogHandler.logCacheDecision(new JSONObject(json.getJson()), json.getLevel());
        return null;
    }

    // Done
    private static String convertToUnit(char unit) {
        switch (unit) {
            case '%':
                return "Percentage";
            case 'B':
                return "Bytes";
            case 'K':
                return "KiloBytes";
            case 'M':
                return "MegaBytes";
            case 'G':
                return "GigaBytes";
            default:
                return "Unknown";
        }
    }
}


