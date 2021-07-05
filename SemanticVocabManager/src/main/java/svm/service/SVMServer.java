/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package svm.service;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import svm.finder.SemanticVocabularyManger;

import java.io.IOException;
import java.util.logging.Logger;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

/**
 *
 * @author ali
 */
public class SVMServer {
    private static Logger log = Logger.getLogger(SVMServer.class.getName());

    static public void main (String [] args) throws IOException, InterruptedException
    {
        SemanticVocabularyManger.init();
        log.info("Starting");
        Server server = ServerBuilder.forPort(9191).addService(new SVMServiceImpl()).maxInboundMessageSize(MAX_MESSAGE_SIZE).build();
        server.start();
        log.info("Server started on port 9191");
        server.awaitTermination();
    }
}
