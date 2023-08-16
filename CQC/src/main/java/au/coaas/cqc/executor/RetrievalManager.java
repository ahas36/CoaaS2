package au.coaas.cqc.executor;

import au.coaas.cqc.utils.ConQEngHelper;
import au.coaas.cqc.utils.Utilities;
import au.coaas.cqc.utils.enums.HttpRequests;
import au.coaas.cqc.utils.enums.RequestDataType;
import au.coaas.cqp.proto.ContextEntity;
import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.cre.proto.ContextEvent;
import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.CSIServiceGrpc;
import au.coaas.csi.proto.ContextService;
import au.coaas.csi.proto.ContextServiceInvokerRequest;
import au.coaas.grpc.client.CSIChannel;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.ContextAccess;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.Statistic;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.logging.Logger;

public class RetrievalManager {

    // Temporary hack to flag context retrievals from certain context providers
    // to create events for monitoring.
    private static HashSet<String> monitored = new HashSet<>();

    private static final int retrys = 20;
    private static Logger log = Logger.getLogger(RetrievalManager.class.getName());

    // Retrieval from non-streaming Context Providers
    // Done
    public static AbstractMap.SimpleEntry<String,List<String>> executeFetch(String contextService, JSONObject qos,
                             HashMap<String,String> params, String cpId, String ccId, String entType,
                             Set<String> attributes, Boolean isFullMiss, ContextEntity subEnt) {
        CSIServiceGrpc.CSIServiceBlockingStub csiStub
                = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance().getChannel());

        final ContextServiceInvokerRequest.Builder fetchRequest = ContextServiceInvokerRequest.newBuilder();
        fetchRequest.putAllParams(params);

        final ContextService cs = ContextService.newBuilder().setJson(contextService).build();
        fetchRequest.setContextService(cs);

        CSIResponse fetch = null;
        double penEarning = 0.0;
        long startTime = 0;

        for(int i=0; i < retrys; i++){
            startTime = System.currentTimeMillis();
            fetch = csiStub.fetch(fetchRequest.build());
            if(fetch.getStatus().equals("200")) break;
            else {
                long int_endTime = System.currentTimeMillis();

                // Should penalties be earned if the CP fails to respond at all (non 200 responses, timeouts)
                long retLatency = int_endTime - startTime;
                double retDiff = retLatency - qos.getDouble("rtmax");
                if(retDiff > 0){
                    penEarning = (((int)(retDiff/1000.0))+1.0) * qos.getDouble("rate")
                            * qos.getDouble("penPct") / 100.0;
                }

                SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                        = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                asyncStub.logPerformanceData(Statistic.newBuilder()
                        .setIsDelayed(retDiff>0).setEarning(penEarning)
                        .setMethod("executeFetch").setStatus(fetch.getStatus())
                        .setTime(int_endTime-startTime).setCs(fetchRequest).setAge(0)
                        .setCost(!fetch.getStatus().equals("500")? fetch.getSummary().getPrice() : 0).build());
            }
        }

