package au.coaas.cqc.server;

import io.grpc.*;

import java.util.logging.Logger;

public class CQCInterceptor implements ServerInterceptor {

    private static final String REQUEST = "Request";
    private static final String RESPONSE = "Response";
    private static final Logger logger = Logger.getLogger(CQCInterceptor.class.getName());

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, final Metadata requestHeaders, ServerCallHandler<ReqT, RespT> next) {

        MethodDescriptor<ReqT, RespT> methodDescriptor = call.getMethodDescriptor();
        logMessage(REQUEST, methodDescriptor);

        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {

            @Override
            public void sendMessage(RespT message) {
                String method = this.delegate().getMethodDescriptor().getFullMethodName();
                logMessage(RESPONSE, method, message);
                super.sendMessage(message);
            }

        }, requestHeaders);
    }

    private <T> void logMessage(String type, String method, T message) {
        // Asynchronously update the performance logs
        log(String.format("%s message : %s", type, message));
    }

    private <T> void logMessage(String type, T message) {
        // Asynchronously update the performance logs
        log(String.format("%s message : %s", type, message));
    }

    protected void log(String message) {
        logger.info(message);
    }

}
