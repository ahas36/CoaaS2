package au.coaas.grafana;

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
        String queryId = UUID.randomUUID().toString();
        if(!request.getHeaders().containsKey("query-id")){
            request.getHeaders().add("query-id", queryId);
        }
    }
}
