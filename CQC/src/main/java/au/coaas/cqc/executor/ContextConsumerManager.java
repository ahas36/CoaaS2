package au.coaas.cqc.executor;

import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqc.proto.ExecutionRequest;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.*;
import org.json.JSONObject;

import java.util.logging.Logger;

public class ContextConsumerManager {

    private static Logger log = Logger.getLogger(ContextServiceManager.class.getName());

    public static CdqlResponse registerContextConsumer(ExecutionRequest request)
    {
        SQEMResponse sqemResponse = null;
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub = null;

        try {
            sqemStub = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
            sqemResponse = sqemStub.registerContextConsumer(RegisterContextConsumerRequest.newBuilder()
                    .setJson(request.getCdql()).build());

            return CdqlResponse.newBuilder().setBody(sqemResponse.getBody())
                    .setStatus(sqemResponse.getStatus()).build();
        }
        catch (Exception e)
        {
            if(sqemResponse!=null && sqemResponse.getStatus().equals("200"))
            {
                JSONObject sqemBody = new JSONObject(sqemResponse.getBody());
                String id = sqemBody.getString("id");
                sqemStub.updateContextConsumerStatus(UpdateContextConsumerStatusRequest.newBuilder()
                        .setId(id).setStatus(false).build());

                sqemBody.put("status", false);
                sqemBody.put("cause",new JSONObject(e.getMessage()));

                return CdqlResponse.newBuilder().setBody(sqemBody.toString())
                        .setStatus(sqemResponse.getStatus()).build();
            }

            return CdqlResponse.newBuilder().setBody(sqemResponse.getBody())
                    .setStatus(sqemResponse.getStatus()).build();
        }

    }
}
