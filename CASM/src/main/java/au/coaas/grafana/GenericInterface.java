/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.grafana;

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
@Path("generic")
public class GenericInterface {

    /**
     * Creates a new instance of QueryInterface
     */
    public GenericInterface() {
    }
    private static Logger log = Logger.getLogger(GenericInterface.class.getName());
    /**
     * Retrieves representation of an instance of
     * au.coaas.casm.api.QueryInterface
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
}
