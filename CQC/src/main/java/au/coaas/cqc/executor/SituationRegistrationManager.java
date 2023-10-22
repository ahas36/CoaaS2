package au.coaas.cqc.executor;

import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqp.proto.ContextFunction;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.RegisterSituationRequest;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;

import java.util.logging.Logger;

public class SituationRegistrationManager {
    private static Logger log = Logger.getLogger(SituationRegistrationManager.class.getName());

    public static CdqlResponse register(String raw, String token, ContextFunction function, String queryId) {
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        String title = function.getFunctionTitle();
        if (function.getPackage() != null && function.getPackage().trim().length() > 0) {
            title = function.getPackage() + "." + title;
        }
        RegisterSituationRequest request = RegisterSituationRequest.newBuilder()
                .setRaw(raw).setSFunction(function.getSFunction())
                .setToken(token).setTitle(title).build();
        SQEMResponse response = sqemStub.registerSituationFunction(request);

        return CdqlResponse.newBuilder().setStatus(response.getStatus())
                .setBody(response.getBody())
                .setQueryId(queryId)
                .build();
    }

}
