package au.coaas.grpc.client;

import au.coaas.grpc.utils.IPResolver;
import au.coaas.grpc.utils.Services;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.AbstractMap;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

public class CPREEChannel {
    private ManagedChannel channel;

    private static CPREEChannel instance;

    // Master Node and Worker (Internal) Channel Resolution.
    private CPREEChannel() {
        channel = ManagedChannelBuilder.forAddress("0.0.0.0", 9292)
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

    // Location-based Overload for Master-Worker Node Channel Resolution.
    private CPREEChannel(long index) {
        AbstractMap.SimpleEntry<String, Integer> ipAddress = IPResolver.convertIpToLocation(index, Services.CPREE);
        channel = ManagedChannelBuilder.forAddress(ipAddress.getKey(), ipAddress.getValue())
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static CPREEChannel getInstance(long index) {
        if (instance == null) {
            //synchronized block to remove overhead
            synchronized (CPREEChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize
                    instance = new CPREEChannel(index);
                }
            }
        }
        return instance;
    }

    // Getter of the CPREE channel.
    public ManagedChannel getChannel() {
        return channel;
    }
}
