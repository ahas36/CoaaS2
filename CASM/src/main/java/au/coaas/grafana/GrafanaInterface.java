/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.grafana;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * REST Web Service
 *
 * @author ali
 */
@Path("grafana")
public class GrafanaInterface {

    /**
     * Creates a new instance of QueryInterface
     */
    public GrafanaInterface() {
    }
    private static Logger log = Logger.getLogger(GrafanaInterface.class.getName());


    @GET
    public Response test() {
        return Response.ok().build();
    }

    @POST
    @Path("search")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(String search) {
        JSONArray result = new JSONArray();

//        SQEMServiceGrpc.SQEMServiceBlockingStub stub
//                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
//
//        SQEMResponse res = stub.getAllEntityTypes(Empty.newBuilder().build());

        JSONObject sample = new JSONObject();

        result.put("place");
        result.put("traffic");

        return Response.ok(result.toString()).build();
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search() {
        JSONArray result = new JSONArray();

        JSONObject sample = new JSONObject();

        result.put("place");
        result.put("traffic");

        return Response.ok(result.toString()).build();
    }

    @POST
    @Path("query")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response query(String query) {
        return Response.ok().build();
    }

    @POST
    @Path("annotations")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response getAnnotations(String annotations) {
        return Response.ok().build();
    }

    @POST
    @Path("tag-keys")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response tagKeys(String annotations) {
        return Response.ok().build();
    }

    @POST
    @Path("tag-values")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response tagValues(String annotations) {
        return Response.ok().build();
    }

}
