package au.coaas.grafana;

import au.coaas.utils.Utilities;

import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;

/**
 * @author shakthi
 */

@Provider
public class RequestFilter implements ContainerRequestFilter {
    private final Set<String> all_headers = Stream.of("x-originating-ip", "X-Originating-IP", "X-Forwarded-For",
                    "x-forwarded-for", "X-FORWARDED-FOR").collect(Collectors.toCollection(HashSet::new));

    @Override
    public void filter(ContainerRequestContext request) {
        // Setting a Context Query ID
        String queryId = UUID.randomUUID().toString();
        if(!request.getHeaders().containsKey("query-id")){
            request.getHeaders().add("query-id", queryId);
        }

        // Setting the origin location of the query if not exist.
        // But, this is the least precise way to get the location and hence has the lowest precedence.
        if(!request.getHeaders().containsKey("x-location")) {
            Set<String> req_headers = request.getHeaders().keySet();
            req_headers.retainAll(all_headers);
            if(req_headers.size()>0) {
                try {
                    String location = Utilities.convertIpToLocation((String) req_headers.toArray()[0]);
                    request.getHeaders().add("x-location", location);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
