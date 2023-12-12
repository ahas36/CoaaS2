package au.coaas.sqem.handler;

import au.coaas.cqp.proto.ContextFunction;
import au.coaas.cqp.proto.SituationFunction;
import au.coaas.cqp.proto.SituationFunctionResponse;
import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.ContextServiceRequest;
import au.coaas.sqem.proto.RegisterContextServiceRequest;
import au.coaas.sqem.proto.RegisterSituationRequest;
import au.coaas.sqem.proto.SQEMResponse;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.protobuf.util.JsonFormat;

import java.util.logging.Logger;

/**
 * @author ali & shakthi
 */
public class SituationHandler {
    private static final Logger log = Logger.getLogger(SituationHandler.class.getName());

    public static SQEMResponse getAllSituationFunctions() {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_situation");

            MongoCollection<Document> collection = db.getCollection("situations");

            JSONArray finalResultJsonArr = new JSONArray();

            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    JSONObject resultJSON = new JSONObject(document.toJson());
                    finalResultJsonArr.put(resultJSON);
                }
            };

            collection.find(new BasicDBObject()).forEach(printBlock);
            return SQEMResponse.newBuilder().setStatus("200").setBody(finalResultJsonArr.toString()).build();

        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    public static SQEMResponse register(RegisterSituationRequest request) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_situation");

            MongoCollection<Document> collection = db.getCollection("situations");

            JSONObject sla = new JSONObject(ContextConsumerHandler.getConsumerSLA(request.getToken(), true));
            String consumerId = sla.getJSONObject("_id").getString("$oid");

            Document doc = new Document("title", request.getTitle())
                    .append("consumerId", consumerId)
                    .append("raw", request.getRaw())
                    .append("parsed", JsonFormat.printer().print(request.getSFunction()));

            collection.insertOne(doc);

            JSONObject body = new JSONObject();
            body.put("message","a new situation with title : " + request.getTitle() + "has been registered!");
            return SQEMResponse.newBuilder().setStatus("200").setBody(body.toString()).build();
        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    public static SituationFunctionResponse findSituationByTitle(String title) {
        SituationFunctionResponse.Builder response = SituationFunctionResponse.newBuilder();
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();

            MongoDatabase db = mongoClient.getDatabase("coaas_situation");
            MongoCollection<Document> collection = db.getCollection("situations");

            Document res = collection.find(new Document("title", title)).first();

            if(res != null){
                SituationFunction.Builder builder = SituationFunction.newBuilder();
                JsonFormat.parser().ignoringUnknownFields().merge(res.getString("parsed"), builder);
                response.setStatus("200");
                response.setSFunction(builder.build());
                response.setRegConsumerId(res.getString("consumerId"));
            }
            else response.setStatus("404");
        } catch (Exception e) {
            log.severe(e.getMessage());
            response.setStatus("500");
        }
        return response.build();
    }

}
