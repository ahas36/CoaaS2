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

import java.util.logging.Logger;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author ali
 */
@Path("query")
public class QueryInterface {

    /**
     * Creates a new instance of QueryInterface
     */
    public QueryInterface() {
    }
    private static Logger log = Logger.getLogger(QueryInterface.class.getName());


    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response parseQuery(String query,@QueryParam("page") @DefaultValue("0") int page) {
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        CdqlResponse cdql =  stub.execute(ExecutionRequest.newBuilder().setCdql(query).setPage(page).build());
        return Response.ok(cdql.getBody()).build();
    }

}
