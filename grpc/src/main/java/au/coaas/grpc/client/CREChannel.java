/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 *
 * @author ali
 */
public class CREChannel {

    private ManagedChannel channel;

    private static CREChannel instance;

    private CREChannel() {
        channel = ManagedChannelBuilder.forAddress("cre", 8583)
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

    public ManagedChannel getChannel() {
        return channel;
    }
    
    
}
