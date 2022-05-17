package Utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;
import javax.ws.rs.core.HttpHeaders;

public class HttpHandler {

    private static Logger log = Logger.getLogger(HttpHandler.class.getName());

    public static void makeContextQuery(String query, String authToken) {
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8070/CASM-2.0.1/api/query"))
                    .setHeader(HttpHeaders.CONTENT_TYPE, "plain/text")
                    .setHeader(HttpHeaders.AUTHORIZATION, authToken)
                    .POST(HttpRequest.BodyPublishers.ofString(query))
                    .build();

            client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        }
        catch(Exception ex){
            log.severe("Error in making context query requests: " + ex.getMessage());
        }
    }
}
