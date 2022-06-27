package au.coaas.grafana;

import au.coaas.grafana.exceptions.ForbiddenConsumer;
import au.coaas.grafana.util.Credentials;
import au.coaas.grafana.util.Encryptor;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.AuthRequest;
import au.coaas.sqem.proto.AuthToken;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Map;
import java.util.logging.Logger;

/**
 * REST Web Service
 *
 * @author shakthi
 */

@Path("auth")
public class AuthenticationInterface {

    private static Logger log = Logger.getLogger(AuthenticationInterface.class.getName());

    private static final String SECRET = "J669sbQPoqJXNV7yBtYMJF5jHdIwjEJRoKtUhTr5dxg2YAmjYsALhrPOcLuJmE7f";

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser(Credentials credentials) {
        try {

            String username = credentials.getUserName();
            String password = Encryptor.encrypt(credentials.getPassword());

            // Authenticate the user using the credentials provided
            String res = authenticate(username, password);

            // Issue a token for the user
            String token = issueToken(res);

            // Return the token on the response
            log.info("Successfully authenticated: " + username);
            return Response.ok(new JSONObject().put("token",token)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    private String authenticate(String username, String password) throws ForbiddenConsumer {
        // Checking the registry status of the context consumer in DB
        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        SQEMResponse consumer =  stub.authenticateConsumer(AuthRequest.newBuilder()
                .setUsername(username)
                .setPassword(password).build());

        if(consumer.getStatus() != "200")
            throw new ForbiddenConsumer();

        return consumer.getBody();
    }

    private String issueToken(String consumer) throws JWTCreationException{
        JSONObject conObj = new JSONObject(consumer);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        Map<String, Object> payload = conObj.getJSONObject("info").toMap();
        payload.put("scope", conObj.getJSONObject("auth").getJSONArray("scope"));

        String token = JWT.create()
                .withIssuer("auth0")
                .withPayload(payload)
                .sign(algorithm);

        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        stub.saveOrUpdateToken(AuthToken.newBuilder()
                .setUsername(conObj.getJSONObject("info").getString("username"))
                .setToken(token).build());

        return token;
    }
}
