package au.coaas.sqem.util;

import au.coaas.sqem.handler.ContextCacheHandler;
import au.coaas.sqem.handler.PerformanceLogHandler;
import au.coaas.sqem.proto.CacheLookUp;
import au.coaas.sqem.proto.CacheLookUpResponse;
import org.json.JSONObject;

import java.time.LocalDateTime;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class CacheDataRegistry{
    class ContextItem {
        private LocalDateTime createdTime;
        private LocalDateTime updatedTime;
        HashMap<String, ContextItem> child;

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
            this.child = new HashMap<>();
            String serId = lookup.getServiceId();
            if(serId.startsWith("{")){
                JSONObject obj = new JSONObject(serId);
                serId = obj.getString("$oid");
            }
            this.child.put(serId, new ContextItem(hashKey));
        }

        ContextItem(Map<String,String> params){
            this.createdTime = LocalDateTime.now();
            this.updatedTime = LocalDateTime.now();
            this.child = new HashMap<>();
            this.child.put(Utilty.getHashKey(params), new ContextItem());
        }

        ContextItem(String hashKey){
            this.createdTime = LocalDateTime.now();
            this.updatedTime = LocalDateTime.now();
            this.child = new HashMap<>();
            this.child.put(hashKey, new ContextItem());
        }

        ContextItem(){
            this.createdTime = LocalDateTime.now();
            this.updatedTime = LocalDateTime.now();
        }
    }

    private static CacheDataRegistry singleton = null;
    private HashMap<String, ContextItem> root;

    private CacheDataRegistry(){
        this.root = new HashMap<>();
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

            String serId = lookup.getServiceId();
            if(serId.startsWith("{")){
                JSONObject obj = new JSONObject(serId);
                serId = obj.getString("$oid");
            }

            if(cs.containsKey(serId)){
                Map<String,ContextItem> entities = cs.get(serId).child;

                hashKey.set(Utilty.getHashKey(lookup.getParamsMap()));
                if(entities.containsKey(hashKey.get())){
                    if(lookup.getCheckFresh()){
                        // TODO:
                        // Need to get this information from Himadri's lifetime profiler
                        JSONObject freshness = new JSONObject(lookup.getUniformFreshness());
                        double ageLoss = PerformanceLogHandler.getLastRetrievalTime(serId)/1000;

                        // Periodic Sampling Device Check
                        JSONObject sampling = new JSONObject(lookup.getSamplingInterval());

                        LocalDateTime expiryTime;
                        LocalDateTime now = LocalDateTime.now();
                        // This freshness should be from the response coming from lifetime profiler
                        switch(freshness.getString("unit")){
                            case "m": {

                                Double fthresh = freshness.getDouble("fthresh");
                                LocalDateTime updateTime = entities.get(hashKey.get()).updatedTime;

                                if(fthresh < 1) {
                                    // TODO:
                                    // This block need to change similar to the "s" and default block
                                    Double residual_life = freshness.getLong("value")
                                            - ageLoss > 1 ? ageLoss/60 : 0;
                                    Double expPrd = residual_life * (1 - fthresh);
                                    staleTime = updateTime.plusMinutes(residual_life.longValue());
                                    expiryTime = updateTime.plusMinutes(expPrd.longValue());
                                }
                                else {
                                    Double expPrd = fthresh;
                                    LocalDateTime sensedTime = updateTime.minusSeconds(Math.round(ageLoss));
                                    expiryTime = sensedTime.plusMinutes(expPrd.longValue());
                                    staleTime = expiryTime;
                                }
                                break;
                            }
                            case "s":
                            default:
                                if(sampling.equals("")){
                                    Double residual_life = freshness.getLong("value") - ageLoss;
                                    Double expPrd = residual_life * (1 - freshness.getDouble("fthresh"));

                                    LocalDateTime updateTime = entities.get(hashKey.get()).updatedTime;

                                    staleTime = updateTime.plusSeconds(residual_life.longValue());
                                    expiryTime = updateTime.plusSeconds(expPrd.longValue());
                                }
                                else {
                                    long samplingInterval = sampling.getLong("value");
                                    Double fthresh = freshness.getDouble("fthresh");
                                    long lifetime = freshness.getLong("value");

                                    if(lifetime>samplingInterval){
                                        // When the lifetime is longer than the sampling interval
                                        long residual_life = lifetime - samplingInterval;
                                        if(fthresh < 1){
                                            Double expPrd = residual_life * (1 - fthresh);
                                            LocalDateTime updateTime = entities.get(hashKey.get()).updatedTime;
                                            LocalDateTime sampledTime = updateTime.minusSeconds(Math.round(ageLoss));

                                            expiryTime = sampledTime.plusSeconds(samplingInterval + expPrd.longValue());
                                            staleTime = sampledTime.plusSeconds(lifetime);
                                        }
                                        else {
                                            Double expPrd = fthresh;
                                            LocalDateTime updateTime = entities.get(hashKey.get()).updatedTime;
                                            LocalDateTime sampledTime = updateTime.minusSeconds(Math.round(ageLoss));

                                            expiryTime = sampledTime.plusSeconds(samplingInterval + expPrd.longValue());
                                            staleTime = expiryTime;
                                        }
                                    }
                                    else {
                                        // When the lifetime is shorter
                                        LocalDateTime updateTime = entities.get(hashKey.get()).updatedTime;
                                        LocalDateTime sampledTime = updateTime.minusSeconds(Math.round(ageLoss));

                                        expiryTime = fthresh > samplingInterval ? sampledTime.plusSeconds(fthresh.longValue())
                                                : sampledTime.plusSeconds(samplingInterval);
                                        staleTime = expiryTime;
                                    }
                                }
                                break;
                        }

                        if(now.isAfter(expiryTime)){
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
                if(v != null){
                    v.updatedTime = LocalDateTime.now();

                    // Updating the last update time of the context service
                    // Add a new context service if any available.

                    String serId = lookup.getServiceId();
                    if(serId.startsWith("{")){
                        JSONObject obj = new JSONObject(serId);
                        serId = obj.getString("$oid");
                    }

                    if(v.child.containsKey(serId)){
                        v.child.compute(serId, (id,stat) -> {
                            if(stat != null){
                                stat.updatedTime = LocalDateTime.now();

                                // Updating the last update time of the entity by hash key
                                // Add a new entity by hash key if not available.
                                hashKey.set(Utilty.getHashKey(lookup.getParamsMap()));
                                if(!stat.child.containsKey(hashKey.get()))
                                    stat.child.put(hashKey.get(),new ContextItem());
                                else {
                                    stat.child.compute(hashKey.get(), (k1,v1) -> {
                                        if(v1!=null)
                                            v1.updatedTime = LocalDateTime.now();
                                        return v1;
                                    });
                                }
                            }
                            return stat;
                        });
                    }
                    else {
                        v.child.put(serId,new ContextItem(lookup.getParamsMap()));
                    }
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

                String serId = lookup.getServiceId();
                if(serId.startsWith("{")){
                    JSONObject obj = new JSONObject(serId);
                    serId = obj.getString("$oid");
                }

                if(v.child.containsKey(serId)){
                    v.child.compute(serId, (id,stat) -> {

                        hashKey.set(Utilty.getHashKey(lookup.getParamsMap()));
                        if(stat.child.containsKey(hashKey)){
                            stat.child.remove(hashKey);
                        }

                        return stat;
                    });

                    if(v.child.get(serId).child.isEmpty()){
                        v.child.remove(serId);
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
