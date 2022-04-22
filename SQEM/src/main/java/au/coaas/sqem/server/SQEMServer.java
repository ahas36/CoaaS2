/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.sqem.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

import java.io.IOException;
import java.util.logging.Logger;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

/**
 *
 * @author ali
 */
public class SQEMServer {
    private static Logger log = Logger.getLogger(SQEMServer.class.getName());

    static public void main (String [] args) throws IOException, InterruptedException
    {
        log.info("Starting");
        Server server = ServerBuilder.forPort(8686)
                .addService(ServerInterceptors.intercept(new SQEMServiceImpl(), new SQEMInterceptor()))
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .build();
        server.start();
        log.info("Server started on port 8686");

        server.awaitTermination();
    }
}
