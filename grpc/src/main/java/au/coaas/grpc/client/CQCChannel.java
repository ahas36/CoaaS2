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
public class CQCChannel {

    private ManagedChannel channel;

    private static CQCChannel instance;

    private CQCChannel() {
        channel = ManagedChannelBuilder.forAddress("cqc", 8484)
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

    public ManagedChannel getChannel() {
        return channel;
    }
    
    
}
