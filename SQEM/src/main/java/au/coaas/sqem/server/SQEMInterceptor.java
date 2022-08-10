package au.coaas.sqem.server;

import au.coaas.sqem.handler.PerformanceLogHandler;
import au.coaas.sqem.monitor.LogicalContextLevel;
import au.coaas.sqem.proto.Empty;
import au.coaas.sqem.proto.SQEMResponse;
import io.grpc.*;

import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class SQEMInterceptor implements ServerInterceptor {

    private static final String REQUEST = "Request";
    private static final String RESPONSE = "Response";
    private static final Logger logger = Logger.getLogger(SQEMInterceptor.class.getName());

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
                Executors.newCachedThreadPool().execute(() ->
                    logMessage(RESPONSE, methodDescriptor.getFullMethodName().replace("au.coaas.sqem.proto.SQEMService/",""),
                            message, endTime - startTime));
            }

        }, requestHeaders);
    }

    private <T> void logMessage(String type, String method, T message, long responseTime) {
        // Asynchronously update the performance logs
        try{
            if(!message.toString().equals("")){
                SQEMResponse res = (SQEMResponse) message;
                switch(method){
                    case "handleContextRequest":
                    case "discoverMatchingServices":
                    case "refreshContextEntity": {
                        // Log response time
                        PerformanceLogHandler.genericRecord(method, res.getStatus(), responseTime);
                        break;
                    }
                    case "handleContextRequestInCache": {
                        // LogicalContextLevel level, String id, Boolean isHit, long rTime
                        PerformanceLogHandler.insertRecord(LogicalContextLevel.ENTITY, res.getHashKey(),
                                res.getStatus().equals("200"), responseTime);
                        break;
                    }
                    case "logPerformanceData": break;
                    default:
                        log(String.format("%s responded in %d ms.", method, responseTime));
                        log(String.format("%s message : %s", type, message));
                        break;
                }
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

    protected void log(String message) { logger.info(message); }

}
