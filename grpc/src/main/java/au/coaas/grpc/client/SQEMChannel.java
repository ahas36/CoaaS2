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
public class SQEMChannel {

    private ManagedChannel channel;

    private static SQEMChannel instance;

    private SQEMChannel() {
        channel = ManagedChannelBuilder.forAddress("sqem", 8686)
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

    public ManagedChannel getChannel() {
        return channel;
    }
    
    
}
