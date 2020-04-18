package au.coaas.cqc.executor;

import au.coaas.cqc.grpc.client.SQEMChannel;
import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqc.proto.ExecutionRequest;
import au.coaas.cqc.server.CQCServiceImpl;
import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.sqem.proto.RegisterEntityRequest;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.UpdateEntityRequest;
import org.json.JSONObject;

import java.util.logging.Logger;

public class ContextEntityManager {

    private static Logger log = Logger.getLogger(ContextEntityManager.class.getName());

    public static CdqlResponse registerContextEntity(ExecutionRequest request)
    {
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        JSONObject data = new JSONObject(request.getCdql());
        JSONObject entityType = data.getJSONObject("EntityType");
        JSONObject entity = data.getJSONObject("Attributes");
        SQEMResponse sqemResponse = sqemStub.registerContextEntity(RegisterEntityRequest.newBuilder().setJson(entity.toString())
                                                                    .setEt(ContextEntityType.newBuilder().setVocabURI(entityType.getString("namespace")).setType(entityType.getString("type")).build())
                                                                    .build());
        return CdqlResponse.newBuilder().setBody(sqemResponse.getBody()).setStatus(sqemResponse.getStatus()).build();
    }

    public static CdqlResponse updateContextEntity(ExecutionRequest request)
    {
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        JSONObject data = new JSONObject(request.getCdql());
        JSONObject entityType = data.getJSONObject("EntityType");
        JSONObject entity = data.getJSONObject("Attributes");
        JSONObject keys = data.getJSONObject("key");
        SQEMResponse sqemResponse = sqemStub.updateContextEntity(UpdateEntityRequest.newBuilder().setJson(entity.toString())
                .setEt(ContextEntityType.newBuilder().setVocabURI(entityType.getString("namespace")).setType(entityType.getString("type")).build())
                .setKey(keys.toString())
                .build());
        return CdqlResponse.newBuilder().setBody(sqemResponse.getBody()).setStatus(sqemResponse.getStatus()).build();
    }

    public static void main (String [] args)
    {
        CdqlResponse res = registerContextEntity( ExecutionRequest.newBuilder().setCdql("{\n" +
                "\t\"EntityType\":\n" +
                "\t{\n" +
                "\t\t\"namespace\" : \"https://cdql.coaas.csiro.au\",\n" +
                "\t\t\"type\" : \"entity1\"\n" +
                "\t},\n" +
                "\t\"Attributes\":\n" +
                "\t{\n" +
                "\t\t\"id\": 1,\n" +
                "\t\t\"name\" : ali,\n" +
                "\t\t\"temp\" : 24\n" +
                "\t}\n" +
                "}").build());

        System.out.print(res.toString());
    }
}
