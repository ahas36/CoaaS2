package au.coaas.grpc.client;

import au.coaas.grpc.utils.IPResolver;
import au.coaas.grpc.utils.Services;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.AbstractMap;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

/**
 * @author shakthi
 */
public class RWCChannel {
    private ManagedChannel channel;

    private static RWCChannel instance;

    // Master Node and Worker (Internal) Channel Resolution.
    private RWCChannel() {
        channel = ManagedChannelBuilder.forAddress("0.0.0.0", 6868)
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static RWCChannel getInstance() {
        if (instance == null) {
            //synchronized block to remove overhead
            synchronized (RWCChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize
                    instance = new RWCChannel();
                }
            }
        }
        return instance;
    }

    // Location-based Overload for Master-Worker Node Channel Resolution.
    private RWCChannel(long index) {
        AbstractMap.SimpleEntry<String, Integer> ipAddress = IPResolver.convertIpToLocation(index, Services.RWC);
        channel = ManagedChannelBuilder.forAddress(ipAddress.getKey(), ipAddress.getValue())
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static RWCChannel getInstance(long index) {
        if (instance == null) {
            //synchronized block to remove overhead
            synchronized (RWCChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize
                    instance = new RWCChannel(index);
                }
            }
        }
        return instance;
    }

    // Getter of the CQC channel.
    public ManagedChannel getChannel() {
        return channel;
    }
}
