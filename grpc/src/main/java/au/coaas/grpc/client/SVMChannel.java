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
public class SVMChannel {

    private ManagedChannel channel;

    private static SVMChannel instance;

    private SVMChannel() {
        channel = ManagedChannelBuilder.forAddress("svm", 9191)
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

    public ManagedChannel getChannel() {
        return channel;
    }

    
}
