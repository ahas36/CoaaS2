package au.coaas.sqem.handler;

import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.redis.ConnectionPool;
import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.sqem.proto.UpdateEntityRequest;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.redisson.api.RedissonClient;

import au.coaas.sqem.util.ResponseUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ContextCacheHandler {

    public final static int BUCKET_SIZE = 200;

    // Caches all context under an entity
    public static void cacheEntity(UpdateEntityRequest updateRequest) {

    }

    // Fetches context by Hashkey
    public static SQEMResponse getContextByKey(String hashKey) {
        return null;
    }

    // Updating cached context for an entity
    public static void refreshEntity(UpdateEntityRequest updateRequest) {

    }

    // Evicts an entity by ID
    public static void evictEntity(String entityId) {

    }

    // Clears the context cache
    public static void clearCache() {

    }
}
