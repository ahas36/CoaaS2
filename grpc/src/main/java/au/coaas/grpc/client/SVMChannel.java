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
 * @author ali & shakthi
 */

public class SVMChannel {

    private ManagedChannel channel;

    private static SVMChannel instance;

    // Master Node and Worker (Internal) Channel Resolution.
    private SVMChannel() {
        channel = ManagedChannelBuilder.forAddress("0.0.0.0", 9191)
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static SVMChannel getInstance() {
        if (instance == null) {
            //synchronized block to remove overhead 
            synchronized (SVMChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize 
                    instance = new SVMChannel();
                }
            }
        }
        return instance;
    }

    // Location-based Overload for Master-Worker Node Channel Resolution.
    private SVMChannel(long index) {
        AbstractMap.SimpleEntry<String, Integer> ipAddress = IPResolver.convertIpToLocation(index, Services.SVM);
        channel = ManagedChannelBuilder.forAddress(ipAddress.getKey(), ipAddress.getValue())
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static SVMChannel getInstance(long index) {
        if (instance == null) {
            //synchronized block to remove overhead.
            synchronized (SVMChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize.
                    instance = new SVMChannel(index);
                }
            }
        }
        return instance;
    }

    // Getter of the SVM channel.
    public ManagedChannel getChannel() {
        return channel;
    }

    
}
