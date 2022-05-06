package au.coaas.sqem.handler;

import au.coaas.sqem.proto.*;
import au.coaas.sqem.redis.ConnectionPool;

import au.coaas.sqem.util.CacheDataRegistry;
import org.bson.Document;

import org.redisson.api.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

    // Evicts an entity by hash key
    public static SQEMResponse evictEntity(CacheLookUp request) {
        // Ideally the initial evict should push the context into ghost cache
        CacheLookUpResponse result= registry.lookUpRegistry(request);
        if(result.getHashkey() != null){
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            RReadWriteLock rwLock = cacheClient.getReadWriteLock("evictLock");
            RLock lock = rwLock.writeLock();

            cacheClient.getKeys().deleteAsync(result.getHashkey());
            RFuture<Boolean> evictStatus = cacheClient.getBucket(result.getHashkey()).deleteAsync();

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

        if(result.getHashkey() == null){
            return SQEMResponse.newBuilder().setStatus("404").setBody("Not Cached.").build();
        }
        return SQEMResponse.newBuilder().setStatus("200").setBody("Cached.").build();
    }

    // Retrieve entity context from cache
    public static SQEMResponse retrieveFromCache(CacheLookUp request) {
        CacheLookUpResponse result= registry.lookUpRegistry(request);

        if(result.getHashkey() != null && result.getIsCached() && result.getIsValid()){
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
        else if(result.getHashkey() != null && result.getIsCached() && !result.getIsValid()){
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

            perfMetrics.forEach((k,v) -> map.fastPut((String)k,v));

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
                    .setStatus("200").setBody(stat.getNow().toString())
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
