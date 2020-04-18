package au.coaas.casm.api;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author ali
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext response) {
        if(response.getHeaders().get("Access-Control-Allow-Origin")==null)
        {
            response.getHeaders().add(
//            "Access-Control-Allow-Origin", "http://localhost:3000");
                    "Access-Control-Allow-Origin", "*");
            response.getHeaders().add(
                    "Access-Control-Allow-Credentials", "true");
            response.getHeaders().add(
                    "Access-Control-Allow-Headers",
                    "origin, content-type, accept, authorization");
            response.getHeaders().add(
                    "Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        }
    }

}
