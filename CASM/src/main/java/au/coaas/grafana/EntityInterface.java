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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * REST Web Service
 *
 * @author ali & shakthi
 */
@Path("entity")
public class EntityInterface {

    /**
     * Creates a new instance of EntityInterface
     */
    public EntityInterface() {
    }

    private static Logger log = Logger.getLogger(EntityInterface.class.getName());

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createEntity(@Context HttpHeaders headers, String entity) {
        // It is assumed that the context provider will use its unique signature to indicate from where the data is sent from.
        // Given the context service will use the returned id in the header.
        String providerId = headers.getHeaderString("provider");
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        // Value set in QueryId is the Context Provider ID.
        CdqlResponse cdql = stub.registerContextEntity(ExecutionRequest.newBuilder()
                .setCdql(entity).setQueryid(providerId).build());
        return Response.ok(cdql.getBody()).build();
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateEntity(@Context HttpHeaders headers, String entity) {
        // It is assumed that the context provider will use its unique signature to indicate from where the data is sent from.
        String providerId = headers.getHeaderString("provider");
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        // Value set in QueryId is the Context Provider ID.
        CdqlResponse cdql = stub.updateContextEntity(ExecutionRequest.newBuilder()
                .setCdql(entity).setQueryid(providerId).build());
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
