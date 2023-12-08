package au.coaas.csi.fetch;

import au.coaas.cpree.proto.CPREEServiceGrpc;
import au.coaas.cqc.proto.CQCServiceGrpc;
import au.coaas.cqp.proto.ContextEntity;
import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.cre.proto.ContextEvent;
import au.coaas.csi.proto.*;
import au.coaas.csi.utils.HttpResponseFuture;
import au.coaas.csi.utils.Utils;
import au.coaas.grpc.client.CPREEChannel;
import au.coaas.grpc.client.CQCChannel;
import au.coaas.grpc.client.CSIChannel;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FetchJob implements Job {

    private static HashMap<String, HashSet<ContextEntity>> monitored = new HashMap<>();

    private static final int retrys = 20;
    private static Logger log = Logger.getLogger(FetchJob.class.getName());

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            execute(context.getTrigger().getJobDataMap());
        }catch (Exception e){
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String> execute(JobDataMap dataMap) {
        try {
            String contextService = dataMap.getString("cs");
            HashMap<String,String> params = new Gson().fromJson(
                    dataMap.getString("params"),
                    new TypeToken<HashMap<String, String>>() {}.getType());

            long startTime = 0;
            double penEarning = 0.0;
            CSIResponse fetch = null;

            JSONObject qos = (new JSONObject(contextService)).getJSONObject("sla").getJSONObject("qos");

            ContextServiceInvokerRequest request = ContextServiceInvokerRequest.newBuilder()
                    .setContextService(ContextService.newBuilder().setJson(contextService))
                    .putAllParams(params)
                    .build();

            for(int i=0; i < retrys; i++){
                startTime = System.currentTimeMillis();
                fetch = FetchManager.fetch(request);

                if(fetch.getStatus().equals("200")) break;
                else {
                    long int_endTime = System.currentTimeMillis();
                    long retLatency = int_endTime-startTime;
                    double retDiff = retLatency - qos.getDouble("rtmax");
                    if(retDiff > 0){
                        penEarning = (((int)(retDiff/1000.0))+1.0) * qos.getDouble("rate")
                                * qos.getDouble("penPct") / 100.0;
                    }

                    SQEMServiceGrpc.SQEMServiceBlockingStub asyncStub
                            = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

                    asyncStub.logPerformanceData(Statistic.newBuilder()
                            .setIsDelayed(retDiff>0).setEarning(penEarning)
                            .setMethod("FetchJob-execute").setStatus(fetch.getStatus())
                            .setTime(int_endTime-startTime).setCs(request).setAge(0)
                            .setCost(!fetch.getStatus().equals("500")? fetch.getSummary().getPrice() : 0.0).build());
                }
            }

            if(fetch.getStatus().equals("200"))
            {
                long endTime = System.currentTimeMillis();
                long retLatency = endTime-startTime;
                ContextEntityType et = ContextEntityType.newBuilder().
                        setVocabURI(dataMap.getString("graph")).
                        setType(dataMap.getString("ontClass")).build();

                UpdateEntityRequest.Builder updateRequest = UpdateEntityRequest.newBuilder()
                        .setEt(et)
                        .setJson(fetch.getBody())
                        .setProviderId(dataMap.getString("providerId"))
                        // This key is the unique identifier attribute(s) of the entity.
                        .setKey(dataMap.getString("key"))
                        .setResolveLocation(dataMap.getBoolean("updateRegistry"))
                        .setReportAccess(dataMap.getBoolean("reportAccess") ? "True" : "False")
                        .setRetLatency(retLatency);

                if(dataMap.getBoolean("updateRegistry")) {
                    updateRequest.setParamHash(Utils.getHashKey(params));
                    dataMap.put("updateRegistry", false);
                }

                SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                        = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
                SQEMResponse sqemResponse = sqemStub.updateContextEntity(updateRequest.build());

                // Check of the entity has gone out of the current edge node zone.
                if(!dataMap.getBoolean("updateRegistry")) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    CSIResponse finalFetch1 = fetch;
                    executor.submit(() -> {
                        try {
                            EdgeDevice device = sqemStub.checkMigration(Migration.newBuilder()
                                            .setResponse(finalFetch1.getBody())
                                            .setLastIndex(dataMap.getLong("index")).build());
                            if(device.getChange()) {
                                // TODO:
                                // Start hot context migration.
                            }
                        } catch (Exception ex) {
                            log.severe("Could not update the provider-edge subscription due to: "
                                    + ex.getMessage());
                        }
                    });
                }

                // This calculation is unnessecary if running in complete DB mode.
                double retDiff = retLatency - qos.getDouble("rtmax");
                if(retDiff > 0){
                    penEarning = (((int)(retDiff/1000.0))+1.0) * qos.getDouble("rate")
                            * qos.getDouble("penPct") / 100.0;
                }

                JSONObject fr = new JSONObject(fetch.getBody());
                Double age = fr.has("results") ? fr.getDouble("avgAge")
                        : fr.getJSONObject("age").getDouble("value");

                JSONObject sqemResBody = new JSONObject(sqemResponse.getBody());
                List<String> hkeys = new ArrayList<>();
                if(sqemResBody.has("hashkeys")){
                    JSONArray ks = sqemResBody.getJSONArray("hashkeys");
                    for(int i = 0; i < ks.length(); i++){
                        hkeys.add(ks.getString(i));
                    }
                }
                else {
                    hkeys.add(sqemResBody.getString("hashkey"));

                    // The following part regards to entity monitoring is strictly single entity only.
                    CSIResponse finalFetch = fetch;
                    Executors.newCachedThreadPool().execute(() -> {
                        try {
                            Message subEnt = Utils.fromJson(dataMap.getString("subscriptionEntity"), ContextEntity.newBuilder());
                            propagateChanges((ContextEntity) subEnt, dataMap.getString("providerId"),
                                    dataMap.getString("ontClass") + "-" + sqemResBody.getString("hashkey"),
                                    new JSONObject(finalFetch.getBody()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }

                SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                        = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());

                asyncStub.logPerformanceData(Statistic.newBuilder().setCs(request)
                        .setMethod("FetchJob-execute").setStatus(fetch.getStatus())
                        .setTime(retLatency).setCs(request).setAge(age.longValue())
                        .setIsDelayed(retDiff>0).setEarning(penEarning)
                        .addAllHaskeys(hkeys)
                        .setCost(fetch.getStatus().equals("200")? fetch.getSummary().getPrice() : 0.0).build());

                if(sqemResponse.getStatus().equals("200")){
                    log.info("Fetch Job Successfully completed from : " + dataMap.getString("providerId"));
                    log.info(sqemResponse.getBody());
                    return hkeys;
                }
            }
            else {
                log.severe("Fetch Job FAILED form : " + dataMap.getString("providerId"));
                log.info(fetch.getBody());
            }
        }catch (Exception e){
            log.severe(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void updateMonitored (CPMonitor request) {
        if(request.getDelete() && monitored.containsKey(request.getContextID())){
            HashSet<ContextEntity> tmp = monitored.get(request.getContextID());
            tmp.remove(request.getContextEntity());
            if(tmp.isEmpty()){
                monitored.remove(request.getContextID());
                try {
                    String providerID = request.getProviderID();
                    String hashkey = (request.getContextID().split("-"))[1];
                    JobSchedulerManager.getInstance().cancelJob(providerID + "-" + hashkey);
                }
                catch(Exception ex) {
                    log.severe(ex.getMessage());
                }
            }
            else {
                monitored.put(request.getContextID(), tmp);
            }
        }
        else changeRegistry(request.getContextEntity(), request.getContextID());
    }

    private static boolean changeRegistry(ContextEntity subEntity, String contextId) {
        if(!monitored.containsKey(contextId)){
            HashSet<ContextEntity> tmpSet = new HashSet<>();
            tmpSet.add(subEntity);
            monitored.put(contextId, tmpSet);
            return true;
        }
        else {
            HashSet<ContextEntity> tmpSet = monitored.get(contextId);
            int init_count = tmpSet.size();
            tmpSet.add(subEntity);
            monitored.put(contextId, tmpSet);
            return init_count != tmpSet.size() ? true : false;
        }
    }

    private static void propagateChanges(ContextEntity subEnt, String cpId, String entId, JSONObject response) {
        // This function is assuming only a single entity because the monitoring need to happen on a
        // single entity only (theorotically).
        if(subEnt != null) {
            if(changeRegistry(subEnt, entId)) {
                // Propagating the change to other retrieval managers as well
                CPREEServiceGrpc.CPREEServiceBlockingStub cpreeStub
                        = CPREEServiceGrpc.newBlockingStub(CPREEChannel.getInstance().getChannel());
                CQCServiceGrpc.CQCServiceBlockingStub cqcStub
                        = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());

                cpreeStub.modifyCPMonitor(au.coaas.cpree.proto.CPMonitor.newBuilder()
                        .setContextEntity(subEnt).setContextID(entId).setDelete(false).build());
                cqcStub.modifyCPMonitor(au.coaas.cqc.proto.CPMonitor.newBuilder()
                        .setContextEntity(subEnt).setContextID(entId).build());
            }
        }

        if(monitored.containsKey(entId)){
            // Creating the event
            try {
                if(subEnt != null) {
                    // Specific event creation only relevant to the current push query registration.
                    sendEvent(ContextEvent.newBuilder()
                            .setKey(entId)
                            .setTimestamp(String.valueOf(System.currentTimeMillis()))
                            .setProviderID(cpId)
                            .setSubscriptionID(subEnt.getSub()) // Subscription Id
                            .setContextEntity(subEnt) // Context entity
                            .setAttributes(response.toString()) // JSON attribute values from the retrieval
                            .build());
                }
                else {
                    // Generic event creation for all subscriptions.
                    HashSet<ContextEntity> ents = monitored.get(entId);
                    for(ContextEntity ent : ents) {
                        sendEvent(ContextEvent.newBuilder()
                                .setKey(entId)
                                .setTimestamp(String.valueOf(System.currentTimeMillis()))
                                .setProviderID(cpId)
                                .setSubscriptionID(subEnt.getSub()) // Subscription Id
                                .setContextEntity(ent) // Context entity
                                .setAttributes(response.toString()) // JSON attribute values from the retrieval
                                .build());
                    }
                }
            } catch(Exception ex) {
                log.severe("failed to create an event for the retrieval: " + ex.getMessage());
            }
        }
    }

    // Triggers a recursive event to CoaaS
    public static void sendEvent(ContextEvent event) throws InvalidProtocolBufferException {
        String eventURI = "http://localhost:8070/CASM-2.0.1/api/event/create";

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();

        HttpResponseFuture fu_res = new HttpResponseFuture();

        MediaType dataType = MediaType.parse("application/json; charset=utf-8");
        Request.Builder request = new Request.Builder().url(eventURI);

        RequestBody formBody = RequestBody.create(dataType, JsonFormat.printer().print(event));
        request.post(formBody);

        try{
            Call call = client.newCall(request.build());
            call.enqueue(fu_res);
            Response response = fu_res.future.get();
            response.close();
        }
        catch (ExecutionException | InterruptedException ex) {
            log.info(ex.getMessage());
        }
    }
}