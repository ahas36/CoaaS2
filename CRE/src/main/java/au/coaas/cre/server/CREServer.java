/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.cre.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

/**
 *
 * @author ali
 */
public class CREServer {
    private static Logger log = Logger.getLogger(CREServer.class.getName());

    static public void main (String [] args) throws IOException, InterruptedException
    {
        log.info("Starting");
        Server server = ServerBuilder.forPort(8583).maxInboundMessageSize(MAX_MESSAGE_SIZE).addService(new CREServiceImpl()).build();
        server.start();
        log.info("Server started on port 8583");
        server.awaitTermination();
    }
}