        // Since the end_time is after the retry attempts until success, what is being reported back to
        // ConQEng is accumalated time.
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
                else if(ageObj instanceof String) age = Long.valueOf((String) ageObj);
                else age = Long.valueOf((long) ageObj);
                response.put("zeroTime", startTime - age*1000);
            }
            else if(response.has("avgAge")){
                age = (long) response.getDouble("avgAge");
            }

            long retLatency = endTime-startTime;
            double retDiff = retLatency - qos.getDouble("rtmax");
            if(retDiff > 0){
                penEarning = (((int)(retDiff/1000.0))+1.0) * qos.getDouble("rate")
                        * qos.getDouble("penPct") / 100.0;
            }

            JSONArray key = (new JSONObject(contextService)).getJSONObject("sla").getJSONArray("key");
            List<String> hkeys = new ArrayList<>();

            if(response.has("results")){
                String hashkey = "";
                JSONArray entities = response.getJSONArray("results");
                JSONArray temp_entities = new JSONArray();

                for(int j=0; j<entities.length(); j++){
                    JSONObject resEntity = entities.getJSONObject(j);
                    for (int i = 0; i < key.length(); i++) {
                        Object idValue = response.get(key.getString(i));
                        hashkey += key.getString(i) + "@" + idValue.toString().replace("\"","") + ";";
                    }
                    String hk = Utilities.getHashKey(hashkey);
                    hkeys.add(hk);

                    resEntity.put("hashkey", hk);
                    String unit = resEntity.getJSONObject("age").getString("unit");
                    Double value = resEntity.getJSONObject("age").getDouble("value");

                    switch(unit){
                        case "s": value = value*1000; break;
                        case "h": value = value*60*1000; break;
                    }

                    resEntity.put("zeroTime", endTime - ( value + retLatency));
                    temp_entities.put(resEntity);

                    if(isFullMiss){
                        SQEMServiceGrpc.SQEMServiceFutureStub sqemStub_2
                                = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                        String contextId = entType + "-" + hk;
                        sqemStub_2.logContextAccess(ContextAccess.newBuilder()
                                .setContextId(contextId).setOutcome("miss")
                                .setAge(value + retLatency).build());
                    }
                }
                response.put("results", temp_entities);
            }
            else {
                String hashkey = "";
                for (int i = 0; i < key.length(); i++) {
                    Object idValue = response.get(key.getString(i));
                    hashkey += key.getString(i) + "@" + idValue.toString().replace("\"","") + ";";
                }
                String hk = Utilities.getHashKey(hashkey);
                hkeys.add(hk);
                response.put("hashkey", hk);

                if(isFullMiss){
                    JSONObject entage = response.optJSONObject("age");
                    SQEMServiceGrpc.SQEMServiceFutureStub sqemStub_2
                            = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                    String contextId = entType + "-" + hk;
                    sqemStub_2.logContextAccess(ContextAccess.newBuilder()
                            .setContextId(contextId).setOutcome("miss")
                            .setAge(entage.getDouble("value")*1000 + retLatency).build());
                }
            }

            if(subEnt != null && !monitored.contains(cpId)) {
                monitored.add(cpId);
                // Propagating the change to other retrieval managers as well

            }

            if(monitored.contains(cpId)){
                // Creating the event
                try {
                    PushBasedExecutor.sendEvent(ContextEvent.newBuilder()
                            .setTimestamp(String.valueOf(System.currentTimeMillis()))
                            .setContextEntity(subEnt) // Context entity
                            .setAttributes(response.toString()) // JSON attribute values from the retrieval
                            .build());
                } catch(Exception ex) {
                    log.severe("failed to create an event for the retrieval: " + ex.getMessage());
                }
            }

            SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                    = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
            asyncStub.logPerformanceData(Statistic.newBuilder()
                    .addAllHaskeys(hkeys)
                    .setMethod("executeFetch").setStatus(fetch.getStatus())
                    .setTime(retLatency).setCs(fetchRequest).setAge(age)
                    .setIsDelayed(retDiff>0).setEarning(penEarning)
                    .setCost(fetch.getStatus().equals("200")? fetch.getSummary().getPrice() : 0).build());

            return new AbstractMap.SimpleEntry(response.toString(),hkeys);
        }

        // Returning null means the CMP failed to retrieved context from the provider despite all attempts.
        return null;
    }

    // Retrieval from streaming Context Providers
    // Done
    public static AbstractMap.SimpleEntry<String,List<String>> executeStreamRead(String contextService, String csID,
                                           HashMap<String,String> params, Boolean isFullMiss){
        CSIServiceGrpc.CSIServiceBlockingStub csiStub
                = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance().getChannel());

        CSIResponse fetch = csiStub.updateFetchJob(ContextService.newBuilder()
                        .setJson(contextService).setMongoID(csID)
                        .setTimes(1).putAllParams(params).setReportAccess(isFullMiss?"True":"False")
                        .build());

        if(fetch.getStatus().equals("200"))
            return new AbstractMap.SimpleEntry("200",fetch.getHashkeysList());
        else{
            log.severe("Error occurred when trying to refresh in Storage");
            return new AbstractMap.SimpleEntry(fetch.getStatus(), null);
        }
    }
}
