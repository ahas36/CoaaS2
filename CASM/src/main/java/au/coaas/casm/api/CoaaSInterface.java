/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.casm.api;

import au.coaas.cqc.proto.CQCServiceGrpc;
import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqc.proto.ExecutionRequest;
import au.coaas.cqp.proto.CDQLQuery;
import au.coaas.cqp.proto.CDQLRepeat;
import au.coaas.cqp.proto.CQPServiceGrpc;
import au.coaas.cqp.proto.ParseRequest;
import au.coaas.svm.proto.*;
import com.google.protobuf.Empty;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Logger;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author ali
 */
@Path("generic")
public class CoaaSInterface {

    /**
     * Creates a new instance of CoaaSInterface
     */
    public CoaaSInterface() {
    }
    private static Logger log = Logger.getLogger(CoaaSInterface.class.getName());
    /**
     * Retrieves representation of an instance of
     * au.coaas.casm.api.CoaaSInterface
     *
     * @return an instance of java.lang.String
     */

    @GET
    @Path("health")
    @Produces(MediaType.APPLICATION_JSON)
    public Response parseQuery() {
        JSONObject jo = new JSONObject();
        jo.put("version","1.9.2");
        jo.put("status","Healthy");
        return Response.ok(jo.toString()).build();
    }


    @POST
    @Path("service/register")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response registerContextService(String serviceDescription) {
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        CdqlResponse cdql =  stub.registerContextService(ExecutionRequest.newBuilder().setCdql(serviceDescription).build());
        return Response.ok(cdql.getBody()).build();
    }


    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response parseQuery(String query) {
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        CdqlResponse cdql =  stub.execute(ExecutionRequest.newBuilder().setCdql(query).build());
        return Response.ok(cdql.getBody()).build();
    }

    @POST
    @Path("entity/create")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createEntity(String entity) {
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        CdqlResponse cdql =  stub.registerContextEntity(ExecutionRequest.newBuilder().setCdql(entity).build());
        return Response.ok(cdql.getBody()).build();
    }

    @POST
    @Path("entity/update")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateEntity(String entity) {
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        CdqlResponse cdql =  stub.updateContextEntity(ExecutionRequest.newBuilder().setCdql(entity).build());
        return Response.ok(cdql.getBody()).build();
    }

    @POST
    @Path("sv/register/{url}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response registerSemanticVocabulary(String vocab,@PathParam("url") String url) {
        SVMServiceGrpc.SVMServiceBlockingStub stub
                = SVMServiceGrpc.newBlockingStub(SVMChannel.getInstance().getChannel());
        SemanticVocabRegisterationResponse response =  stub.registerVocabulary(SemanticVocabClass.newBuilder().setOntologyClass(vocab).setUrl(url).build());
        return Response.ok(response.getBody()).build();
    }

    @GET
    @Path("sv/terms/{url}/{class}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getTerms(@PathParam("url") String url,@PathParam("class") String ontClass) {
        SVMServiceGrpc.SVMServiceBlockingStub stub
                = SVMServiceGrpc.newBlockingStub(SVMChannel.getInstance().getChannel());
        Terms response =  stub.getTerms(SemanticVocabClass.newBuilder().setOntologyClass(ontClass).setUrl(url).build());
        return Response.ok(response.getBody()).build();
    }

    @GET
    @Path("sv/parents/{url}/{class}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getParentClasses(@PathParam("url") String url,@PathParam("class") String ontClass) {
        SVMServiceGrpc.SVMServiceBlockingStub stub
                = SVMServiceGrpc.newBlockingStub(SVMChannel.getInstance().getChannel());
        ListOfString response =  stub.getParentClasses(SemanticVocabClass.newBuilder().setOntologyClass(ontClass).setUrl(url).build());
        return Response.ok(this.ListOfString2JsonArray(response).toString()).build();
    }

    @GET
    @Path("sv/classes/{url}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getParentClasses(@PathParam("url") String url) {
        SVMServiceGrpc.SVMServiceBlockingStub stub
                = SVMServiceGrpc.newBlockingStub(SVMChannel.getInstance().getChannel());
        ListOfString response =  stub.getClasses(SemanticVocabURL.newBuilder().setUrl(url).build());
        return Response.ok(this.ListOfString2JsonArray(response).toString()).build();
    }

    @GET
    @Path("sv/graphs")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getParentClasses() {
        SVMServiceGrpc.SVMServiceBlockingStub stub
                = SVMServiceGrpc.newBlockingStub(SVMChannel.getInstance().getChannel());
        ListOfString response =  stub.getGraphs(empty.newBuilder().build());
        return Response.ok(this.ListOfString2JsonArray(response).toString()).build();
    }

    private static JSONArray ListOfString2JsonArray(ListOfString los)
    {
        JSONArray result = new JSONArray();
        for(int i = 0; i < los.getValueCount();i++)
        {
            result.put(los.getValue(i));
        }
        return result;
    }

}
