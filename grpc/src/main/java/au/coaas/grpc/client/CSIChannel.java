/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.grpc.client;

import au.coaas.grpc.utils.IPResolver;
import au.coaas.grpc.utils.Services;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.AbstractMap;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

/**
 *
 * @author ali
 */
public class CSIChannel {

    private ManagedChannel channel;

    private static CSIChannel instance;

    // Master Node and Worker (Internal) Channel Resolution.
    private CSIChannel() {
        channel = ManagedChannelBuilder.forAddress("0.0.0.0", 8582)
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static CSIChannel getInstance() {
        if (instance == null) {
            //synchronized block to remove overhead 
            synchronized (CSIChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize 
                    instance = new CSIChannel();
                }
            }
        }
        return instance;
    }

    // Location-based Overload for Master-Worker Node Channel Resolution.
    private CSIChannel(long index) {
        AbstractMap.SimpleEntry<String, Integer> ipAddress = IPResolver.convertIpToLocation(index, Services.CSI);
        channel = ManagedChannelBuilder.forAddress(ipAddress.getKey(), ipAddress.getValue())
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static CSIChannel getInstance(long index) {
        if (instance == null) {
            //synchronized block to remove overhead
            synchronized (CSIChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize
                    instance = new CSIChannel(index);
                }
            }
        }
        return instance;
    }

    // Getter of the CSI channel.
    public ManagedChannel getChannel() {
        return channel;
    }
    
    
}
