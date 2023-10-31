package au.coaas.sqem.util;

import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.cqp.proto.Operand;
import au.coaas.cqp.proto.OperandType;
import au.coaas.cqp.proto.SituationFunctionResponse;
import au.coaas.sqem.entity.ContextCacheItem;
import au.coaas.sqem.entity.SituationItem;
import au.coaas.sqem.proto.*;
import au.coaas.sqem.entity.ContextItem;
import au.coaas.sqem.handler.ContextCacheHandler;
import au.coaas.sqem.handler.PerformanceLogHandler;

import au.coaas.sqem.util.PubSub.Event;
import au.coaas.sqem.util.PubSub.Message;
import au.coaas.sqem.util.PubSub.Subscriber;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.*;
import java.util.logging.Logger;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public final class CacheDataRegistry{

    private HashMap<String, ContextCacheItem> root;
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
            HashMap<String, ContextCacheItem> cs = this.root.get(lookup.getEt().getType()).getChildren();
            String serId = lookup.getServiceId();
            String entType = lookup.getEt().getType();

            if(serId.startsWith("{")){
                JSONObject obj = new JSONObject(serId);
                serId = obj.getString("$oid");
            }

            // Check if originating from a context provider is available
            // This is because of the context provider selection.
            if(cs.containsKey(serId)){
                HashMap<String, ContextCacheItem> entities = cs.get(serId).getChildren();
                // Map<String, String> parameters = lookup.getParamsMap();

                if(lookup.getCheckFresh()){
                    // Cache lookup for unspecified entity
                    if(lookup.getHashKey().isEmpty() && !idAvailable){
                        // I need to parallely check in all the entities whether at least one of them is not fresh.
                        int entitiesCached = entities.size();
                        int numberOfIterations = (int)(entitiesCached/numberOfItemsPerTask) + 1;
                        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

                        List<Long> remainLife = Collections.synchronizedList(new ArrayList<Long>());
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
                                    LocalDateTime staleTime;
                                    LocalDateTime expiryTime;

                                    ContextItem data = (ContextItem) entities.get(keySet.get(i));

                                    JSONObject lifetime = data.getlifetime();
                                    LocalDateTime zeroTime = data.getZeroTime();
                                    JSONObject sampling = new JSONObject(lookup.getSamplingInterval());
                                    JSONObject freshness = new JSONObject(lookup.getUniformFreshness());

                                    LocalDateTime now = LocalDateTime.now(TimeZone.getDefault().toZoneId());

                                    // This freshness should be from the response coming from lifetime profiler
                                    switch(lifetime.getString("unit")){
                                        case "m": {
                                            Double fthresh = freshness.getDouble("fthresh");
                                            long samplingInterval = sampling.getLong("value");
                                            long lifetimeValue = (long)lifetime.getDouble("value") * 60;

                                            if(lifetimeValue > samplingInterval){
                                                // When the lifetime is longer than the sampling interval
                                                long residual_life = lifetimeValue - samplingInterval;
                                                if(fthresh < 1){
                                                    Double expPrd = residual_life * (1.0 - fthresh);
                                                    expiryTime = zeroTime.plusSeconds(samplingInterval + expPrd.longValue());
                                                    staleTime = zeroTime.plusSeconds(lifetimeValue);
                                                }
                                                else {
                                                    expiryTime = zeroTime.plusSeconds(lifetimeValue);
                                                    staleTime = expiryTime;
                                                }
                                            }
                                            else {
                                                // When the lifetime is shorter
                                                expiryTime = zeroTime.plusSeconds(samplingInterval);
                                                staleTime = expiryTime;
                                            }
                                            break;
                                        }
                                        case "s":
                                        default:
                                            if(sampling.equals("")){
                                                Double expPrd = lifetime.getDouble("value") * (1.0 - freshness.getDouble("fthresh"));
                                                staleTime = zeroTime.plusSeconds((long)lifetime.getDouble("value"));
                                                expiryTime = zeroTime.plusSeconds(expPrd.longValue());
                                            }
                                            else {
                                                long samplingInterval = sampling.getLong("value");
                                                Double fthresh = freshness.getDouble("fthresh");

                                                if(lifetime.getDouble("value") > samplingInterval){
                                                    // When the lifetime is longer than the sampling interval
                                                    long residual_life = (long)lifetime.getDouble("value") - samplingInterval;
                                                    if(fthresh < 1){
                                                        Double expPrd = residual_life * (1.0 - fthresh);
                                                        expiryTime = zeroTime.plusSeconds(samplingInterval + expPrd.longValue());
                                                        staleTime = zeroTime.plusSeconds((long)lifetime.getDouble("value"));
                                                    }
                                                    else {
                                                        expiryTime = zeroTime.plusSeconds((long)lifetime.getDouble("value"));
                                                        staleTime = expiryTime;
                                                    }
                                                }
                                                else {
                                                    // When the lifetime is shorter
                                                    expiryTime = zeroTime.plusSeconds(samplingInterval);
                                                    staleTime = expiryTime;
                                                }
                                            }
                                            break;
                                    }

                                    double finalAgeLoss = ChronoUnit.MILLIS.between(zeroTime, now);
                                    if(now.isAfter(expiryTime)){
                                        // Store cache access in Time Series DB
                                        PerformanceLogHandler.insertAccess(
                                                entType + "-" + keySet.get(i),
                                                "p_miss", finalAgeLoss);
                                        staleEntities.add("entity:"+entType+"-"+keySet.get(i));
                                    }
                                    else {
                                        long remainingLife = ChronoUnit.MILLIS.between(now, staleTime);
                                        remainLife.add(remainingLife);
                                        PerformanceLogHandler.insertAccess(
                                                entType + "-" + keySet.get(i),
                                                "hit", finalAgeLoss);
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

                        int stackSize;
                        long sumRemLife = 0;
                        for(stackSize = 0 ; stackSize < remainLife.size(); stackSize++){
                            sumRemLife += remainLife.get(stackSize);
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
                        long remainingLife = 0;
                        LocalDateTime staleTime;
                        LocalDateTime expiryTime;
                        String finalHashKey = "";

                        if(idAvailable){
                            String unencrypt = "";
                            for (String attr : idKeySet) {
                                unencrypt += attr+"@"+lookup.getParamsMap().get(attr).replace("\"","")+";";
                            }
                            finalHashKey = Utilty.getHashKey(unencrypt);
                        }
                        else finalHashKey = lookup.getHashKey();

                        ContextItem data = (ContextItem) entities.get(finalHashKey);

                        if(data != null){
                            JSONObject lifetime = data.getlifetime();
                            JSONObject freshness = new JSONObject(lookup.getUniformFreshness());
                            JSONObject sampling = new JSONObject(lookup.getSamplingInterval());

                            LocalDateTime now = LocalDateTime.now(TimeZone.getDefault().toZoneId());
                            LocalDateTime zeroTime = data.getZeroTime();

                            // This freshness should be from the response coming from lifetime profiler
                            switch(lifetime.optString("unit")){
                                case "m": {
                                    Double fthresh = freshness.getDouble("fthresh");
                                    long samplingInterval = sampling.getLong("value");
                                    long lifetimeValue = (long)lifetime.getDouble("value") * 60;

                                    if(fthresh < 1) {
                                        if(lifetimeValue > samplingInterval){
                                            // When the lifetime is longer than the sampling interval
                                            long residual_life =  - samplingInterval;
                                            if(fthresh < 1){
                                                Double expPrd = residual_life * (1.0 - fthresh);
                                                expiryTime = zeroTime.plusSeconds(samplingInterval + expPrd.longValue());
                                                staleTime = zeroTime.plusSeconds(lifetimeValue);
                                            }
                                            else {
                                                expiryTime = zeroTime.plusSeconds(samplingInterval);
                                                staleTime = expiryTime;
                                            }
                                        }
                                        else {
                                            // When the lifetime is shorter
                                            expiryTime = zeroTime.plusSeconds(samplingInterval);
                                            staleTime = expiryTime;
                                        }
                                    }
                                    else {
                                        expiryTime = zeroTime.plusSeconds(sampling.getLong("value"));
                                        staleTime = expiryTime;
                                    }
                                    break;
                                }
                                case "s":
                                default:
                                    if(sampling.equals("")){
                                        Double expPrd = lifetime.getDouble("value") * (1.0 - freshness.getDouble("fthresh"));
                                        staleTime = zeroTime.plusSeconds((long)lifetime.getDouble("value"));
                                        expiryTime = zeroTime.plusSeconds(expPrd.longValue());
                                    }
                                    else {
                                        long samplingInterval = sampling.getLong("value");
                                        Double fthresh = freshness.getDouble("fthresh");

                                        if(lifetime.getDouble("value") > samplingInterval){
                                            // When the lifetime is longer than the sampling interval
                                            long residual_life = (long)lifetime.getDouble("value") - samplingInterval;
                                            if(fthresh < 1){
                                                Double expPrd = residual_life * (1.0 - fthresh);
                                                expiryTime = zeroTime.plusSeconds(samplingInterval + expPrd.longValue());
                                                staleTime = zeroTime.plusSeconds((long)lifetime.getDouble("value"));
                                            }
                                            else {
                                                expiryTime = zeroTime.plusSeconds(samplingInterval);
                                                staleTime = expiryTime;
                                            }
                                        }
                                        else {
                                            // When the lifetime is shorter
                                            expiryTime = zeroTime.plusSeconds(samplingInterval);
                                            staleTime = expiryTime;
                                        }
                                    }
                                    break;
                            }

                            double finalAgeLoss = ChronoUnit.MILLIS.between(zeroTime,now);
                            if(now.isAfter(expiryTime)){

                                PerformanceLogHandler.insertAccess(
                                        entType + "-" + finalHashKey,
                                        "p_miss", finalAgeLoss);
                                return res.setHashkey("entity:" + entType + "-" + finalHashKey)
                                        .setIsValid(false)
                                        .setIsCached(true)
                                        .setRefreshLogic(data.getRefreshLogic())
                                        .setRemainingLife(remainingLife).build();
                            }

                            remainingLife = ChronoUnit.MILLIS.between(now, staleTime);
                            PerformanceLogHandler.insertAccess(
                                    entType + "-" + finalHashKey,
                                    "hit", finalAgeLoss);
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
                // This is when the freshness is not considered.
                else if(!entities.isEmpty()){
                    // Not a specific hashkey is looked up.
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
                                unencrypt += attr+"@"+lookup.getParamsMap().get(attr).replace("\"","")+";";
                            }
                            finalHashKey = Utilty.getHashKey(unencrypt);
                        }
                        else finalHashKey = lookup.getHashKey();

                        ContextItem data = (ContextItem) entities.get(finalHashKey);
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

    public CacheLookUpResponse lookUpSituation(SituationLookUp lookup) {
        // Initializing
        CacheLookUpResponse.Builder res = CacheLookUpResponse.newBuilder();

        if(this.root.containsKey(lookup.getFunction().getFunctionName())){
            HashMap<String, ContextCacheItem> situs = this.root.get(lookup.getFunction()
                                                            .getFunctionName()).getChildren();

            if(situs.containsKey(lookup.getUniquehashkey())){
                long remainingLife = 0;
                LocalDateTime expiryTime;

                SituationItem data = (SituationItem) situs.get(lookup.getUniquehashkey());

                JSONObject lifetime = data.getlifetime();
                LocalDateTime zeroTime = data.getZeroTime();
                LocalDateTime now = LocalDateTime.now(TimeZone.getDefault().toZoneId());

                // This freshness should be from the response coming from lifetime profiler
                switch(lifetime.optString("unit")){
                    case "m": {
                        long lifetimeValue = (long)lifetime.getDouble("value") * 60;
                        expiryTime = zeroTime.plusSeconds(lifetimeValue);
                        break;
                    }
                    case "s":
                    default:
                        expiryTime = zeroTime.plusSeconds((long)lifetime.getDouble("value"));
                        break;
                }

                double finalAgeLoss = ChronoUnit.MILLIS.between(zeroTime,now);
                if(now.isAfter(expiryTime)){
                    PerformanceLogHandler.insertAccess(
                            lookup.getFunction().getFunctionName() + "-" + lookup.getUniquehashkey(),
                            "p_miss", finalAgeLoss);
                    return res.setHashkey(lookup.getUniquehashkey())
                            .setIsValid(false)
                            .setIsCached(true)
                            .setRefreshLogic(data.getRefreshLogic())
                            .setRemainingLife(remainingLife).build();
                }

                remainingLife = ChronoUnit.MILLIS.between(now, expiryTime);
                PerformanceLogHandler.insertAccess(
                        lookup.getFunction().getFunctionName() + "-" + lookup.getUniquehashkey(),
                        "hit", finalAgeLoss);
                return res.setHashkey(lookup.getUniquehashkey())
                        .setIsValid(true)
                        .setIsCached(true)
                        .setRefreshLogic(data.getRefreshLogic())
                        .setRemainingLife(remainingLife).build();
            }
        }

        res.setIsCached(false).setIsValid(false).setRemainingLife(0);

        // Store cache access in Time Series DB
        if(!lookup.getUniquehashkey().isEmpty()){
            Executors.newCachedThreadPool().submit(()
                    -> PerformanceLogHandler.insertAccess(
                    lookup.getFunction().getFunctionName() + "-" + lookup.getUniquehashkey(),
                    "miss", 0));
        }

        return res.build();
    }

    private ContextItem lookupEntity(String entityType, String hashkey){
        if(this.root.containsKey(entityType)){
            ContextItem entType = (ContextItem) this.root.get(entityType);
            Collection<ContextCacheItem> css = entType.getChildren().values();
            for(ContextCacheItem ent: css){
                if(ent.getChildren().containsKey(hashkey)){
                    return (ContextItem) ent.getChildren().get(hashkey);
                }
            }
            return null;
        }
        return null;
    }

    // Adds or updates the cached context repository
    // Done
    public synchronized String updateRegistry(Object lookup){
        return updateRegistry(lookup, null);
    }

    // Done
    public synchronized String updateRegistry(Object lookup, String refreshLogic){
        try {
            if(lookup instanceof CacheLookUp) return updateCachedEntity((CacheLookUp)lookup, refreshLogic);
            else if(lookup instanceof SituationLookUp) return updateCachedSituation((SituationLookUp)lookup);
        }
        catch(Exception ex) {
            log.severe("Error in update registry: " + ex.getMessage());
        }
        return "Failed.";
    }

    public synchronized boolean setMissingEntityRefs(String entType, String entHash, String situName, String situHash) {
        try {
            if(this.root.containsKey(situName)){
                if(this.root.get(situName).getChildren().containsKey(situHash)){
                    SituationItem situ = (SituationItem) this.root.get(situName).getChildren().get(situHash);
                    situ.setChildren(lookupEntity(entType, entHash));
                }
                // Missing block. But, unsubscribing the subscriber.
            }
        }
        catch(Exception ex) {
            log.severe("Error in update registry: " + ex.getMessage());
            return false;
        }
        return true;
    }

    // Updating a cached situation or introducing a new cached situation.
    private String updateCachedSituation(SituationLookUp lookup){
        String situName = lookup.getFunction().getFunctionName();
        if(this.root.containsKey(situName)){
            this.root.compute(situName, (k,v) -> {
                v.setUpdatedTime(LocalDateTime.now(TimeZone.getDefault().toZoneId()));
                if(v.getChildren().containsKey(lookup.getUniquehashkey())) {
                    v.getChildren().compute(lookup.getUniquehashkey(), (id, stat) -> {
                        LocalDateTime zeroTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lookup.getZeroTime()),
                                TimeZone.getDefault().toZoneId());
                        stat.setZeroTime(zeroTime);
                        stat.setUpdatedTime(LocalDateTime.now(TimeZone.getDefault().toZoneId()));
                        ((SituationItem)stat).setOperands(lookup.getFunction().getArgumentsList());
                        return stat;
                    });
                }
                else {
                    LocalDateTime zeroTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lookup.getZeroTime()),
                            TimeZone.getDefault().toZoneId());
                    SituationItem newSituationInstance = new SituationItem((SituationItem) v, lookup.getUniquehashkey(),
                            lookup.getFunction().getArgumentsList(), zeroTime);

                    // Setting the entities related to the situation
                    for(Operand ope: lookup.getFunction().getArgumentsList()){
                        if(ope.getType().equals(OperandType.CONTEXT_VALUE_JSON)){
                            String entityName = ope.getContextAttribute().isInitialized() ?
                                    ope.getContextAttribute().getEntityName() : ope.getContextEntity().getEntityID();

                            ContextEntityType entType = lookup.getSituation().getRelatedEntitiesMap().get(entityName);
                            JSONObject value = new JSONObject(ope.getStringValue());

                            ContextItem ents = lookupEntity(entType.getType(), value.getString("hashkey"));

                            String entId = entType.getType() + "-" + value.getString("hashkey");
                            String situId = situName + "-" + lookup.getUniquehashkey();

                            if(ents != null){
                                ents.setParents(situId, newSituationInstance);
                                newSituationInstance.setChildren(ents);
                            }
                            else{
                                // Cached Situation subscribing to when the context could be cached in the future
                                // to set the situation-entity reference.
                                Subscriber subscriber = new Subscriber("situ:"+entId, situId);
                                Event.operation.subscribe("situationUpdate", subscriber);
                            }
                        }
                    }
                    v.getChildren().put(lookup.getUniquehashkey(), newSituationInstance);
                }
                return v;
            });
        }
        else {
            SituationItem rootSitu = new SituationItem(lookup);
            LocalDateTime zeroTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lookup.getZeroTime()),
                    TimeZone.getDefault().toZoneId());
            SituationItem newSituationInstance = new SituationItem(rootSitu, lookup.getUniquehashkey(),
                    lookup.getFunction().getArgumentsList(), zeroTime);

            // Setting the entities related to the situation
            for(Operand ope: lookup.getFunction().getArgumentsList()){
                if(ope.getType().equals(OperandType.CONTEXT_VALUE_JSON)){
                    String entityName  = ope.getContextAttribute().getEntityName();
                    ContextEntityType entType = lookup.getSituation().getRelatedEntitiesMap().get(entityName);
                    JSONObject value = new JSONObject(ope.getStringValue());

                    ContextItem ents = lookupEntity(entType.getType(), value.getString("hashkey"));

                    String entId = entType.getType() + "-" + value.getString("hashkey");
                    String situId = situName + "-" + lookup.getUniquehashkey();

                    if(ents != null){
                        ents.setParents(situName + "-" + lookup.getUniquehashkey(), newSituationInstance);
                        newSituationInstance.setChildren(ents);
                    }
                    else{
                        // Cached Situation subscribing to when the context could be cached in the future
                        // to set the situation-entity reference.
                        Subscriber subscriber = new Subscriber("situ:"+entId, situId);
                        Event.operation.subscribe("situationUpdate", subscriber);
                    }
                }
            }
            this.root.put(lookup.getFunction().getFunctionName(), rootSitu);
        }
        return lookup.getUniquehashkey();
    }

    // Caching and updating a cached entity
    private String updateCachedEntity(CacheLookUp lookup, String refreshLogic){
        String entType = lookup.getEt().getType();
        if(this.root.containsKey(entType)){
            // Updating the last update time of the entity type
            this.root.compute(entType, (k,v) -> {
                if(v != null){
                    v.setUpdatedTime(LocalDateTime.now(TimeZone.getDefault().toZoneId()));

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
                                stat.setUpdatedTime(LocalDateTime.now(TimeZone.getDefault().toZoneId()));

                                // Updating the last update time of the entity by hash key
                                // Add a new entity by hash key if not available.
                                if(!stat.getChildren().containsKey(lookup.getHashKey())){
                                    ContextItem sharedEntity = null;
                                    HashMap<String, ContextCacheItem> siblingCPs = v.getChildren();
                                    for(Map.Entry<String, ContextCacheItem> sibling : siblingCPs.entrySet()){
                                        if(sibling.getValue().getChildren().containsKey(lookup.getHashKey())){
                                            sharedEntity = (ContextItem) sibling.getValue()
                                                    .getChildren().get(lookup.getHashKey());
                                            break;
                                        }
                                    }

                                    LocalDateTime zeroTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lookup.getZeroTime()),
                                            TimeZone.getDefault().toZoneId());
                                    if(sharedEntity == null){
                                        // No shared entity.
                                        stat.getChildren().put(lookup.getHashKey(),
                                                refreshLogic != null ?
                                                        new ContextItem((ContextItem) stat, lookup.getHashKey(), refreshLogic, zeroTime, entType)
                                                        : new ContextItem((ContextItem) stat, lookup.getHashKey(), zeroTime, entType));
                                    }
                                    else {
                                        // Already existing shared entity.
                                        sharedEntity.setZeroTime(zeroTime);
                                        sharedEntity.setParents(finalSerId, stat);
                                        sharedEntity.setUpdatedTime(LocalDateTime.now(TimeZone.getDefault().toZoneId()));
                                        stat.getChildren().put(lookup.getHashKey(),sharedEntity);
                                    }
                                }
                                else {
                                    stat.getChildren().compute(lookup.getHashKey(), (k1,v1) -> {
                                        if(v1!=null){
                                            LocalDateTime zeroTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lookup.getZeroTime()),
                                                    TimeZone.getDefault().toZoneId());
                                            v1.setZeroTime(zeroTime);
                                            v1.setUpdatedTime(LocalDateTime.now(TimeZone.getDefault().toZoneId()));
                                            if(refreshLogic != null)
                                                ((ContextItem)v1).setRefreshLogic(refreshLogic);
                                        }
                                        return v1;
                                    });
                                }
                            }
                            return stat;
                        });
                    }
                    else {
                        LocalDateTime zeroTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lookup.getZeroTime()),
                                TimeZone.getDefault().toZoneId());
                        v.getChildren().put(serId,new ContextItem((ContextItem) v, serId, lookup.getHashKey(), refreshLogic, zeroTime));
                    }
                }
                return v;
            });
        }
        else {
            LocalDateTime zeroTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lookup.getZeroTime()),
                    TimeZone.getDefault().toZoneId());
            this.root.put(lookup.getEt().getType(), new ContextItem(lookup,lookup.getHashKey(), refreshLogic, zeroTime));
        }

        if(Event.operation.checkChannel("situationUpdate")){
            Message message = new Message(entType, lookup.getHashKey());
            Event.operation.publish("situationUpdate", message);
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
                                            ((ContextItem)v1).setRefreshLogic(lookup.getRefreshLogic());
                                        return v1;
                                    });
                                }
                            }
                            return stat;
                        });
                    }
                }
                return v;
            });
        }
    }

    // Done
    // Removes context items from the cached context registry
    public String removeFromRegistry(CacheLookUp lookup){
        if(this.root.containsKey(lookup.getEt().getType())){
            this.root.compute(lookup.getEt().getType(), (k,v) -> {

                String serId = lookup.getServiceId();
                if(serId.startsWith("{")){
                    JSONObject obj = new JSONObject(serId);
                    serId = obj.getString("$oid");
                }

                if(v.getChildren().containsKey(serId)){
                    ContextItem statTemp = (ContextItem) v.getChildren().compute(serId, (id,stat) -> {
                        if(stat.getChildren().containsKey(lookup.getHashKey())){
                            ContextItem entity = (ContextItem) stat.getChildren().get(lookup.getHashKey());
                            for(String situId: entity.getParents().keySet()){
                                if(!Utilty.isEntity(situId)){
                                    // When situation
                                    String[] situNames = situId.split("-");
                                    ContextCacheItem func = this.root.get(situNames[0]).getChildren()
                                            .get(situNames[1]);
                                    ((SituationItem) func).removeChild(entity.getId());

                                    // Cached Situtaion subscribing to make a reference to this entity
                                    // in case it gets cashed again.
                                    String entId = lookup.getEt().getType() + "-" + lookup.getHashKey();
                                    Subscriber subscriber = new Subscriber("situ:"+ entId, situId);
                                    Event.operation.subscribe("situationUpdate", subscriber);
                                }
                            }
                            entity.removeAllParents();
                        }

                        ((ContextItem) stat).removeChild(lookup.getHashKey());
                        if(stat.getChildren().isEmpty()){
                            ((ContextItem) stat).removeAllParents();
                        }
                        return stat;
                    });

                    if(statTemp.getParents().isEmpty()){
                        ((ContextItem) v).removeChild(serId);
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
    // Evicts entities
    public void removeFromRegistry(String serviceId, String hashKey, String entityType){
        if(this.root.containsKey(entityType)){
            this.root.compute(entityType, (k,v) -> {
                String serId = serviceId;
                if(serId.startsWith("{")){
                    JSONObject obj = new JSONObject(serId);
                    serId = obj.getString("$oid");
                }

                // Context Service Reference
                if(v.getChildren().containsKey(serId)){
                    ContextItem statTemp = (ContextItem) v.getChildren().compute(serId, (id,stat) -> {
                        // Specific Entities
                        if(stat.getChildren().containsKey(hashKey)){
                            ContextItem entity = (ContextItem) stat.getChildren().get(hashKey);
                            //Checking if the entity is a related to any situations
                            for(String situId: entity.getParents().keySet()){
                                if(!Utilty.isEntity(situId)){
                                    // When situation
                                    String[] situNames = situId.split("-");
                                    ContextCacheItem func = this.root.get(situNames[0]).getChildren()
                                            .get(situNames[1]);
                                    ((SituationItem) func).removeChild(entity.getId());

                                    // Cached Situtaion subscribing to make a reference to this entity
                                    // in case it gets cashed again.
                                    String entId = entityType + "-" + hashKey;
                                    Subscriber subscriber = new Subscriber("situ:"+ entId, situId);
                                    Event.operation.subscribe("situationUpdate", subscriber);
                                }
                            }
                            entity.removeAllParents();
                        }
                        ((ContextItem) stat).removeChild(hashKey);
                        if(stat.getChildren().isEmpty()){
                            ((ContextItem) stat).removeAllParents();
                        }
                        return stat;
                    });

                    if(statTemp.getParents().isEmpty()){
                        ((ContextItem) v).removeChild(serId);
                    }
                }
                return v;
            });

            if(this.root.get(entityType).getChildren().isEmpty()){
                this.root.remove(entityType);
            }
        }
    }

    // Done
    // Evicts Situations
    public boolean removeFromRegistry(String situName, String hashKey){
        // Return indicates the relevant situation Type should be evicted from cache.
        boolean shouldEvict = false;
        String contextId = situName + "-" + hashKey;

        // Situation Type
        if(this.root.containsKey(situName)){
            ContextCacheItem situType = this.root.compute(situName, (k, v) -> {
                // Specific instance of a situation
                if (v.getChildren().containsKey(hashKey)) {
                    v.getChildren().compute(hashKey, (id, stat) -> {
                        // Related entities in cache
                        if (!stat.getChildren().isEmpty()) {
                            for (Map.Entry<String, ContextCacheItem> ent : stat.getChildren().entrySet()) {
                                ContextItem currEnt = (ContextItem) ent.getValue();
                                currEnt.removeParent(contextId);
                            }
                        }
                        ((SituationItem) stat).removeAllChildren();
                        ((SituationItem) stat).removeAllParents();

                        // Following is a known improvement. Existing subscriptions are not removed at this time.
                        // But, in case any of the entity gets cached, and the event triggers, it will remove the
                        // Subscription at that point. So, then the subscription gets removed.
                        // Subscriber subscriber = new Subscriber("situ:"+entId, situId);
                        // Event.operation.subscribe("situationUpdate", subscriber);

                        return stat;
                    });
                    ((SituationItem) v).removeChild(hashKey);
                }
                return v;
            });

            if(situType.getChildren().isEmpty()){
                this.root.remove(situName);
                return true;
            }
        }
        return shouldEvict;
    }
}

