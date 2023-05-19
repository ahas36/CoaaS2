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
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.logging.Logger;

public class FetchJob implements Job {

    private static final int retrys = 20;
    private static Logger log = Logger.getLogger(FetchJob.class.getName());

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap dataMap = context.getTrigger().getJobDataMap();
            String contextService = dataMap.getString("cs");
            HashMap<String,String> params = new Gson().fromJson(
                    dataMap.getString("params"),
                    new TypeToken<HashMap<String, String>>() {}.getType());

            long startTime = 0;
            double penEarning = 0;
            CSIResponse fetch = null;

            JSONObject qos = (new JSONObject(contextService)).getJSONObject("sla").getJSONObject("qos");

            ContextServiceInvokerRequest request  = ContextServiceInvokerRequest.newBuilder()
                    .setContextService(ContextService.newBuilder().setJson(contextService))
                    .putAllParams(params)
                    .build();
            SQEMServiceGrpc.SQEMServiceBlockingStub asyncStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

            for(int i=0; i < retrys; i++){
                startTime = System.currentTimeMillis();
                fetch = FetchManager.fetch(request);

                if(fetch.getStatus().equals("200")) break;
                else {
                    long int_endTime = System.currentTimeMillis();
                    long retLatency = int_endTime-startTime;
                    double retDiff = retLatency - qos.getDouble("rtmax");
                    if(retDiff > 0){
                        penEarning = (((int)(retDiff/1000))+1) * qos.getDouble("rate")
                                * qos.getDouble("penPct") / 100;
                    }

                    asyncStub.logPerformanceData(Statistic.newBuilder()
                            .setIsDelayed(retDiff>0).setEarning(penEarning)
                            .setMethod("executeFetch").setStatus(fetch.getStatus())
                            .setTime(int_endTime-startTime).setCs(request).setAge(0)
                            .setCost(!fetch.getStatus().equals("500")? fetch.getSummary().getPrice() : 0).build());
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
                    penEarning = (((int)(retDiff/1000))+1) * qos.getDouble("rate")
                            * qos.getDouble("penPct") / 100;
                }

                JSONObject fr = new JSONObject(fetch.getBody());
                Double age = fr.has("results") ? fr.getDouble("avgAge")
                        : fr.getJSONObject("age").getDouble("value");

                asyncStub.logPerformanceData(Statistic.newBuilder()
                        .setMethod("FetchJob-execute").setStatus(fetch.getStatus())
                        .setTime(retLatency).setCs(request).setAge(age.longValue())
                        .setIsDelayed(retDiff>0).setEarning(penEarning)
                        .setCost(fetch.getStatus().equals("200")? fetch.getSummary().getPrice() : 0).build());

                if(!sqemResponse.getStatus().equals("200")){
                    log.info(sqemResponse.getBody());
                }
            }
            else {
                log.info(fetch.getBody());
            }
        }catch (Exception e){
            log.info(e.getMessage());
            e.printStackTrace();
        }

    }
}