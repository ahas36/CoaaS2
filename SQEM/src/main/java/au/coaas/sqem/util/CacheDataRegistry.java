package au.coaas.sqem.util;

import au.coaas.sqem.proto.CacheLookUp;
import au.coaas.sqem.entity.ContextItem;
import au.coaas.sqem.proto.RefreshUpdate;
import au.coaas.sqem.proto.CacheLookUpResponse;
import au.coaas.sqem.handler.ContextCacheHandler;
import au.coaas.sqem.handler.PerformanceLogHandler;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.*;
import java.util.logging.Logger;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public final class CacheDataRegistry{

    private HashMap<String, ContextItem> root;
    private static CacheDataRegistry singleton = null;
    private static Logger log = Logger.getLogger(ContextCacheHandler.class.getName());

    private static final int numberOfThreads = 10;
    private static final int numberOfItemsPerTask = 100;

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
    // Done
    public CacheLookUpResponse lookUpRegistry(CacheLookUp lookup){
        // Initializing
        CacheLookUpResponse.Builder res = CacheLookUpResponse.newBuilder();

        // Check if the keys are the same as the parameters. If so, send in the hashkey available path.
        List<String> idKeySet = Arrays.asList(lookup.getKey().split(","));
        boolean idAvailable = lookup.getParamsMap().keySet().containsAll(idKeySet);

        // Check if the entity type is cached.
        if(this.root.containsKey(lookup.getEt().getType())){
            Map<String,ContextItem> cs = this.root.get(lookup.getEt().getType()).getChildren();
            String serId = lookup.getServiceId();
            String entType = lookup.getEt().getType();

            if(serId.startsWith("{")){
                JSONObject obj = new JSONObject(serId);
                serId = obj.getString("$oid");
            }

            // Check if originating from a context provider is available
            // This is because of the context provider selection.
            if(cs.containsKey(serId)){
                Map<String,ContextItem> entities = cs.get(serId).getChildren();
                // Map<String, String> parameters = lookup.getParamsMap();

                if(lookup.getCheckFresh()){
                    // Cache lookup for unspecified entity
                    if(lookup.getHashKey().isEmpty() && !idAvailable){
                        // I need to parallely check in all the entities whether at least one of them is not fresh.
                        int entitiesCached = entities.size();
                        int numberOfIterations = (int)(entitiesCached/numberOfItemsPerTask) + 1;
                        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

                        Stack<Long> remainLife = new Stack<>();
                        Set<String> staleEntities = Collections.synchronizedSet(new HashSet<>());

                        List<String> keySet = new ArrayList<>(entities.keySet());
                        for (int factor = 0; factor<numberOfIterations; factor++)
                        {
                            int finalFactor = factor;
                            executorService.submit(() -> {
                                int start = numberOfItemsPerTask * finalFactor;
                                int end =  Math.min(entitiesCached, start + numberOfItemsPerTask);

                                // Check for freshness of each context entity
                                for(int i = start; i < end; i++){
                                    // Initializing
                                    double ageLoss = 0.0;
                                    long remainingLife = 0;
                                    LocalDateTime staleTime;
                                    LocalDateTime expiryTime;

                                    ContextItem data = entities.get(keySet.get(i));

                                    JSONObject freshness = new JSONObject(lookup.getUniformFreshness());
                                    JSONObject sampling = new JSONObject(lookup.getSamplingInterval());

                                    ageLoss = PerformanceLogHandler.getLastRetrievalTime(
                                            lookup.getServiceId(), keySet.get(i));

                                    LocalDateTime now = LocalDateTime.now();
                                    LocalDateTime updateTime = data.getUpdatedTime();

                                    // This freshness should be from the response coming from lifetime profiler
                                    switch(freshness.getString("unit")){
                                        case "m": {
                                            Double fthresh = freshness.getDouble("fthresh");

                                            if(fthresh < 1) {
                                                Double residual_life = freshness.getLong("value")
                                                        - ageLoss > 1 ? ageLoss/60.0 : 0.0;
                                                Double expPrd = residual_life * (1.0 - fthresh);
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
                                                        LocalDateTime sampledTime = updateTime.minusSeconds(Math.round(ageLoss));
                                                        expiryTime = sampledTime.plusSeconds(samplingInterval + expPrd.longValue());
                                                        staleTime = sampledTime.plusSeconds(lifetime);
                                                    }
                                                    else {
                                                        Double expPrd = fthresh;
                                                        LocalDateTime sampledTime = updateTime.minusSeconds(Math.round(ageLoss));
                                                        expiryTime = sampledTime.plusSeconds(samplingInterval + expPrd.longValue());
                                                        staleTime = expiryTime;
                                                    }
                                                }
                                                else {
                                                    // When the lifetime is shorter
                                                    LocalDateTime sampledTime = updateTime.minusSeconds(Math.round(ageLoss));
                                                    expiryTime = sampledTime.plusSeconds(samplingInterval);
                                                    // expiryTime = fthresh > samplingInterval ? sampledTime.plusSeconds(fthresh.longValue())
                                                    //         : sampledTime.plusSeconds(samplingInterval);
                                                    staleTime = expiryTime;
                                                }
                                            }
                                            break;
                                    }

                                    if(now.isAfter(expiryTime)){
                                        // Store cache access in Time Series DB
                                        double finalAgeLoss = ageLoss;
                                        PerformanceLogHandler.insertAccess(
                                                entType + "-" + keySet.get(i),
                                                "p_miss", finalAgeLoss*1000);
                                        staleEntities.add("entity:"+entType+"-"+keySet.get(i));
                                    }
                                    else {
                                        remainingLife = ChronoUnit.MILLIS.between(LocalDateTime.now(), staleTime);
                                        remainLife.push(remainingLife);

                                        double finalAgeLoss = ageLoss;
                                        PerformanceLogHandler.insertAccess(
                                                entType + "-" + keySet.get(i),
                                                "hit", finalAgeLoss * 1000);
                                    }
                                }
                            });
                        }
                        executorService.shutdown();

                        try {
                            executorService.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
                        } catch (InterruptedException e) {
                            log.severe("Executor failed to execute with error: " + e.getMessage());
                        }

                        int stackSize = 0;
                        long sumRemLife = 0;
                        for(stackSize = 0; stackSize < remainLife.size(); stackSize++){
                            sumRemLife += remainLife.pop();
                        }

                        // Reconciling what is stale and what is not? Whether refreshing is needed?
                        if(staleEntities.isEmpty()){
                            // Meaning all the context entities are valid
                            return res.setHashkey("service:" + lookup.getServiceId())
                                    .addAllHashKeys(keySet.stream().map(v -> "entity:" + entType + "-" + v)
                                            .collect(Collectors.toList()))
                                    .setIsValid(true)
                                    .setIsCached(true)
                                    .setRemainingLife(sumRemLife/stackSize).build();
                        }
                        else if (stackSize > 0){
                            // Meaning some of the context entities may be stale.
                            Set<String> allKeySet = new HashSet<>(keySet.stream().map(v -> "entity:"+entType+"-"+v)
                                    .collect(Collectors.toList()));
                            allKeySet.removeAll(staleEntities);
                            return res.setHashkey("service:" + lookup.getServiceId())
                                    .addAllHashKeys(allKeySet)
                                    .addAllMissKeys(staleEntities)
                                    .setIsValid(false)
                                    .setIsCached(true)
                                    .setRemainingLife(sumRemLife/stackSize).build();
                        }
                        else {
                            // Meaning none of the context entities are valid (all stale).
                            return res.setHashkey("service:" + lookup.getServiceId())
                                    .addAllMissKeys(keySet.stream().map(v -> "entity:"+entType+"-"+v)
                                            .collect(Collectors.toList()))
                                    .setIsValid(false)
                                    .setIsCached(true)
                                    .setRemainingLife(0).build();
                        }
                    }
                    // Cache lookup for a specific entity
                    else {
                        double ageLoss = 0.0;
                        long remainingLife = 0;
                        LocalDateTime staleTime;
                        LocalDateTime expiryTime;
                        String finalHashKey = "";

                        if(idAvailable){
                            String unencrypt = "";
                            for (String attr : idKeySet) {
                                unencrypt += attr+"@"+lookup.getParamsMap().get(attr)+";";
                            }
                            finalHashKey = Utilty.getHashKey(unencrypt);
                        }
                        else finalHashKey = lookup.getHashKey();

                        ContextItem data = entities.get(finalHashKey);
                        if(data != null){
                            JSONObject freshness = new JSONObject(lookup.getUniformFreshness());
                            JSONObject sampling = new JSONObject(lookup.getSamplingInterval());

                            // This is also wrong becuase the entity could have later been updated by a different CP
                            ageLoss = PerformanceLogHandler.getLastRetrievalTime(
                                    lookup.getServiceId(), finalHashKey);

                            LocalDateTime now = LocalDateTime.now();
                            LocalDateTime updateTime = data.getUpdatedTime();

                            // This freshness should be from the response coming from lifetime profiler
                            switch(freshness.getString("unit")){
                                case "m": {
                                    Double fthresh = freshness.getDouble("fthresh");

                                    if(fthresh < 1) {
                                        Double residual_life = freshness.getLong("value")
                                                - ageLoss > 1 ? ageLoss/60.0 : 0.0;
                                        Double expPrd = residual_life * (1.0 - fthresh);
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
                                        LocalDateTime sampleTime = updateTime.minusSeconds(Math.round(ageLoss));
                                        Double expPrd = freshness.getLong("value") * (1.0 - freshness.getDouble("fthresh"));
                                        staleTime = sampleTime.plusSeconds(freshness.getLong("value"));
                                        expiryTime = sampleTime.plusSeconds(expPrd.longValue());
                                    }
                                    else {
                                        long samplingInterval = sampling.getLong("value");
                                        Double fthresh = freshness.getDouble("fthresh");
                                        long lifetime = freshness.getLong("value");

                                        if(lifetime>samplingInterval){
                                            // When the lifetime is longer than the sampling interval
                                            long residual_life = lifetime - samplingInterval;
                                            if(fthresh < 1){
                                                Double expPrd = residual_life * (1.0 - fthresh);
                                                LocalDateTime sampledTime = updateTime.minusSeconds(Math.round(ageLoss));
                                                expiryTime = sampledTime.plusSeconds(samplingInterval + expPrd.longValue());
                                                staleTime = sampledTime.plusSeconds(lifetime);
                                            }
                                            else {
                                                Double expPrd = fthresh;
                                                LocalDateTime sampledTime = updateTime.minusSeconds(Math.round(ageLoss));
                                                expiryTime = sampledTime.plusSeconds(samplingInterval + expPrd.longValue());
                                                staleTime = expiryTime;
                                            }
                                        }
                                        else {
                                            // When the lifetime is shorter
                                            LocalDateTime sampledTime = updateTime.minusSeconds(Math.round(ageLoss));
                                            expiryTime = sampledTime.plusSeconds(samplingInterval);
                                            // expiryTime = fthresh > samplingInterval ? sampledTime.plusSeconds(fthresh.longValue())
                                            //         : sampledTime.plusSeconds(samplingInterval);
                                            staleTime = expiryTime;
                                        }
                                    }
                                    break;
                            }

                            if(now.isAfter(expiryTime)){
                                double finalAgeLoss = ageLoss;

                                PerformanceLogHandler.insertAccess(
                                        entType + "-" + finalHashKey,
                                        "p_miss", finalAgeLoss * 1000);
                                return res.setHashkey("entity:" + entType + "-" + finalHashKey)
                                        .setIsValid(false)
                                        .setIsCached(true)
                                        .setRefreshLogic(data.getRefreshLogic())
                                        .setRemainingLife(remainingLife).build();
                            }

                            remainingLife = ChronoUnit.MILLIS.between(LocalDateTime.now(), staleTime);
                            double finalAgeLoss = ageLoss;

                            PerformanceLogHandler.insertAccess(
                                    entType + "-" + finalHashKey,
                                    "hit", finalAgeLoss * 1000);
                            return res.setHashkey("entity:" + entType + "-" + finalHashKey)
                                    .setIsValid(true)
                                    .setIsCached(true)
                                    .setRefreshLogic(data.getRefreshLogic())
                                    .setRemainingLife(remainingLife).build();
                        }
                        else {
                            PerformanceLogHandler.insertAccess(
                                    entType + "-" + finalHashKey,
                                    "miss", 0.0);
                            return res.setHashkey("entity:" + entType + "-" + finalHashKey)
                                    .setIsValid(false)
                                    .setIsCached(false)
                                    .setRemainingLife(0).build();
                        }
                    }
                }
                else if(!entities.isEmpty()){
                    // This is when the freshness is not considered.
                    if(lookup.getHashKey().isEmpty() && !idAvailable){
                        int entitiesCached = entities.size();
                        List<String> keySet = new ArrayList<>(entities.keySet());
                        int numberOfIterations = (int)(entitiesCached/numberOfItemsPerTask) + 1;
                        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

                        for (int factor = 0; factor<numberOfIterations; factor++)
                        {
                            int finalFactor = factor;
                            executorService.submit(() -> {
                                int start = numberOfItemsPerTask * finalFactor;
                                int end =  Math.min(entitiesCached, start + numberOfItemsPerTask);

                                // Sending cache hit or miss record to performance monitor.
                                for(int i = start; i < end; i++){
                                    PerformanceLogHandler.insertAccess(
                                            entType + "-" + keySet.get(i),
                                            "hit", 0);
                                }
                            });
                        }

                        executorService.shutdown();

                        try {
                            executorService.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
                        } catch (InterruptedException e) {
                            log.severe("Executor failed to execute with error: " + e.getMessage());
                        }

                        // Sending the ID is the level above to generalize the cache hit.
                        return res.setHashkey("service:" + lookup.getServiceId())
                                .addAllHashKeys(keySet.stream().map(v -> "entity:" + entType + "-" + v)
                                        .collect(Collectors.toList()))
                                .setIsValid(true)
                                .setIsCached(true)
                                .setRemainingLife(-1).build(); // -1 to notify the remaining life is ignored.
                    }
                    else {
                        // Hashkey provided and the freshness not cached.
                        String finalHashKey = "";
                        if(idAvailable){
                            String unencrypt = "";
                            for (String attr : idKeySet) {
                                unencrypt += attr+"@"+lookup.getParamsMap().get(attr)+";";
                            }
                            finalHashKey = Utilty.getHashKey(unencrypt);
                        }
                        else finalHashKey = lookup.getHashKey();

                        ContextItem data = entities.get(finalHashKey);
                        if(data != null){
                            PerformanceLogHandler.insertAccess(
                                    entType + "-" + finalHashKey,
                                    "hit", 0);
                            return res.setHashkey("entity:" + entType + "-" + finalHashKey)
                                    .setIsValid(true)
                                    .setIsCached(true)
                                    .setRefreshLogic(data.getRefreshLogic())
                                    .setRemainingLife(-1).build();
                        }
                        else {
                            PerformanceLogHandler.insertAccess(
                                    entType + "-" + finalHashKey,
                                    "miss", 0);
                            return res.setHashkey("entity:" + entType + "-" + finalHashKey)
                                    .setIsValid(false)
                                    .setIsCached(false)
                                    .setRemainingLife(0).build();
                        }
                    }
                }
                // If this block is missed, then it means a complete miss.
            }
        }

        // There can be 2 types of returns
        // 1. (hashkey, false) - the specific entity (by parameter combination) is not cached.
        // 2. ("", false) - either the entity type or the context service is not cached.
        res.setIsCached(false).setIsValid(false).setRemainingLife(0);

        // Store cache access in Time Series DB
        if(!lookup.getHashKey().isEmpty()){
            Executors.newCachedThreadPool().submit(()
                    -> PerformanceLogHandler.insertAccess(
                            lookup.getEt().getType() + "-" + lookup.getHashKey(),
                            "miss", 0));
        }

        return res.build();
    }

    // Adds or updates the cached context repository
    // Done
    public synchronized String updateRegistry(CacheLookUp lookup){
        return updateRegistry(lookup, null);
    }

    // Done
    public synchronized String updateRegistry(CacheLookUp lookup, String refreshLogic){
        if(this.root.containsKey(lookup.getEt().getType())){
            // Updating the last update time of the entity type
            this.root.compute(lookup.getEt().getType(), (k,v) -> {
                if(v != null){
                    v.setUpdatedTime(LocalDateTime.now());

                    // Updating the last update time of the context service
                    // Add a new context service if any available.

                    String serId = lookup.getServiceId();
                    if(serId.startsWith("{")){
                        JSONObject obj = new JSONObject(serId);
                        serId = obj.getString("$oid");
                    }

                    if(v.getChildren().containsKey(serId)){
                        String finalSerId = serId;
                        v.getChildren().compute(serId, (id, stat) -> {
                            if(stat != null){
                                stat.setUpdatedTime(LocalDateTime.now());

                                // Updating the last update time of the entity by hash key
                                // Add a new entity by hash key if not available.
                                if(!stat.getChildren().containsKey(lookup.getHashKey())){
                                    ContextItem sharedEntity = null;
                                    HashMap<String, ContextItem> siblingCPs = v.getChildren();
                                    for(Map.Entry<String, ContextItem> sibling : siblingCPs.entrySet()){
                                        if(sibling.getValue().getChildren().containsKey(lookup.getHashKey())){
                                            sharedEntity = sibling.getValue()
                                                    .getChildren().get(lookup.getHashKey());
                                            break;
                                        }
                                    }

                                    if(sharedEntity == null){
                                        // No shared entity.
                                        stat.getChildren().put(lookup.getHashKey(),
                                                refreshLogic != null ? new ContextItem(stat, lookup.getHashKey(), refreshLogic)
                                                        : new ContextItem(stat, lookup.getHashKey()));
                                    }
                                    else {
                                        // Already existing shared entity.
                                        sharedEntity.setParents(finalSerId, stat);
                                        sharedEntity.setUpdatedTime(LocalDateTime.now());
                                        stat.getChildren().put(lookup.getHashKey(),sharedEntity);
                                    }
                                }
                                else {
                                    stat.getChildren().compute(lookup.getHashKey(), (k1,v1) -> {
                                        if(v1!=null){
                                            v1.setUpdatedTime(LocalDateTime.now());
                                            if(refreshLogic != null)
                                                v1.setRefreshLogic(refreshLogic);
                                        }
                                        return v1;
                                    });
                                }
                            }
                            return stat;
                        });
                    }
                    else {
                        v.getChildren().put(serId,new ContextItem(v, serId, lookup.getHashKey(), refreshLogic));
                    }
                }
                return v;
            });
        }
        else {
            this.root.put(lookup.getEt().getType(), new ContextItem(lookup,lookup.getHashKey(), refreshLogic));
        }
        return lookup.getHashKey();
    }

    // Change the hashtable record of the current refreshing logic
    // Done
    public void changeRefreshLogic(RefreshUpdate lookup){
        if(this.root.containsKey(lookup.getLookup().getEt().getType())){
            this.root.compute(lookup.getLookup().getEt().getType(), (k,v) -> {
                if(v != null){
                    String serId = lookup.getLookup().getServiceId();
                    if(serId.startsWith("{")){
                        JSONObject obj = new JSONObject(serId);
                        serId = obj.getString("$oid");
                    }

                    if(v.getChildren().containsKey(serId)){
                        v.getChildren().compute(serId, (id,stat) -> {
                            if(stat != null){
                                if(stat.getChildren().containsKey(lookup.getHashkey())){
                                    stat.getChildren().compute(lookup.getHashkey(), (k1,v1) -> {
                                        if(v1!=null)
                                            v1.setRefreshLogic(lookup.getRefreshLogic());
                                        return v1;
                                    });
                                }
                            }
                            return stat;
                        });
                    }
                    else {
                        v.getChildren().put(serId,
                                new ContextItem(v, serId, lookup.getHashkey(), lookup.getRefreshLogic()));
                    }
                }
                return v;
            });
        }
    }

    // Removes context items from the cached context registry
    // Done
    public String removeFromRegistry(CacheLookUp lookup){
        if(this.root.containsKey(lookup.getEt().getType())){
            this.root.compute(lookup.getEt().getType(), (k,v) -> {

                String serId = lookup.getServiceId();
                if(serId.startsWith("{")){
                    JSONObject obj = new JSONObject(serId);
                    serId = obj.getString("$oid");
                }

                if(v.getChildren().containsKey(serId)){
                    v.getChildren().compute(serId, (id,stat) -> {
                        if(stat.getChildren().containsKey(lookup.getHashKey())){
                            ContextItem entity = stat.getChildren().get(lookup.getHashKey());
                            for(Map.Entry<String, ContextItem> parent: entity.getParents().entrySet()){
                                parent.getValue().getChildren().remove(lookup.getHashKey());
                            }
                        }
                        return stat;
                    });

                    if(v.getChildren().get(serId).getChildren().isEmpty()){
                        v.getChildren().remove(serId);
                    }
                }

                return v;
            });

            if(this.root.get(lookup.getEt().getType()).getChildren().isEmpty()){
                this.root.remove(lookup.getEt().getType());
            }
        }

        return lookup.getHashKey();
    }

    // Done
    public void removeFromRegistry(String serviceId, String hashKey, String entityType){
        if(this.root.containsKey(entityType)){
            this.root.compute(entityType, (k,v) -> {
                String serId = serviceId;
                if(serId.startsWith("{")){
                    JSONObject obj = new JSONObject(serId);
                    serId = obj.getString("$oid");
                }

                if(v.getChildren().containsKey(serId)){
                    v.getChildren().compute(serId, (id,stat) -> {
                        if(stat.getChildren().containsKey(hashKey)){
                            ContextItem entity = stat.getChildren().get(hashKey);
                            for(Map.Entry<String, ContextItem> parent: entity.getParents().entrySet()){
                                parent.getValue().getChildren().remove(hashKey);
                            }
                        }

                        return stat;
                    });

                    if(v.getChildren().get(serId).getChildren().isEmpty()){
                        v.getChildren().remove(serId);
                    }
                }

                return v;
            });

            if(this.root.get(entityType).getChildren().isEmpty()){
                this.root.remove(entityType);
            }
        }
    }
}

