package au.coaas.sqem.handler;

import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.redis.ConnectionPool;

import au.coaas.sqem.util.cacherequest.CacheEntityRequest;
import au.coaas.sqem.util.cacherequest.RefreshEntityRequest;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.logging.Logger;

public class ContextCacheHandler {

    private static Logger log = Logger.getLogger(ContextCacheHandler.class.getName());

    // Caches all context under an entity
    public static SQEMResponse cacheEntity(CacheEntityRequest registerRequest) {
        try {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            Document entityJson = Document.parse(registerRequest.getEntityRequest().getJson());

            RBucket<Document> ent = cacheClient.getBucket(registerRequest.getEntityId());
            // No expiration set at this point.
            // It is handled using the Eviction Manager.
            ent.setAsync(entityJson);

            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity cached!").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    // Updating cached context for an entity
    public static SQEMResponse refreshEntity(RefreshEntityRequest updateRequest) {
        try {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
            RBucket<Document> ent = cacheClient.getBucket(updateRequest.getEntityId());
            if(ent.isExists()){
                Document entityDoc = ent.get();
                JSONObject attributes = new JSONObject(updateRequest.getRefreshRequest().getJson());
                for (String attributeName : attributes.keySet()) {
                    Object item = attributes.get(attributeName);
                    if(entityDoc.containsKey(attributeName)){
                        if (item instanceof JSONArray || item instanceof JSONObject) {
                            entityDoc.replace(attributeName, Document.parse(item.toString()));
                        } else {
                            entityDoc.replace(attributeName, item);
                        }
                    }
                    else {
                        if (item instanceof JSONArray || item instanceof JSONObject) {
                            entityDoc.put(attributeName, Document.parse(item.toString()));
                        } else {
                            entityDoc.put(attributeName, item);
                        }
                    }
                }

                ent.set(entityDoc);
            }
            else {
                Document entityJson = Document.parse(updateRequest.getRefreshRequest().getJson());
                ent.set(entityJson);

                return SQEMResponse.newBuilder().setStatus("200").setBody("Entity cached!").build();
            }

            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity updated!").build();

        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
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
