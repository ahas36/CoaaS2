/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.grafana;

import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.Empty;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * REST Web Service
 *
 * @author ali
 */
@Path("subscription")
public class SubscriptionInterface {

    /**
     * Creates a new instance of QueryInterface
     */
    public SubscriptionInterface() {
    }
    private static Logger log = Logger.getLogger(SubscriptionInterface.class.getName());

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSubscriptions() {
        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        SQEMResponse res = stub.getAllSubscriptions(Empty.newBuilder().build());
        if (res.getStatus().equals("200")) {
            return Response.ok(res.getBody()).build();
        } else {
            return Response.status(500).entity(res.getBody()).build();
        }
    }
}
