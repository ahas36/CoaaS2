package au.coass.rwc.server;

import au.coass.rwc.executor.SubscriptionHandler;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

public class RWCServer {
    private static Logger log = Logger.getLogger(RWCServer.class.getName());

    static public void main (String [] args) throws IOException, InterruptedException {

        log.info("Starting Remote Controller.");
        Server server = ServerBuilder.forPort(6868)
                .addService(new RWCServiceImpl())
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .build();
        server.start();
        log.info("Remote Worker Controller started on port 6868.");

        // Send Ready status to Master.
        SubscriptionHandler.registerDevice();

        server.awaitTermination();
    }
}
