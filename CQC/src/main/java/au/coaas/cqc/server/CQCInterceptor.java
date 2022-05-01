package au.coaas.cqc.server;

import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.Statistic;
import io.grpc.*;

import java.util.logging.Logger;

public class CQCInterceptor implements ServerInterceptor {

    private static final String REQUEST = "Request";
    private static final String RESPONSE = "Response";
    private static final Logger logger = Logger.getLogger(CQCInterceptor.class.getName());

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
                logMessage(RESPONSE,
                        methodDescriptor.getFullMethodName().replace("au.coaas.sqem.proto.CQCService/",""),
                        message, endTime - startTime);
            }

        }, requestHeaders);
    }

    private <T> void logMessage(String type, String method, T message, long responseTime) {
        // Asynchronously update the performance logs
        try{
            switch(method){
                case "execute": {
                    // Log response time
                    CdqlResponse res = (CdqlResponse) message;
                    double penalty = 0;
                    double earning = 0;

                    if(res.getAdmin() != null){
                        if(responseTime >= res.getAdmin().getRtmax())
                            penalty = res.getAdmin().getRtpenalty();
                        else
                            earning = res.getAdmin().getPrice();
                    }

                    SQEMServiceGrpc.SQEMServiceFutureStub sqemStub
                            = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                    sqemStub.logPerformanceData (Statistic.newBuilder()
                            .setMethod(method).setTime(responseTime)
                            .setStatus(res.getStatus()).setIdentifier(res.getQueryId())
                            .setCost(penalty) // This is penalty
                            .setEarning(earning) // This is earning
                            .setIsDelayed(penalty>0 ? true: false)
                            .build());
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
