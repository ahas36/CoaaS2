package Handlers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("stats")
public class StatisticsInterface {
    public StatisticsInterface() {}

    private static Logger log = Logger.getLogger(StatisticsInterface.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllLogs() {
        try{
            String json = QueryStatHandler.getrecentQueryStats();
            return Response.ok(json).build();
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
            return Response.status(500).build();
        }
    }
}
