package au.coaas.csi.fetch;

import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.ContextService;
import au.coaas.csi.proto.ContextServiceInvokerRequest;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.UpdateEntityRequest;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.logging.Logger;

public class FetchJob implements Job {

    private static Logger log = Logger.getLogger(FetchJob.class.getName());

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap dataMap = context.getTrigger().getJobDataMap();
            String contextService = dataMap.getString("cs");
            CSIResponse fetch = FetchManager.fetch(ContextServiceInvokerRequest.newBuilder().
                    setContextService(ContextService.newBuilder().setJson(contextService).build())
                    .build());

            if(fetch.getStatus().equals("200"))
            {
                ContextEntityType et = ContextEntityType.newBuilder().
                        setVocabURI(dataMap.getString("graph")).
                        setType(dataMap.getString("ontClass")).build();

                UpdateEntityRequest updateRequest = UpdateEntityRequest.newBuilder().setEt(et)
                        .setJson(fetch.getBody())
                        .setKey(dataMap.getString("key"))
                        .build();

                SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                        = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

                SQEMResponse sqemResponse = sqemStub.updateContextEntity(updateRequest);

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