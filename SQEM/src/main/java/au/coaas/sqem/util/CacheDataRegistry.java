package au.coaas.sqem.util;

import au.coaas.sqem.proto.CacheLookUp;
import au.coaas.sqem.proto.CacheLookUpResponse;
import com.google.common.hash.Hashing;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public final class CacheDataRegistry{
    class ContextItem {
        private LocalDateTime createdTime;
        private LocalDateTime updatedTime;
        ConcurrentHashMap<String, ContextItem> child;

        /*
        ContextItem(CacheLookUp lookup){
            this.createdTime = LocalDateTime.now();
            this.updatedTime = LocalDateTime.now();
            this.child = new ConcurrentHashMap<>();
            this.child.put(lookup.getServiceId(), new ContextItem(lookup.getParamsMap()));
        }
        */

        ContextItem(CacheLookUp lookup, String hashKey){
            this.createdTime = LocalDateTime.now();
            this.updatedTime = LocalDateTime.now();
            this.child = new ConcurrentHashMap<>();
            this.child.put(lookup.getServiceId(), new ContextItem(hashKey));
        }

        ContextItem(Map<String,String> params){
            this.createdTime = LocalDateTime.now();
            this.updatedTime = LocalDateTime.now();
            this.child = new ConcurrentHashMap<>();
            this.child.put(getHashKey(params), new ContextItem());
        }

        ContextItem(String hashKey){
            this.createdTime = LocalDateTime.now();
            this.updatedTime = LocalDateTime.now();
            this.child = new ConcurrentHashMap<>();
            this.child.put(hashKey, new ContextItem());
        }

        ContextItem(){
            this.createdTime = LocalDateTime.now();
            this.updatedTime = LocalDateTime.now();
        }
    }

    private static CacheDataRegistry singleton = null;
    private ConcurrentHashMap<String, ContextItem> root;

    private CacheDataRegistry(){
        this.root = new ConcurrentHashMap<>();
    }

    public static CacheDataRegistry getInstance()
    {
        if (singleton == null){
            singleton = new CacheDataRegistry();
        }


        return singleton;
    }

    private static String getHashKey(Map<String,String> params){
        String hashKey = "";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            hashKey = hashKey + entry.getKey() + "@" + entry.getValue() + ";";
        }

        return Hashing.sha256().hashString(hashKey, StandardCharsets.UTF_8).toString();
    }

    // Registry lookup. Returns hash key if available in cache.

    public CacheLookUpResponse lookUpRegistry(CacheLookUp lookup){
        AtomicReference<String> hashKey = null;
        CacheLookUpResponse.Builder res = CacheLookUpResponse.newBuilder();

        if(this.root.containsKey(lookup.getEt().getType())){
            Map<String,ContextItem> cs = this.root.get(lookup.getEt().getType()).child;
            if(cs.containsKey(lookup.getServiceId())){
                Map<String,ContextItem> entities = cs.get(lookup.getServiceId()).child;

                hashKey.set(getHashKey(lookup.getParamsMap()));
                if(entities.containsKey(hashKey.get())){
                    JSONObject freshness = new JSONObject(lookup.getUniformFreshness());
                    long expPrd = freshness.getLong("unit") * (1-freshness.getLong("fthresh"));

                    LocalDateTime expiryTime;
                    switch(freshness.getString("unit")){
                        case "m": {
                            expiryTime = entities.get(hashKey.get()).updatedTime.plusMinutes(expPrd);
                            break;
                        }
                        case "s":
                        default:
                            expiryTime = entities.get(hashKey.get()).updatedTime.plusSeconds(expPrd);
                            break;
                    }

                    if(LocalDateTime.now().isAfter(expiryTime)){
                        return res.setHashkey(hashKey.get())
                                .setIsValid(false)
                                .setIsCached(true).build();
                    }

                    return res.setHashkey(hashKey.get())
                            .setIsValid(true)
                            .setIsCached(true).build();
                }
            }
        }

        // There can be 2 types of returns
        // 1. (hashkey, false) - the specific entity (by parameter combination) is not cached.
        // 2. (null, false) - either the entity type or the context service is not cached.
        return res.setHashkey(hashKey.get())
                .setIsCached(false)
                .setIsValid(false).build();
    }

    // Adds or updates the cached context repository
    public AtomicReference<String> updateRegistry(CacheLookUp lookup){
        AtomicReference<String> hashKey = null;
        if(this.root.containsKey(lookup.getEt().getType())){
            // Updating the last update time of the entity type

            this.root.compute(lookup.getEt().getType(), (k,v) -> {
                v.updatedTime = LocalDateTime.now();

                // Updating the last update time of the context service
                // Add a new context service if any available.

                if(v.child.containsKey(lookup.getServiceId())){
                    v.child.compute(lookup.getServiceId(), (id,stat) -> {
                        stat.updatedTime = LocalDateTime.now();

                        // Updating the last update time of the entity by hash key
                        // Add a new entity by hash key if not available.

                        hashKey.set(getHashKey(lookup.getParamsMap()));
                        if(!stat.child.contains(hashKey.get())){
                            stat.child.compute(hashKey.get(), (k1,v1) -> {
                                v1.updatedTime = LocalDateTime.now();

                                return v1;
                            });
                        }
                        else {
                            stat.child.put(hashKey.get(),new ContextItem());
                        }

                        return stat;
                    });
                }
                else {
                    v.child.put(lookup.getServiceId(),new ContextItem(lookup.getParamsMap()));
                }

                return v;
            });
        }
        else {
            hashKey.set(getHashKey(lookup.getParamsMap()));
            this.root.put(lookup.getEt().getType(), new ContextItem(lookup, hashKey.get()));
        }
        return hashKey;
    }

    // Removes context items from the cached context registry
    public AtomicReference<String> removeFromRegistry(CacheLookUp lookup){
        AtomicReference<String> hashKey = null;
        if(this.root.containsKey(lookup.getEt().getType())){
            this.root.compute(lookup.getEt().getType(), (k,v) -> {

                if(v.child.containsKey(lookup.getServiceId())){
                    v.child.compute(lookup.getServiceId(), (id,stat) -> {

                        hashKey.set(getHashKey(lookup.getParamsMap()));
                        if(stat.child.contains(hashKey)){
                            stat.child.remove(hashKey);
                        }

                        return stat;
                    });

                    if(v.child.get(lookup.getServiceId()).child.isEmpty()){
                        v.child.remove(lookup.getServiceId());
                    }
                }

                return v;
            });

            if(this.root.get(lookup.getEt().getType()).child.isEmpty()){
                this.root.remove(lookup.getEt().getType());
            }
        }

        return hashKey;
    }

}

