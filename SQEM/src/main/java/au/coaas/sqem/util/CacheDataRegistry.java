package au.coaas.sqem.util;

import au.coaas.sqem.handler.ContextCacheHandler;
import au.coaas.sqem.handler.PerformanceLogHandler;
import au.coaas.sqem.proto.CacheLookUp;
import au.coaas.sqem.proto.CacheLookUpResponse;
import org.json.JSONObject;

import java.time.LocalDateTime;

import java.time.temporal.ChronoUnit;
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
            this.child.put(Utilty.getHashKey(params), new ContextItem());
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

    // Registry lookup. Returns hash key if available in cache.
    public CacheLookUpResponse lookUpRegistry(CacheLookUp lookup){
        long remainingLife = 0;
        LocalDateTime staleTime;
        AtomicReference<String> hashKey = new AtomicReference<>();
        CacheLookUpResponse.Builder res = CacheLookUpResponse.newBuilder();

        if(this.root.containsKey(lookup.getEt().getType())){
            Map<String,ContextItem> cs = this.root.get(lookup.getEt().getType()).child;
            if(cs.containsKey(lookup.getServiceId())){
                Map<String,ContextItem> entities = cs.get(lookup.getServiceId()).child;

                hashKey.set(Utilty.getHashKey(lookup.getParamsMap()));
                if(entities.containsKey(hashKey.get())){
                    if(lookup.getCheckFresh()){
                        JSONObject freshness = new JSONObject(lookup.getUniformFreshness());
                        double ageLoss = PerformanceLogHandler.getLastRetrievalTime(lookup.getServiceId())/1000;

                        LocalDateTime expiryTime;
                        switch(freshness.getString("unit")){
                            case "m": {
                                Double residual_life = freshness.getLong("value")
                                        - ageLoss > 1 ? ageLoss/60 : 0;
                                Double expPrd = residual_life * (1 - freshness.getLong("fthresh"));

                                LocalDateTime updateTime = entities.get(hashKey.get()).updatedTime;

                                staleTime = updateTime.plusSeconds(residual_life.longValue());
                                expiryTime = updateTime.plusMinutes(expPrd.longValue());
                                break;
                            }
                            case "s":
                            default:
                                Double residual_life = freshness.getLong("value")
                                        - ageLoss;
                                Double expPrd = residual_life * (1 - freshness.getLong("fthresh"));

                                LocalDateTime updateTime = entities.get(hashKey.get()).updatedTime;

                                staleTime = updateTime.plusSeconds(residual_life.longValue());
                                expiryTime = updateTime.plusSeconds(expPrd.longValue());
                                break;
                        }

                        if(LocalDateTime.now().isAfter(expiryTime)){
                            return res.setHashkey(hashKey.get())
                                    .setIsValid(false)
                                    .setIsCached(true)
                                    .setRemainingLife(remainingLife).build();
                        }

                        remainingLife = ChronoUnit.SECONDS.between(LocalDateTime.now(), staleTime);
                    }

                    return res.setHashkey(hashKey.get())
                            .setIsValid(true)
                            .setIsCached(true)
                            .setRemainingLife(remainingLife).build();
                }
            }
        }

        // There can be 2 types of returns
        // 1. (hashkey, false) - the specific entity (by parameter combination) is not cached.
        // 2. ("", false) - either the entity type or the context service is not cached.
        res.setIsCached(false).setIsValid(false).setRemainingLife(remainingLife).build();
        if(hashKey.get() != null)
            res.setHashkey(hashKey.get());

        return res.build();
    }

    // Adds or updates the cached context repository
    public AtomicReference<String> updateRegistry(CacheLookUp lookup){
        AtomicReference<String> hashKey = new AtomicReference<>();
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

                        hashKey.set(Utilty.getHashKey(lookup.getParamsMap()));
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
            hashKey.set(Utilty.getHashKey(lookup.getParamsMap()));
            this.root.put(lookup.getEt().getType(), new ContextItem(lookup, hashKey.get()));
        }
        return hashKey;
    }

    // Removes context items from the cached context registry
    public AtomicReference<String> removeFromRegistry(CacheLookUp lookup){
        AtomicReference<String> hashKey = new AtomicReference<>();
        if(this.root.containsKey(lookup.getEt().getType())){
            this.root.compute(lookup.getEt().getType(), (k,v) -> {

                if(v.child.containsKey(lookup.getServiceId())){
                    v.child.compute(lookup.getServiceId(), (id,stat) -> {

                        hashKey.set(Utilty.getHashKey(lookup.getParamsMap()));
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

