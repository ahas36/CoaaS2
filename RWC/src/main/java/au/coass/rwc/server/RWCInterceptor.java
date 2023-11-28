package au.coass.rwc.server;

import au.coaas.sqem.proto.EdgeStatus;
import io.grpc.*;

public class RWCInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata metadata, ServerCallHandler<ReqT, RespT> next) {

        String address = call.getAttributes().get(Grpc.TRANSPORT_ATTR_LOCAL_ADDR).toString();
        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void sendMessage(RespT message) {
                RespT modifiedMessage = modify(message, address);
                super.sendMessage(modifiedMessage);
            }
        }, metadata);
    }

    private <RespT> RespT modify(RespT message, String ip) {
        if(message.getClass().isInstance(EdgeStatus.class)){
            EdgeStatus origMessage = (EdgeStatus) message;
            return (RespT) origMessage.toBuilder()
                    .setIpAddress(ip).build();
        }
        return message;
    }
}
