package au.coaas.sqem.server;

import io.grpc.*;

import java.util.EnumSet;
import java.util.logging.Logger;

public class SQEMInterceptor implements ServerInterceptor {

    private static final String REQUEST = "Request";
    private static final String RESPONSE = "Response";
    private static final Logger logger = Logger.getLogger(SQEMInterceptor.class.getName());

    public enum Level {
        METHOD, MESSAGE
    }

    private EnumSet<Level> levels = EnumSet.of(Level.METHOD);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, final Metadata requestHeaders, ServerCallHandler<ReqT, RespT> next) {

        MethodDescriptor<ReqT, RespT> methodDescriptor = call.getMethodDescriptor();
        logMessage(REQUEST, methodDescriptor);

        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {

            @Override
            public void sendMessage(RespT message) {
                logMessage(RESPONSE, message);
                super.sendMessage(message);
            }

        }, requestHeaders);
    }

    private <T> void logMessage(String type, T message) {
        if (levels.contains(Level.MESSAGE)) {
            log(String.format("%s message : %s", type, message));
        }
    }

    protected void log(String message) {
        logger.info(message);
    }

}
