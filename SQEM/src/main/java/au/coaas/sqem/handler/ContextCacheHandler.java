package au.coaas.sqem.handler;

import au.coaas.sqem.proto.*;
import au.coaas.sqem.redis.ConnectionPool;

import au.coaas.sqem.util.CacheDataRegistry;
import au.coaas.sqem.util.ScheduleTasks;
import au.coaas.sqem.util.Utilty;
import org.bson.Document;

import org.redisson.api.*;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ContextCacheHandler {

    private static Logger log = Logger.getLogger(ContextCacheHandler.class.getName());
    private static CacheDataRegistry registry = CacheDataRegistry.getInstance();

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
        CacheLookUpResponse result= registry.lookUpRegistry(request);

        if(result.getHashkey() != "" && result.getIsCached() && result.getIsValid()){
            try{
                RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

                RReadWriteLock rwLock = cacheClient.getReadWriteLock("readLock");
                RLock lock = rwLock.readLock();

                RBucket<Document> ent = cacheClient.getBucket(result.getHashkey());
                RFuture<Document> entityContext = ent.getAsync();

                entityContext.whenCompleteAsync((res, exception) -> {
                    lock.unlockAsync();
                });

                return SQEMResponse.newBuilder().setStatus("200").setBody(entityContext.getNow().toJson())
                        .setHashKey(result.getHashkey()).build();
            }
            catch(Exception ex){
                SQEMResponse.newBuilder().setStatus("500").build();
            }
        }
        else if(result.getHashkey() != "" && result.getIsCached() && !result.getIsValid()){
            return SQEMResponse.newBuilder().setStatus("400")
                    .setHashKey(result.getHashkey()).build();
        }

        return SQEMResponse.newBuilder().setStatus("404")
                .setHashKey(result.getHashkey()).build();
    }

    public static void updatePerformanceStats(Map perfMetrics) {
        try {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            RLock lock = cacheClient.getFairLock("refreshLock");
            RMap<String, Object> map = cacheClient.getMap("perf_stats");
            map.putAll(perfMetrics);
            lock.unlockAsync();

        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public static SQEMResponse getPerformanceStats(String statKey){
        try {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            RReadWriteLock rwLock = cacheClient.getReadWriteLock("readLock");
            RLock lock = rwLock.readLock();

            RMap<String, Object> map = cacheClient.getMap("perf_stats");
            RFuture<Object> stat = map.getAsync(statKey);

            stat.whenCompleteAsync((res, exception) -> {
                lock.unlockAsync();
            });

            return SQEMResponse.newBuilder()
                    .setStatus("200").setBody((String) stat.getNow())
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

}
