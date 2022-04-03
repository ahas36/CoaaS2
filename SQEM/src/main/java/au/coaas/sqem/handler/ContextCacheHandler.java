package au.coaas.sqem.handler;

import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.util.ResponseUtils;
import au.coaas.sqem.redis.ConnectionPool;
import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.sqem.proto.UpdateEntityRequest;

import au.coaas.sqem.util.cacherequest.CacheEntityRequest;
import au.coaas.sqem.util.cacherequest.RefreshEntityRequest;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import org.redisson.api.RBucket;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.logging.Logger;

public class ContextCacheHandler {

    private static Logger log = Logger.getLogger(ContextCacheHandler.class.getName());

    // Caches all context under an entity
    public static SQEMResponse cacheEntity(CacheEntityRequest registerRequest) {
        try {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            Document entityJson = Document.parse(registerRequest.getEntityRequest().getJson());

            RBucket<Object> ent = cacheClient.getBucket(registerRequest.getEntityId());
            // No expiration set at this point.
            // It is handled using the Eviction Manager.
            ent.setAsync(entityJson);

            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity cached!").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    // Fetches context by Hashkey
    public static SQEMResponse getContextByKey(String hashKey) {
        return null;
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

    // Evicts an entity by ID
    public static SQEMResponse evictEntity(String entityId) {
        RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
        RBucket<Object> ent = cacheClient.getBucket(entityId);
        ent.delete();
        return SQEMResponse.newBuilder().setStatus("200").build();
    }

    // Clears the context cache
    public static void clearCache() {
        RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
        cacheClient.getKeys().flushdb();
        log.info("Cleared the Redis Cache");
    }
}
