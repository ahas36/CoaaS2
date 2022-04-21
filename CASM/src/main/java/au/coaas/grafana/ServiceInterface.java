/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.grafana;

import au.coaas.cqc.proto.CQCServiceGrpc;
import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqc.proto.ExecutionRequest;
import au.coaas.grpc.client.CQCChannel;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.Empty;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * REST Web Service
 *
 * @author ali
 */
@Path("service")
public class ServiceInterface {

    /**
     * Creates a new instance of QueryInterface
     */

    public ServiceInterface() {
    }

    private static Logger log = Logger.getLogger(ServiceInterface.class.getName());

    @POST
    @Path("register")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response registerContextService(String serviceDescription) {
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        CdqlResponse cdql = stub.registerContextService(ExecutionRequest.newBuilder().setCdql(serviceDescription).build());
        return Response.ok(cdql.getBody()).build();
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllContextServices() {
        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        SQEMResponse res = stub.getAllContextServices(Empty.newBuilder().build());
        if (res.getStatus().equals("200")) {
            return Response.ok(res.getBody()).build();
        } else {
            return Response.status(500).entity(res.getBody()).build();
        }
    }

}
