/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.grafana;

import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.Empty;
import au.coaas.sqem.proto.ProbDelay;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import org.json.JSONArray;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * REST Web Service
 *
 * @author shakthi & ali
 */
@Path("log")
public class LogInterface {

    /**
     * Creates a new instance of QueryInterface
     */
    public LogInterface() {
    }
    private static Logger log = Logger.getLogger(LogInterface.class.getName());

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllLogs() {
        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        SQEMResponse res = stub.getAllQueryLogs(Empty.newBuilder().build());
        if (res.getStatus().equals("200")) {
            return Response.ok(res.getBody()).build();
        } else {
            return Response.status(500).entity(res.getBody()).build();
        }
    }

    @GET
    @Path("hazards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentHazards(@QueryParam("lookback") @DefaultValue("60.0") double lookback) {
        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        // Lookback is in seconds.
        SQEMResponse res = stub.getLatestPushes(ProbDelay.newBuilder()
                .setValue(lookback).build());
        if (res.getStatus().equals("200")) {
            return Response.ok(new JSONArray(res.getBody())).build();
        } else {
            return Response.status(500).entity(res.getBody()).build();
        }
    }

    @GET
    @Path("performance")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerformanceData() {
        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        SQEMResponse res = stub.getPerformanceData(Empty.newBuilder().build());
        if (res.getStatus().equals("200")) {
            return Response.ok(res.getBody()).build();
        } else {
            return Response.status(500).entity(res.getBody()).build();
        }
    }

    @GET
    @Path("models")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModelState() {
        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        SQEMResponse res = stub.getModelState(Empty.newBuilder().build());
        if (res.getStatus().equals("200")) {
            return Response.ok(res.getBody()).build();
        } else {
            return Response.status(500).entity(res.getBody()).build();
        }
    }
}
