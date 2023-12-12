package au.coaas.grafana;

import au.coaas.grafana.util.Secured;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;
import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;

import java.io.IOException;

import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.AuthRequest;
import au.coaas.sqem.proto.AuthToken;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.json.JSONObject;

/**
 * @author shakthi
 */

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String REALM = "coaas";
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private static final String SECRET = "J669sbQPoqJXNV7yBtYMJF5jHdIwjEJRoKtUhTr5dxg2YAmjYsALhrPOcLuJmE7f";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the Authorization header from the request
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader
                .substring(AUTHENTICATION_SCHEME.length()).trim();

        try {
            // Validate the token
            validateToken(token);

        } catch (Exception e) {
            abortWithUnauthorized(requestContext);
        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        // Abort the filter chain with a 401 status code response
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .build());
    }

    private void validateToken(String token) throws JWTVerificationException, ForbiddenException {
        // Check if the token was issued by the server
        // Throw an JWTVerificationException if the token is invalid
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();

        DecodedJWT jwt = verifier.verify(token);
        JSONObject payload = new JSONObject(jwt.getPayload());

        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        SQEMResponse response =  stub.validateToken(AuthToken.newBuilder()
                .setUsername(payload.getString("username")).build());

        if(response.getStatus() != "200"){
            throw new ForbiddenException();
        }
    }
}
