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
 * @author ali & shakthi
 */
public class SQEMChannel {

    private ManagedChannel channel;

    private static SQEMChannel instance;

    // Master Node and Worker (Internal) Channel Resolution.
    private SQEMChannel() {
        channel = ManagedChannelBuilder.forAddress("0.0.0.0", 8686)
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static SQEMChannel getInstance() {
        if (instance == null) {
            //synchronized block to remove overhead 
            synchronized (SQEMChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize 
                    instance = new SQEMChannel();
                }
            }
        }
        return instance;
    }

    // Location-based Overload for Master-Worker Node Channel Resolution.
    private SQEMChannel(long index) {
        AbstractMap.SimpleEntry<String, Integer> ipAddress = IPResolver.convertIpToLocation(index, Services.SQEM);
        channel = ManagedChannelBuilder.forAddress(ipAddress.getKey(), ipAddress.getValue())
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static SQEMChannel getInstance(long index) {
        if (instance == null) {
            //synchronized block to remove overhead
            synchronized (SQEMChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize
                    instance = new SQEMChannel(index);
                }
            }
        }
        return instance;
    }

    // Getter of the SQEM channel.
    public ManagedChannel getChannel() {
        return channel;
    }
    
    
}
