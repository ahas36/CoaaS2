package au.coaas.sqem.handler;

import au.coaas.sqem.proto.*;
import au.coaas.sqem.redis.ConnectionPool;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ContextCacheHandler {

    private static Logger log = Logger.getLogger(ContextCacheHandler.class.getName());

    // Caches all context under an entity
    public static SQEMResponse cacheEntity(CacheRequest registerRequest) {
        try {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();

            String vocab = registerRequest.getEnt().getEt().getVocabURI();
            Document entityJson = new Document();
            entityJson.put(vocab, Document.parse(registerRequest.getEnt().getJson()));

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
    public static SQEMResponse refreshEntity(CacheRefreshRequest updateRequest) {
        try {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
            RBucket<Document> ent = cacheClient.getBucket(updateRequest.getEntityId());

            String vocab = updateRequest.getEt().getVocabURI();

            if(ent.isExists() && ent.get().containsKey(vocab)){
                Document cacheBlock = ent.get();
                Document entityDoc = (Document) cacheBlock.get(vocab);
                JSONObject attributes = new JSONObject(updateRequest.getJson());

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
                cacheBlock.replace(vocab,entityDoc);
                ent.set(cacheBlock);
            }
            else {
                Document entityJson = Document.parse(updateRequest.getJson());
                ent.set(entityJson);

                return SQEMResponse.newBuilder().setStatus("200").setBody("Entity cached!").build();
            }

            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity updated!").build();

        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    // Evicts an entity by ID
    public static SQEMResponse evictEntity(CachedEntityLookUp entity) {
        RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
        RBucket<Document> ent = cacheClient.getBucket(entity.getEntityId());

        String vocab = entity.getEt().getVocabURI();

        if(ent.isExists()){
            Document entDoc = ent.get();
            if(entDoc.keySet().size() == 1){
                // Completely evicts the entity from the cache
                ent.delete();
            }
            else {
                // Evicts part of the entity for a specific schema
                entDoc.remove(vocab);
                ent.set(entDoc);
            }

            return SQEMResponse.newBuilder().setStatus("200").setBody("Entity-attribute found!").build();
        }

        return SQEMResponse.newBuilder().setStatus("404").setBody("Can not evict!").build();
    }

    // Lookup if an entity-attribute pair is available in cache
    public static SQEMResponse isEntityAttributeCached(CachedEntityAttributes lookUp) {
        try {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
            RBucket<Document> ent = cacheClient.getBucket(lookUp.getEntityId());

            String vocab = lookUp.getEt().getVocabURI();

            if(ent.isExists() && ent.get().containsKey(vocab)){
                Document entityDoc = (Document) ent.get().get(vocab);
                if(entityDoc.containsKey(lookUp.getAttributes())){
                    return SQEMResponse.newBuilder().setStatus("200").setBody("Entity-attribute found!").build();
                }
            }

            return SQEMResponse.newBuilder().setStatus("404").setBody("Entity-attribute pair not found!").build();

        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    // Lookup whether the entity is cached with all the attributes
    public static SQEMResponse isEntityCached(CachedEntityAttributes lookUp) {
        try {
            RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
            RBucket<Document> ent = cacheClient.getBucket(lookUp.getEntityId());

            String vocab = lookUp.getEt().getVocabURI();

            if(ent.isExists() && ent.get().containsKey(vocab)){
                Document entityDoc = (Document) ent.get().get(vocab);

                JSONArray attrs = new JSONArray(lookUp.getAttributes());
                List<String> attList = new ArrayList<String>();
                for (int i = 0; i < attrs.length(); i++) {
                    attList.add(attrs.getString(i));
                }

                if(entityDoc.keySet().containsAll(attList)){
                    return SQEMResponse.newBuilder().setStatus("200").setBody("All entity-attributes are cached!").build();
                }
            }

            return SQEMResponse.newBuilder().setStatus("404").setBody("Entity-attribute pair not in cache!").build();

        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    // Clears the context cache
    public static SQEMResponse clearCache() {
        RedissonClient cacheClient = ConnectionPool.getInstance().getRedisClient();
        cacheClient.getKeys().flushdb();
        log.info("Cleared the context Cache");

        return SQEMResponse.newBuilder().setStatus("200").setBody("Cleared context cache!").build();
    }
}
