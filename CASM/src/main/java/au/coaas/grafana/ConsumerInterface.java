package au.coaas.grafana;

import au.coaas.cqc.proto.CQCServiceGrpc;
import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqc.proto.ExecutionRequest;
import au.coaas.grafana.util.Encryptor;
import au.coaas.grpc.client.CQCChannel;
import org.json.JSONObject;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * REST Web Service
 *
 * @author shakthi
 */

@Path("consumers")
public class ConsumerInterface {

    private static Logger log = Logger.getLogger(ServiceInterface.class.getName());

    @POST
    @Path("register")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response registerContextConsumer(String consumerDescription) {
        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());

        // Encrypting the password
        JSONObject request = new JSONObject(consumerDescription);
        JSONObject infoObj = request.getJSONObject("info");
        String psw = infoObj.getString("password");
        infoObj.put("password", Encryptor.encrypt(psw));
        request.put("info", infoObj);

        CdqlResponse consumer = stub.registerContextConsumer(ExecutionRequest.newBuilder().setCdql(request.toString()).build());

        switch(consumer.getStatus()){
            case "200":
                return Response.ok(consumer.getBody()).build();
            case "404":
                return Response.status(404).entity(consumer.getBody()).build();
            default:
                return Response.status(500).entity(consumer.getBody()).build();
        }
    }
}
