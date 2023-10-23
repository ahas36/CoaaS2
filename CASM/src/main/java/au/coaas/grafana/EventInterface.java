package au.coaas.grafana;

import au.coaas.cqc.proto.*;
import au.coaas.grafana.util.Secured;

import au.coaas.grpc.client.CQCChannel;
import au.coaas.situations.KafkaMessenger;

import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("event")
public class EventInterface {

    /**
     * Creates a new instance of EventInterface
     */
    public EventInterface() {
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEvent(String event) {
        KafkaMessenger.sendMessage(event, "event");
        return Response.ok().build();
    }

    @Path("/monitor/subscription")
    @POST
    @Secured
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response monitorSubscription(String query, @QueryParam("page") @DefaultValue("-1") int page,
                                      @QueryParam("limit") @DefaultValue("-1") int limit,
                                      @Context HttpHeaders headers) {
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());

        String authToken = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        String queryId = headers.getHeaderString("query-id");
        String critical_level = headers.getHeaderString("critical-level");

        CdqlResponse cdql = stub.execute(ExecutionRequest.newBuilder()
                .setCdql(query).setPage(page).setLimit(limit)
                .setQueryid(queryId)
                .setToken(authToken)
                .setCriticality(critical_level).build());

        return Response.ok(cdql.getBody()).header("query-id", queryId).build();
    }

    @Path("/subscription/cancel/{id}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response cancelSubscription(@PathParam("id") String subID) {
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        RegisterState status =  stub.cancelSubscription(Subscription.newBuilder()
                .setId(subID).build());
        return Response.ok(status).build();
    }

    @Path("/monitor/stats")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getEventHandlingStats() {
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        EventStats stats =  stub.getEventStats(Empty.newBuilder().build());
        return Response.ok(stats).build();
    }

    @Path("/monitor/reset")
    @Produces(MediaType.TEXT_PLAIN)
    @GET
    public Response resetMonitoring() {
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        stub.resetEventStats(Empty.newBuilder().build());
        return Response.ok().build();
    }
}
