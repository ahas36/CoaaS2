/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.grafana;

import au.coaas.base.proto.ListOfString;
import au.coaas.grpc.client.SVMChannel;
import au.coaas.svm.proto.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

import static au.coaas.grafana.util.Utils.ListOfString2JsonArray;

/**
 * REST Web Service
 *
 * @author ali
 */
@Path("sv")
public class SemanticInterface {

    /**
     * Creates a new instance of QueryInterface
     */
    public SemanticInterface() {
    }
    private static Logger log = Logger.getLogger(SemanticInterface.class.getName());

    @POST
    @Path("register/{url}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response registerSemanticVocabulary(String vocab,@PathParam("url") String url) {
        SVMServiceGrpc.SVMServiceBlockingStub stub
                = SVMServiceGrpc.newBlockingStub(SVMChannel.getInstance().getChannel());
        SemanticVocabRegisterationResponse response =  stub.registerVocabulary(SemanticVocabClass.newBuilder().setOntologyClass(vocab).setUrl(url).build());
        return Response.ok(response.getBody()).build();
    }

    @GET
    @Path("terms/{url}/{class}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getTerms(@PathParam("url") String url,@PathParam("class") String ontClass) {
        SVMServiceGrpc.SVMServiceBlockingStub stub
                = SVMServiceGrpc.newBlockingStub(SVMChannel.getInstance().getChannel());
        Terms response =  stub.getTerms(SemanticVocabClass.newBuilder().setOntologyClass(ontClass).setUrl(url).build());
        return Response.ok(response.getBody()).build();
    }

    @GET
    @Path("parents/{url}/{class}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getParentClasses(@PathParam("url") String url,@PathParam("class") String ontClass) {
        SVMServiceGrpc.SVMServiceBlockingStub stub
                = SVMServiceGrpc.newBlockingStub(SVMChannel.getInstance().getChannel());
        ListOfString response = stub.getParentClasses(SemanticVocabClass.newBuilder().setOntologyClass(ontClass).setUrl(url).build());
        return Response.ok(ListOfString2JsonArray(response).toString()).build();
    }

    @GET
    @Path("classes/{url}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getParentClasses(@PathParam("url") String url) {
        SVMServiceGrpc.SVMServiceBlockingStub stub
                = SVMServiceGrpc.newBlockingStub(SVMChannel.getInstance().getChannel());
        ListOfString response =  stub.getClasses(SemanticVocabURL.newBuilder().setUrl(url).build());
        return Response.ok(ListOfString2JsonArray(response).toString()).build();
    }

    @GET
    @Path("graph/{url}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRawGraph(@PathParam("url") String url) {
        SVMServiceGrpc.SVMServiceBlockingStub stub
                = SVMServiceGrpc.newBlockingStub(SVMChannel.getInstance().getChannel());
        SemanticVocabResponse response = stub.getRawGraph(SemanticVocabURL.newBuilder().setUrl(url).build());
        if(response.getCode() == 200)
        {
            return Response.ok(response.getBody()).build();
        }else {
            return Response.status(500).entity(response.getBody()).build();
        }
    }

    @GET
    @Path("graphs")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getParentClasses() {
        SVMServiceGrpc.SVMServiceBlockingStub stub
                = SVMServiceGrpc.newBlockingStub(SVMChannel.getInstance().getChannel());
        ListOfString response =  stub.getGraphs(empty.newBuilder().build());
        return Response.ok(ListOfString2JsonArray(response).toString()).build();
    }

}
