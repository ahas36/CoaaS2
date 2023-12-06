/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.cqc.server;

import au.coaas.cqc.executor.CDQLExecutor;
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
public class CQCServer {
    private static Logger log = Logger.getLogger(CQCServer.class.getName());

    static public void main (String [] args) throws IOException, InterruptedException
    {
        log.info("Starting");
        Server server = ServerBuilder.forPort(8484)
                .addService(ServerInterceptors.intercept(new CQCServiceImpl(), new CQCInterceptor()))
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .build();
        server.start();
        log.info("Server started on port 8484");

        // Register that the master is ready.
        CDQLExecutor.registerReadyMaster();

        server.awaitTermination();
    }
}
