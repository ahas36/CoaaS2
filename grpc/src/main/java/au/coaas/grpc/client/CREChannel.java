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
public class CREChannel {

    private ManagedChannel channel;

    private static CREChannel instance;

    // Master Node and Worker (Internal) Channel Resolution.
    private CREChannel() {
        channel = ManagedChannelBuilder.forAddress("0.0.0.0", 8583)
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static CREChannel getInstance() {
        if (instance == null) {
            //synchronized block to remove overhead 
            synchronized (CREChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize 
                    instance = new CREChannel();
                }
            }
        }
        return instance;
    }

    // Location-based Overload for Master-Worker Node Channel Resolution.
    private CREChannel(long index) {
        AbstractMap.SimpleEntry<String, Integer> ipAddress = IPResolver.convertIpToLocation(index, Services.CRE);
        channel = ManagedChannelBuilder.forAddress(ipAddress.getKey(), ipAddress.getValue())
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static CREChannel getInstance(long index) {
        if (instance == null) {
            //synchronized block to remove overhead
            synchronized (CREChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize
                    instance = new CREChannel(index);
                }
            }
        }
        return instance;
    }

    // Getter of the CRE channel.
    public ManagedChannel getChannel() {
        return channel;
    }
    
    
}
