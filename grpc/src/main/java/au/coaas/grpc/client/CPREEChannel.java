package au.coaas.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

public class CPREEChannel {
    private ManagedChannel channel;

    private static CPREEChannel instance;

    private CPREEChannel() {
        channel = ManagedChannelBuilder.forAddress("cpree", 9292)
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static CPREEChannel getInstance() {
        if (instance == null) {
            //synchronized block to remove overhead
            synchronized (CPREEChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize
                    instance = new CPREEChannel();
                }

            }
        }
        return instance;
    }

    public ManagedChannel getChannel() {
        return channel;
    }
}
