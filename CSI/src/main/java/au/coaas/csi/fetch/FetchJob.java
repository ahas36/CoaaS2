package au.coaas.csi.fetch;

import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.ContextService;
import au.coaas.csi.proto.ContextServiceInvokerRequest;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.UpdateEntityRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.logging.Logger;

public class FetchJob implements Job {

    private static Logger log = Logger.getLogger(FetchJob.class.getName());

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap dataMap = context.getTrigger().getJobDataMap();
            String contextService = dataMap.getString("cs");
            HashMap<String,String> params = new Gson().fromJson(
                    dataMap.getString("params"),
                    new TypeToken<HashMap<String, String>>() {}.getType());

            long startTime = System.currentTimeMillis();
            CSIResponse fetch = FetchManager.fetch(ContextServiceInvokerRequest.newBuilder()
                    .setContextService(ContextService.newBuilder().setJson(contextService))
                    .putAllParams(params)
                    .build());

            if(fetch.getStatus().equals("200"))
            {
                long endTime = System.currentTimeMillis();
                long retLatency = endTime-startTime;
                ContextEntityType et = ContextEntityType.newBuilder().
                        setVocabURI(dataMap.getString("graph")).
                        setType(dataMap.getString("ontClass")).build();

                UpdateEntityRequest updateRequest = UpdateEntityRequest.newBuilder().setEt(et)
                        .setJson(fetch.getBody())
                        .setKey(dataMap.getString("key"))
                        .setRetLatency(retLatency)
                        .build();

                SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                        = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

                SQEMResponse sqemResponse = sqemStub.updateContextEntity(updateRequest);

                SQEMServiceGrpc.SQEMServiceBlockingStub asyncStub
                        = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

                // TODO: Need to push stats if this will be used.
//                asyncStub.logPerformanceData(Statistic.newBuilder()
//                        .setMethod("FetchJob-execute").setStatus(fetch.getStatus())
//                        .setTime(retLatency).setCs(contextService).setAge(age)
//                        .setIsDelayed(retDiff>0).setEarning(penEarning)
//                        .setCost(fetch.getStatus().equals("200")? fetch.getSummary().getPrice() : 0).build());

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