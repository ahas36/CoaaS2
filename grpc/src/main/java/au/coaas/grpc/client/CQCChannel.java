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
public class CQCChannel {

    private ManagedChannel channel;

    private static CQCChannel instance;

    // Master Node and Worker (Internal) Channel Resolution.
    private CQCChannel() {
        channel = ManagedChannelBuilder.forAddress("0.0.0.0", 8484)
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static CQCChannel getInstance() {
        if (instance == null) {
            //synchronized block to remove overhead 
            synchronized (CQCChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize 
                    instance = new CQCChannel();
                }
            }
        }
        return instance;
    }

    // Location-based Overload for Master-Worker Node Channel Resolution.
    private CQCChannel(long index) {
        AbstractMap.SimpleEntry<String, Integer> ipAddress = IPResolver.convertIpToLocation(index, Services.CQC);
        channel = ManagedChannelBuilder.forAddress(ipAddress.getKey(), ipAddress.getValue())
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static CQCChannel getInstance(long index) {
        if (instance == null) {
            //synchronized block to remove overhead
            synchronized (CQCChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize
                    instance = new CQCChannel(index);
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
