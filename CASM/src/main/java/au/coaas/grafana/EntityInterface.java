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
import au.coaas.sqem.proto.Empty;
import au.coaas.sqem.proto.EntityTypeRequest;
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
@Path("entity")
public class EntityInterface {

    /**
     * Creates a new instance of QueryInterface
     */
    public EntityInterface() {
    }

    private static Logger log = Logger.getLogger(EntityInterface.class.getName());


    @POST
    @Path("create")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createEntity(String entity) {
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        CdqlResponse cdql = stub.registerContextEntity(ExecutionRequest.newBuilder().setCdql(entity).build());
        return Response.ok(cdql.getBody()).build();
    }

    @POST
    @Path("update")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateEntity(String entity) {
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        CdqlResponse cdql = stub.updateContextEntity(ExecutionRequest.newBuilder().setCdql(entity).build());
        return Response.ok(cdql.getBody()).build();
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEntityTypes() {
        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        SQEMResponse res = stub.getAllEntityTypes(Empty.newBuilder().build());
        if (res.getStatus().equals("200")) {
            return Response.ok(res.getBody()).build();
        } else {
            return Response.status(500).entity(res.getBody()).build();
        }

    }

    @PUT
    @Path("clear/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearEntityType(@PathParam("name") String name) {

        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        SQEMResponse res = stub.clearEntityType(EntityTypeRequest.newBuilder().setName(name).build());

        if (res.getStatus().equals("200")) {
            return Response.ok(res.getBody()).build();
        } else {
            return Response.status(500).entity(res.getBody()).build();
        }
    }

    @DELETE
    @Path("remove/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeEntityType(@PathParam("name") String name) {

        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        SQEMResponse res = stub.removeEntityType(EntityTypeRequest.newBuilder().setName(name).build());

        if (res.getStatus().equals("200")) {
            return Response.ok(res.getBody()).build();
        } else {
            return Response.status(500).entity(res.getBody()).build();
        }
    }

}
