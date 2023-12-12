package au.coaas.cre.server;

import au.coaas.sqem.proto.InferenceStat;
import io.grpc.*;
import au.coaas.cre.proto.CREResponse;
import au.coaas.cre.proto.ReasoningResponse;

import au.coaas.sqem.proto.Statistic;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.SQEMServiceGrpc;

import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author shakthi
 */
public class CREInterceptor implements ServerInterceptor {

    private static final String REQUEST = "Request";
    private static final String RESPONSE = "Response";
    private static final Logger logger = Logger.getLogger(CREInterceptor.class.getName());

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, final Metadata requestHeaders, ServerCallHandler<ReqT, RespT> next) {

        long startTime = System.currentTimeMillis();

        MethodDescriptor<ReqT, RespT> methodDescriptor = call.getMethodDescriptor();
        logMessage(REQUEST, methodDescriptor);

        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {

            @Override
            public void sendMessage(RespT message) {
                super.sendMessage(message);
                long endTime = System.currentTimeMillis();
                Executors.newCachedThreadPool().execute(() -> logMessage(RESPONSE,
                        methodDescriptor.getFullMethodName().replace("au.coaas.cre.proto.CREService/",""),
                        message, endTime - startTime));
            }

        }, requestHeaders);
    }

    private <T> void logMessage(String type, String method, T message, long responseTime) {
        // Asynchronously update the performance logs
        try{
            switch(method){
                case "infer": {
                    // Log response time
                    CREResponse res = (CREResponse) message;
                    SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                            = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

                    for(ReasoningResponse reasonRes : res.getBodyList()){
                        sqemStub.logInferencePerformance (
                                InferenceStat.newBuilder()
                                        .setConsumerId(res.getMeta())
                                        .setResponse(reasonRes)
                                        .setStat(Statistic.newBuilder()
                                                .setMethod(method).setTime(responseTime)
                                                .setStatus(res.getStatus()).setIdentifier(reasonRes.getSituationTitle())
                                                .build())
                                        .build());
                    }

                    break;
                }
                default:
                    log(String.format("%s responded in %d ms.", method, responseTime));
                    log(String.format("%s message : %s", type, message));
                    break;
            }
        }
        catch(Exception ex){
            log(ex.getMessage());
        }
    }

    private <T> void logMessage(String type, T message) {
        // Asynchronously update the performance logs
        log(String.format("%s message : %s", type, message));
    }

    protected void log(String message) {
        logger.info(message);
    }
}
