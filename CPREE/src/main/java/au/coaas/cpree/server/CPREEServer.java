package au.coaas.cpree.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

import java.io.IOException;
import java.util.logging.Logger;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

public class CPREEServer {
    private static Logger log = Logger.getLogger(CPREEServer.class.getName());

    static public void main (String [] args) throws IOException, InterruptedException {

        log.info("Starting");
        Server server = ServerBuilder.forPort(9292)
                .addService(ServerInterceptors.intercept(new CPREEServiceImpl(), new CPREEInterceptor()))
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .build();
        server.start();
        log.info("CPREE Server started on port 9292");

        server.awaitTermination();
    }
}
