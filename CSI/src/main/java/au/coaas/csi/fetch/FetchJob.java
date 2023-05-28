package au.coaas.csi.fetch;

import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.ContextService;
import au.coaas.csi.proto.ContextServiceInvokerRequest;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.Statistic;
import au.coaas.sqem.proto.UpdateEntityRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class FetchJob implements Job {

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

    public List<String> execute(JobDataMap dataMap) throws JobExecutionException {
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
                else hkeys.add(sqemResBody.getString("hashkey"));

                SQEMServiceGrpc.SQEMServiceBlockingStub asyncStub
                        = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

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
}