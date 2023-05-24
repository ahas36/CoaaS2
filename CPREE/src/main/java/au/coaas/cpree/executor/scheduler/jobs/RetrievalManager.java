package au.coaas.cpree.executor.scheduler.jobs;

import au.coaas.cpree.proto.SimpleContainer;
import au.coaas.cpree.utils.Utilities;
import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.CSIServiceGrpc;
import au.coaas.csi.proto.ContextService;
import au.coaas.csi.proto.ContextServiceInvokerRequest;

import au.coaas.sqem.proto.GetEntitiesRequest;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.Statistic;

import au.coaas.grpc.client.CSIChannel;
import au.coaas.grpc.client.SQEMChannel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class RetrievalManager {
    
    private static final int retrys = 20;
    private static Logger log = Logger.getLogger(RetrievalManager.class.getName());

    // Retrieval from non-streaming Context Providers
    public static AbstractMap.SimpleEntry<String,SimpleContainer> executeFetch(String contextService, HashMap<String,String> params) {
        CSIServiceGrpc.CSIServiceBlockingStub csiStub
                = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance().getChannel());

        SQEMServiceGrpc.SQEMServiceBlockingStub asyncStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        final ContextServiceInvokerRequest.Builder fetchRequest = ContextServiceInvokerRequest.newBuilder();
        fetchRequest.putAllParams(params);

        final ContextService cs = ContextService.newBuilder().setJson(contextService).build();
        fetchRequest.setContextService(cs);

        JSONObject provider = new JSONObject(contextService);
        JSONObject qos = provider.getJSONObject("sla").getJSONObject("qos");

        CSIResponse fetch = null;
        double penEarning = 0;
        long startTime = 0;
        
        for(int i=0; i < retrys; i++){
            startTime = System.currentTimeMillis();
            fetch = csiStub.fetch(fetchRequest.build());

            if(fetch.getStatus().equals("200")) break;
            else {
                long int_endTime = System.currentTimeMillis();

                // Should penalties be earned if the CP fails to respond at all (non 200 responses, timeouts)
                long retLatency = int_endTime-startTime;
                double retDiff = retLatency - qos.getDouble("rtmax");
                if(retDiff > 0){
                    penEarning = (((int)(retDiff/1000))+1) * qos.getDouble("rate")
                            * qos.getDouble("penPct") / 100;
                }

                asyncStub.logPerformanceData(Statistic.newBuilder()
                        .setIsDelayed(retDiff>0).setEarning(penEarning)
                        .setMethod("executeFetch").setStatus(fetch.getStatus())
                        .setTime(int_endTime-startTime).setCs(fetchRequest).setAge(0)
                        .setCost(!fetch.getStatus().equals("500")? fetch.getSummary().getPrice() : 0).build());
            }
        }

        // Since the end_time is after the retry attempts until success, what is being reported back to ConQEng is accumilated time.
        long endTime = System.currentTimeMillis();

        long age = 0;
        if(fetch.getStatus().equals("200")){
            JSONObject response = new JSONObject(fetch.getBody());
            if(response.has("age")){
                Object ageObj = response.get("age");
                if(ageObj instanceof JSONObject){
                    JSONObject age_obj = (JSONObject) ageObj;

                    String unit = age_obj.getString("unitText");
                    long value = age_obj.getLong("value");

                    // Age is always considered in seconds in the code
                    switch(unit){
                        case "ms": age = value/1000; break;
                        case "s": age = value; break;
                        case "h": age = value*60; break;
                    }
                }
                else if (ageObj instanceof String) age = Long.valueOf((String) ageObj);
                else age = Long.valueOf((long) ageObj);
            }
            else if(response.has("avgAge")){
                age = (long) response.getDouble("avgAge");
            }

            long retLatency = endTime-startTime;
            double retDiff = retLatency - qos.getDouble("rtmax");
            if(retDiff > 0){
                penEarning = (((int)(retDiff/1000))+1) * qos.getDouble("rate")
                        * qos.getDouble("penPct") / 100;
            }

            JSONArray key = provider.getJSONObject("sla").getJSONArray("key");
            List<String> hkeys = new ArrayList<>();

            if(response.has("results")){
                String hashkey = "";
                JSONArray entities = response.getJSONArray("results");
                for(int j=0; j<entities.length(); j++){
                    for (int i = 0; i < key.length(); i++) {
                        Object idValue = entities.getJSONObject(i).get(key.getString(i));
                        hashkey += key.getString(i) + "@" + idValue.toString() + ";";
                    }
                    hkeys.add(Utilities.getHashKey(hashkey));
                }
            }
            else {
                String hashkey = "";
                for (int i = 0; i < key.length(); i++) {
                    Object idValue = response.get(key.getString(i));
                    hashkey += key.getString(i) + "@" + idValue.toString() + ";";
                }
                hkeys.add(Utilities.getHashKey(hashkey));
            }

            asyncStub.logPerformanceData(Statistic.newBuilder()
                    .addAllHaskeys(hkeys)
                    .setIsDelayed(retDiff>0).setEarning(penEarning)
                    .setCs(fetchRequest).setAge(age).setTime(retLatency)
                    .setMethod("executeFetch").setStatus(fetch.getStatus())
                    .setCost(fetch.getStatus().equals("200")? fetch.getSummary().getPrice() : 0).build());

            SimpleContainer meta = SimpleContainer.newBuilder().addAllHashKeys(hkeys)
                    .setRetLatMilis(endTime-startTime)
                    .setFreshness(provider.getJSONObject("sla").getJSONObject("freshness").getDouble("value"))
                    .build();

            return new AbstractMap.SimpleEntry(fetch.getBody(),meta);
        }

        // Returning null means the CMP failed to retrieved context from the provider despite all attempts.
        return null;
    }

    // Retrieval from streaming Context Providers
    public static AbstractMap.SimpleEntry<String,SimpleContainer> executeStreamRead(String contextService, HashMap<String,String> params,
                                           String csID, String entityType){
        CSIServiceGrpc.CSIServiceBlockingStub csiStub
                = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance().getChannel());

        long startTime = System.currentTimeMillis();
        CSIResponse fetch = csiStub.updateFetchJob(ContextService.newBuilder()
                .setJson(contextService).setMongoID(csID)
                .setTimes(1).putAllParams(params)
                .build());

        if(fetch.getStatus().equals("200")) {
            SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
            SQEMResponse entities = sqemStub.getEntities(GetEntitiesRequest.newBuilder()
                            .setEntityType(entityType)
                            .addAllKeys(fetch.getHashkeysList())
                            .build());

            long endTime = System.currentTimeMillis();
            JSONObject cs = new JSONObject(contextService);
            SimpleContainer meta = SimpleContainer.newBuilder().addAllHashKeys(fetch.getHashkeysList())
                    .setRetLatMilis(endTime-startTime)
                    .setFreshness(cs.getJSONObject("sla").getJSONObject("freshness").getDouble("value"))
                    .build();
            return new AbstractMap.SimpleEntry(entities.getBody(),meta);
        }
        else{
            log.severe("Error occurred when refreshing from to refresh in Storage");
            return null;
        }
    }
}
