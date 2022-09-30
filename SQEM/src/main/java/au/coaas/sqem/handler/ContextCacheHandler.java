package au.coaas.sqem.handler;

import au.coaas.sqem.proto.*;
import au.coaas.sqem.redis.ConnectionPool;

import org.bson.Document;

import org.json.JSONObject;
import org.redisson.api.*;

import java.util.Map;
import java.time.Instant;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.concurrent.Executors;

import au.coaas.sqem.util.Utilty;
import au.coaas.sqem.util.PubSub.Event;
import au.coaas.sqem.util.CacheDataRegistry;
import au.coaas.sqem.util.PubSub.Subscriber;
import au.coaas.sqem.util.enums.ScheduleTasks;
import au.coaas.sqem.util.enums.PerformanceStats;
import au.coaas.sqem.util.enums.DelayCacheLatency;

import static java.lang.Character.isDigit;

public class ContextCacheHandler {

    private static Logger log = Logger.getLogger(ContextCacheHandler.class.getName());
    private static CacheDataRegistry registry = CacheDataRegistry.getInstance();
    private static final String regex = "^[\\d\\.]*[BGKM%]{0,1}$";
    private static Hashtable<String, Object> currentPerf;

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

    // Caches all context under an entity
    public static SQEMResponse cacheEntity(CacheRequest registerRequest){
        try {
            String hashKey = String.valueOf(registry.updateRegistry(registerRequest.getReference(),
                    registerRequest.getRefreshLogic()));

            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            // Preparing Cache Object
            Document entityJson = new Document();
            entityJson.put("data", Document.parse(registerRequest.getJson()));
            entityJson.put("entityType", registerRequest.getReference().getEt().getType());
            entityJson.put("serviceId", registerRequest.getReference().getServiceId());

            RBucket<Document> ent = cacheClient.getBucket(hashKey);
            synchronized (ContextCacheHandler.class){
                if(!registerRequest.getIndefinite()){
                    // Cached with definite lifetime
                    ent.set(entityJson, registerRequest.getCachelife(), TimeUnit.MILLISECONDS);
                    PerformanceLogHandler.logDecisionLatency("cachelife", registerRequest.getCachelife(), DelayCacheLatency.DEFINITE);
                }
                else {
                    // Cached with an indefinite lifetime
                    ent.set(entityJson);
                    Subscriber subscriber = new Subscriber(hashKey, registerRequest.getLambdaConf());
                    Event.operation.subscribe(hashKey, subscriber);
                    PerformanceLogHandler.logDecisionLatency("cachelife", Long.MAX_VALUE, DelayCacheLatency.INDEFINITE);
                }
            }

            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity cached.").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    // Updating cached context for an entity
    public static SQEMResponse refreshEntity(CacheRefreshRequest updateRequest) {
        try {
            String hashKey = String.valueOf(registry.updateRegistry(updateRequest.getReference()));

            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
            Document data =  Document.parse(updateRequest.getJson());
            RBucket<Document> ent = cacheClient.getBucket(hashKey);

            RLock lock;
            RFuture<Document> refreshStatus;

            synchronized (ContextCacheHandler.class){
                lock = cacheClient.getFairLock("refreshLock");
                Document cacheObject = ent.get();
                cacheObject.replace("data", data);
                refreshStatus = ent.getAndSetAsync(cacheObject);
            }

            refreshStatus.whenCompleteAsync((res, exception) -> {
                lock.unlockAsync();
            });

            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity refreshed.").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    public static Empty toggleRefreshLogic(RefreshUpdate request) {
        try {
            synchronized (ContextCacheHandler.class) {
                registry.changeRefreshLogic(request);
            }
        } catch (Exception e) {
            log.severe("Couldn't toggle refresh logic: " + e.getMessage());
        }
        return null;
    }

    public static void fullyEvict(ScheduleTask request){
        // Remove from registry
        synchronized (ContextCacheHandler.class) {
            registry.removeFromRegistry(request.getLookup());
        }
        RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

        RReadWriteLock rwLock = cacheClient.getReadWriteLock("evictLock");
        RLock lock = rwLock.writeLock();

        cacheClient.getKeys().deleteAsync(request.getHashkey());
        RFuture<Boolean> evictStatus = cacheClient.getBucket(request.getHashkey()).deleteAsync();

        evictStatus.whenCompleteAsync((res, exception) -> {
            lock.unlockAsync();
        });
    }

    // Evicts an entity by hash key
    public static SQEMResponse evictEntity(CacheLookUp request) {
        // Ideally the initial evict should push the context into ghost cache
        CacheLookUpResponse result= registry.lookUpRegistry(request);

        if(result.getHashkey() != ""){
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            RReadWriteLock rwLock = cacheClient.getReadWriteLock("evictLock");
            RLock lock = rwLock.writeLock();
            RFuture<Boolean> evictStatus;

            if(result.getRemainingLife() > 0){
                // Todo:
                // Check whether there are queries remaining in queue to access this context
                Instant future = Instant.now().plusSeconds(result.getRemainingLife());
                evictStatus = cacheClient.getBucket(result.getHashkey())
                        .expireAsync(future);
                Utilty.schedulTask(ScheduleTasks.EVICT,
                        ScheduleTask.newBuilder()
                                .setLookup(request)
                                .setHashkey(result.getHashkey()).build(),
                        result.getRemainingLife());
            }
            else {
                synchronized (ContextCacheHandler.class){
                    registry.removeFromRegistry(request);
                }
                cacheClient.getKeys().deleteAsync(result.getHashkey());
                evictStatus = cacheClient.getBucket(result.getHashkey()).deleteAsync();
            }

            evictStatus.whenCompleteAsync((res, exception) -> {
                lock.unlockAsync();
            });

            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity evicted.").build();
        }

        return SQEMResponse.newBuilder().setStatus("404").setBody("Nothing to Evict.").build();
    }

    public static SQEMResponse evictEntity(String hashkey, boolean definite) {
        // This function assumes that the hash key is unique across all the contexts (irrespective of the context service, etc.)
        // Ideally the initial evict should push the context into ghost cache
        RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

        RBucket<Object> bucket = cacheClient.getBucket(hashkey);
        Document cacheObject = (Document) bucket.get();

        RReadWriteLock rwLock = cacheClient.getReadWriteLock("evictLock");
        RLock lock = rwLock.writeLock();
        RFuture<Boolean> evictStatus;

        synchronized (ContextCacheHandler.class){
            registry.removeFromRegistry(cacheObject.getString("serviceId"),
                    hashkey, cacheObject.getString("serviceId"));
            if(!definite) Event.operation.deleteChannel(hashkey);
        }

        cacheClient.getKeys().deleteAsync(hashkey);
        evictStatus = bucket.deleteAsync();

        evictStatus.whenCompleteAsync((res, exception) -> {
            lock.unlockAsync();
        });

        return SQEMResponse.newBuilder().setStatus("200").setBody("Entity evicted.").build();
    }

    // Lookup in  context registry whether the entity is cached
    private static SQEMResponse lookUp(CacheLookUp request){
        CacheLookUpResponse result = registry.lookUpRegistry(request);

        if(result.getHashkey().equals("")){
            return SQEMResponse.newBuilder().setStatus("404").setBody("Not Cached.").build();
        }
        return SQEMResponse.newBuilder().setStatus("200").setBody("Cached.").build();
    }

    // Retrieve entity context from cache
    public static SQEMResponse retrieveFromCache(CacheLookUp request) {
        long startTime = System.currentTimeMillis();
        CacheLookUpResponse result = registry.lookUpRegistry(request);

        if(!result.getHashkey().equals("") && result.getIsCached() && result.getIsValid()){
            try{
                RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

                RReadWriteLock rwLock = cacheClient.getReadWriteLock("readLock");
                RLock lock = rwLock.readLock();

                RBucket<Document> ent = cacheClient.getBucket(result.getHashkey());
                Document entityContext = ent.get();
                lock.unlockAsync();

                long endTime = System.currentTimeMillis();

                Executors.newCachedThreadPool().execute(()
                        -> logCacheSearch("200", request.getServiceId(), request.getUniformFreshness(), endTime - startTime));
                return SQEMResponse.newBuilder().setStatus("200")
                        .setBody(entityContext.get("data", Document.class).toJson())
                        .setMeta(result.getRefreshLogic())
                        .setHashKey(result.getHashkey()).build();
            }
            catch(Exception ex){
                SQEMResponse.newBuilder().setStatus("500").build();
            }
        }
        else if(!result.getHashkey().equals("") && result.getIsCached() && !result.getIsValid()){
            long endTime = System.currentTimeMillis();
            Executors.newCachedThreadPool().execute(()
                    -> logCacheSearch("400", request.getServiceId(), request.getUniformFreshness(), endTime - startTime));
            return SQEMResponse.newBuilder().setStatus("400")
                    .setMeta(result.getRefreshLogic())
                    .setHashKey(result.getHashkey()).build();
        }

        long endTime = System.currentTimeMillis();
        Executors.newCachedThreadPool().execute(()
                -> logCacheSearch("404", request.getServiceId(), request.getUniformFreshness(), endTime - startTime));
        return SQEMResponse.newBuilder().setStatus("404")
                .setHashKey(result.getHashkey()).build();
    }

    private static void logCacheSearch(String status, String csId, String freshness, long responseTime){
        JSONObject frsh = new JSONObject(freshness);
        Statistic stat = Statistic.newBuilder().setMethod("cacheSearch").setStatus(status).setTime(responseTime)
                .setIdentifier(csId).setEarning(frsh.getDouble("fthresh")).build();
        PerformanceLogHandler.coassPerformanceRecord(stat);
    }

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
    public static SQEMResponse clearCache() {
        RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
        cacheClient.getKeys().flushdb();
        log.info("Cleared the context Cache");

        return SQEMResponse.newBuilder().setStatus("200").setBody("Cleared context cache").build();
    }

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

    public static Empty logCacheDecisionLatency(DecisionLog request){
        PerformanceLogHandler.logDecisionLatency(request.getType(), request.getLatency(),
                request.getIndefinite()? DelayCacheLatency.INDEFINITE : DelayCacheLatency.DEFINITE);
        return null;
    }

    public static Empty logCacheDecision(ContextCacheDecision json){
        PerformanceLogHandler.logCacheDecision(new JSONObject(json.getJson()), json.getLevel());
        return null;
    }

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


