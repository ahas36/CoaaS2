package au.coaas.grafana;

import au.coaas.utils.Utilities;

import java.util.UUID;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;

/**
 * @author shakthi
 */

@Provider
public class RequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext request) {
        // Setting a Context Query ID
        String queryId = UUID.randomUUID().toString();
        if(!request.getHeaders().containsKey("query-id")){
            request.getHeaders().add("query-id", queryId);
        }

        // Setting the origin location of the query if not exist.
        if(!request.getHeaders().containsKey("x-location")) {
            if(request.getHeaders().containsKey("x-originating-ip") ||
                    request.getHeaders().containsKey("X-Originating-IP")) {
                try {
                    String location = Utilities.convertIpToLocation(
                            request.getHeaders().getFirst("X-Originating-IP"));
                    request.getHeaders().add("x-location", location);
                } catch(Exception ex) {

                }
            }
        }
    }
}
