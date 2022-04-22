package au.coaas.sqem.server;

import io.grpc.*;

import java.util.EnumSet;
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
                logMessage(RESPONSE,
                        methodDescriptor.getFullMethodName().replace("au.coaas.sqem.proto.SQEMService/",""),
                        "200", message, endTime - startTime);
            }

        }, requestHeaders);
    }

    private <T> void logMessage(String type, String method, String status, T message, long responseTime) {
        // Asynchronously update the performance logs
        switch(method){
            case "handleContextRequest":
            case "discoverMatchingServices":
            case "refreshContextEntity": {
                // Log response time
                break;
            }
            case "handleContextRequestInCache": {
                if(status == "200"){
                    // Cache hit scenario
                }
                else if(status == "400"){
                    // Partial hit scenario (Cached but not fresh)
                }
                else if (status == "404"){
                    // Not cached scenario
                }
                break;
            }
            default:
                log(String.format("%s responded in %d ms.", method, responseTime));
                log(String.format("%s message : %s", type, message));
                break;
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
