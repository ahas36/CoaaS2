package au.coaas.sqem.server;

import io.grpc.*;

import java.util.logging.Logger;

public class SQEMInterceptor implements ServerInterceptor {

    private static final Logger logger = Logger.getLogger(SQEMInterceptor.class.getName());

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, final Metadata requestHeaders, ServerCallHandler<ReqT, RespT> next) {
        logger.info("Message recieved:" + requestHeaders);
        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            // Need to log time here
        }, requestHeaders);
    }
}
