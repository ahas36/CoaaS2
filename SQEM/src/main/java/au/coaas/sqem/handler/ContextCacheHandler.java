package au.coaas.sqem.handler;

import au.coaas.sqem.proto.*;
import au.coaas.sqem.redis.ConnectionPool;

import au.coaas.sqem.util.CacheDataRegistry;
import au.coaas.sqem.util.enums.PerformanceStats;
import au.coaas.sqem.util.enums.ScheduleTasks;
import au.coaas.sqem.util.Utilty;
import org.bson.Document;

import org.redisson.api.*;

import java.time.Instant;
import java.util.Map;
import java.util.logging.Logger;

import static java.lang.Character.isDigit;

public class ContextCacheHandler {

    private static Logger log = Logger.getLogger(ContextCacheHandler.class.getName());
    private static CacheDataRegistry registry = CacheDataRegistry.getInstance();
    private static final String regex = "^[\\d\\.]*[BGKM%]{0,1}$";

    // Caches all context under an entity
    public static SQEMResponse cacheEntity(CacheRequest registerRequest) {
        try {
            String hashKey = String.valueOf(registry.updateRegistry(registerRequest.getReference()));

            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
            Document entityJson =  Document.parse(registerRequest.getJson());
            RBucket<Document> ent = cacheClient.getBucket(hashKey);
            ent.set (entityJson);

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

            Document entityJson =  Document.parse(updateRequest.getJson());

            RLock lock = cacheClient.getFairLock("refreshLock");

            RBucket<Document> ent = cacheClient.getBucket(hashKey);
            RFuture<Document> refreshStatus = ent.getAndSetAsync(entityJson);

            refreshStatus.whenCompleteAsync((res, exception) -> {
                lock.unlockAsync();
            });

            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity refreshed.").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    public static void fullyEvict(ScheduleTask request){
        // Remove from registry
        registry.removeFromRegistry(request.getLookup());
        // Remove key
        RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
        cacheClient.getKeys().deleteAsync(request.getHashkey());
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
                registry.removeFromRegistry(request);
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

    // Lookup in  context registry whether the entity is cached
    private static SQEMResponse lookUp(CacheLookUp request){
        CacheLookUpResponse result= registry.lookUpRegistry(request);

        if(result.getHashkey().equals("")){
            return SQEMResponse.newBuilder().setStatus("404").setBody("Not Cached.").build();
        }
        return SQEMResponse.newBuilder().setStatus("200").setBody("Cached.").build();
    }

    // Retrieve entity context from cache
    public static SQEMResponse retrieveFromCache(CacheLookUp request) {
        CacheLookUpResponse result = registry.lookUpRegistry(request);

        if(!result.getHashkey().equals("") && result.getIsCached() && result.getIsValid()){
            try{
                RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

                RReadWriteLock rwLock = cacheClient.getReadWriteLock("readLock");
                RLock lock = rwLock.readLock();

                RBucket<Document> ent = cacheClient.getBucket(result.getHashkey());
                Document entityContext = ent.get();
                lock.unlockAsync();

                return SQEMResponse.newBuilder().setStatus("200").setBody(entityContext.toJson())
                        .setHashKey(result.getHashkey()).build();
            }
            catch(Exception ex){
                SQEMResponse.newBuilder().setStatus("500").build();
            }
        }
        else if(!result.getHashkey().equals("") && result.getIsCached() && !result.getIsValid()){
            return SQEMResponse.newBuilder().setStatus("400")
                    .setHashKey(result.getHashkey()).build();
        }

        return SQEMResponse.newBuilder().setStatus("404")
                .setHashKey(result.getHashkey()).build();
    }

    public static void updatePerformanceStats(Map perfMetrics, PerformanceStats key) {
        try {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            RLock lock = cacheClient.getFairLock("refreshLock");
            RMap<String, Object> map = cacheClient.getMap(key.toString());
            map.putAll(perfMetrics);
            lock.unlockAsync();

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

                    cachestats.put(keyval[0],setValue);
                }
            }

            return cachestats;

        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
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


