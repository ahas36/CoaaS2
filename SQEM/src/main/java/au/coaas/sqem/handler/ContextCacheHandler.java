package au.coaas.sqem.handler;

import au.coaas.sqem.proto.*;
import au.coaas.sqem.redis.ConnectionPool;

import au.coaas.sqem.util.CacheDataRegistry;
import org.bson.Document;

import org.redisson.api.*;

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
        String hashKey = String.valueOf(registry.removeFromRegistry(request));
        if(hashKey != null){
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            RReadWriteLock rwLock = cacheClient.getReadWriteLock("evictLock");
            RLock lock = rwLock.writeLock();

            cacheClient.getKeys().deleteAsync(hashKey);
            RFuture<Boolean> evictStatus = cacheClient.getBucket(hashKey).deleteAsync();

            evictStatus.whenCompleteAsync((res, exception) -> {
                lock.unlockAsync();
            });

            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity evicted.").build();
        }

        return SQEMResponse.newBuilder().setStatus("404").setBody("Nothing to Evict.").build();
    }

    // Lookup in  context registry whether the entity is cached
    private static SQEMResponse lookUp(CacheLookUp request){
        String hashKey = String.valueOf(registry.lookUpRegistry(request));
        if(hashKey == null){
            return SQEMResponse.newBuilder().setStatus("404").setBody("Not Cached.").build();
        }
        return SQEMResponse.newBuilder().setStatus("200").setBody("Cached.").build();
    }

    // Retrieve entity context from cache
    public static SQEMResponse retrieveFromCache(CacheLookUp request) {
        String hashKey = String.valueOf(registry.lookUpRegistry(request));

        if(hashKey != null){
            try{
                RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

                RReadWriteLock rwLock = cacheClient.getReadWriteLock("readLock");
                RLock lock = rwLock.readLock();

                RBucket<Document> ent = cacheClient.getBucket(hashKey);
                RFuture<Document> entityContext = ent.getAsync();

                entityContext.whenCompleteAsync((res, exception) -> {
                    lock.unlockAsync();
                });

                return SQEMResponse.newBuilder().setStatus("200").setBody(entityContext.getNow().toJson())
                        .setHashKey(hashKey).build();
            }
            catch(Exception ex){
                SQEMResponse.newBuilder().setStatus("500").setBody("An error occurred.").build();
            }
        }

        return SQEMResponse.newBuilder().setStatus("404").setBody("Not Cached.")
                .setHashKey(hashKey).build();
    }

    // Clears the context cache
    public static SQEMResponse clearCache() {
        RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
        cacheClient.getKeys().flushdb();
        log.info("Cleared the context Cache");

        return SQEMResponse.newBuilder().setStatus("200").setBody("Cleared context cache").build();
    }

}
