package au.coaas.csi.fetch;

import au.coaas.cqp.proto.ContextEntity;
import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.cre.proto.ContextEvent;
import au.coaas.csi.utils.HttpResponseFuture;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.ContextService;
import au.coaas.csi.proto.ContextServiceInvokerRequest;
import au.coaas.sqem.proto.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.InvalidProtocolBufferException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FetchJob implements Job {

    private static HashMap<String, ContextEntity> monitored = new HashMap<>();

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

                UpdateEntityRequest updateRequest = UpdateEntityRequest.newBuilder()
                        .setEt(et)
                        .setJson(fetch.getBody())
                        .setProviderId(dataMap.getString("providerId"))
                        .setKey(dataMap.getString("key"))
                        .setReportAccess(dataMap.getBoolean("reportAccess")?"True":"False")
                        // This key is the unique identifier attribute(s) of the entity.
                        .setRetLatency(retLatency)
                        .build();

                SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                        = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

                SQEMResponse sqemResponse = sqemStub.updateContextEntity(updateRequest);

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
                    if(monitored.containsKey(sqemResBody.getString("hashkey"))){
                        // Creating the event if it should be monitored.
                        try {
                            sendEvent(ContextEvent.newBuilder()
                                    .setTimestamp(String.valueOf(System.currentTimeMillis()))
                                    .setContextEntity(monitored.get(sqemResBody.getString("hashkey"))) // Context entity
                                    .setAttributes(fetch.getBody()) // JSON attribute values from the retrieval
                                    .build());
                        } catch(Exception ex) {
                            log.severe("Failed to create an event for the retrieval: " + ex.getMessage());
                        }
                    }
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
                    log.info("Fetch Job Successfully completed form : " + dataMap.getString("providerId"));
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

    public static void updateMonitored (String contextId, ContextEntity subEntity, boolean delete) {
        if(delete && monitored.containsKey(contextId)){
            monitored.remove(contextId);
        }
        monitored.put(contextId, subEntity);
    }
}