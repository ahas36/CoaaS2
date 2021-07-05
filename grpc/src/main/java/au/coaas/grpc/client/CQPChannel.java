/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

/**
 *
 * @author ali
 */
public class CQPChannel {

    private ManagedChannel channel;

    private static CQPChannel instance;

    private CQPChannel() {
        channel = ManagedChannelBuilder.forAddress("cqp", 8585)
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .usePlaintext()
                .build();
    }

    public static CQPChannel getInstance() {
        if (instance == null) {
            //synchronized block to remove overhead 
            synchronized (CQPChannel.class) {
                if (instance == null) {
                    // if instance is null, initialize 
                    instance = new CQPChannel();
                }

            }
        }
        return instance;
    }

    public ManagedChannel getChannel() {
        return channel;
    }
    
    
}
