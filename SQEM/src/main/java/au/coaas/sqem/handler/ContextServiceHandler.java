package au.coaas.sqem.handler;

import au.coaas.sqem.util.GeoIndexer;
import au.coaas.sqem.proto.EdgeDevice;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.ContextServiceRequest;
import au.coaas.sqem.proto.RegisterContextServiceRequest;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;
import org.bson.types.ObjectId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ContextServiceHandler {
    private static final Logger log = Logger.getLogger(ContextServiceHandler.class.getName());

    public static SQEMResponse register(RegisterContextServiceRequest registerRequest) {
        try {
            String ipAddress = "0.0.0.0"; // Which means the master should handle.
            Long cpIndex = registerRequest.getIndex();

            if(cpIndex > 0) {
                // If the index is zero (not assigned), there is either a problem with the coordinates,
                // or the location can not be found. So, will be attached to the Master node until the
                // location can be resolved.
                AbstractMap.SimpleEntry res_index = resolveAttachedEdgeIndex(cpIndex);
                if(res_index != null) {
                    cpIndex = (Long) res_index.getKey();
                    ipAddress = (String) res_index.getValue();
                }
            }

            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");

            MongoCollection<Document> collection = db.getCollection("contextService");

            Document myDoc = Document.parse(registerRequest.getJson());
            myDoc.put("info.index", cpIndex);
            myDoc.put("status","active");

            collection.insertOne(myDoc);
            DistributionManager.insertCPSubscription(myDoc.get("_id").toString(), cpIndex);

            JSONObject body = new JSONObject();
            body.put("index", cpIndex);
            body.put("sub_edge", ipAddress);
            body.put("id",myDoc.get("_id").toString());
            body.put("message","A Context Service has been registered.");

            return SQEMResponse.newBuilder().setStatus("200").setBody(body.toString()).build();
        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    private static AbstractMap.SimpleEntry resolveAttachedEdgeIndex (long cpIndex) {
        List<EdgeDevice> edges = DistributionManager.getEdgeDeviceIndexes();
        GeoIndexer indexer = GeoIndexer.getInstance();
        List<EdgeDevice> parentIndexes = edges.parallelStream()
                .filter(ed -> indexer.isParent(cpIndex, ed.getIndex()))
                .collect(Collectors.toList());

        if(parentIndexes.size() > 0) return new AbstractMap.SimpleEntry(
                parentIndexes.get(0).getIndex(), parentIndexes.get(0).getIpAddress());

        List<EdgeDevice> distances = edges.parallelStream()
                .map(ed -> ed.toBuilder().setDistance(indexer.distance(cpIndex, ed.getIndex())).build())
                .collect(Collectors.toList());
        distances.sort(Comparator.comparing(EdgeDevice::getDistance));

        // Load balancing if there are multiple by the same distance or in the same index.

        return new AbstractMap.SimpleEntry(
                distances.get(0).getIndex(), distances.get(0).getIpAddress());
    }

    public static SQEMResponse changeStatus(String id,String status) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");

            MongoCollection<Document> collection = db.getCollection("contextService");

            BasicDBObject query = new BasicDBObject();

            query.put("_id", id);

            BasicDBObject updateFields = new BasicDBObject();

            updateFields.put("status",status);

            UpdateResult ur = collection.updateMany(query, new Document("$set", updateFields), new UpdateOptions().upsert(false));

            JSONObject body = new JSONObject();
            body.put("message","1 Service updated");
            body.put("id",id);
            return SQEMResponse.newBuilder().setStatus("200").setBody(body.toString()).build();
        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    public static SQEMResponse discoverMatchingServices(ContextServiceRequest serviceRequest) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");

            MongoCollection<Document> collection = db.getCollection("contextService");

            String vocabURI = "<" + serviceRequest.getEt().getVocabURI().trim();

            if(!vocabURI.endsWith("/"))
            {
                vocabURI = vocabURI + "/";
            }

            vocabURI = vocabURI + serviceRequest.getEt().getType() + ">";

            JSONArray finalResultJsonArr = new JSONArray();

            Block<Document> printBlock = document -> {
                JSONObject resultJSON = new JSONObject(document.toJson());
                finalResultJsonArr.put(resultJSON);
            };

            // Get all the context services that conforms to the given ontology, and contains all the attributes needed in the query.
            // Service Description is within this json documents. Not like the prototype.
            collection.find(Filters.and(
                    Filters.eq("status","active"),
                    Filters.eq("info.ontClass",vocabURI),
                    Filters.all("info.params.term.label",serviceRequest.getParamsList())
                    )).forEach(printBlock);

            return SQEMResponse.newBuilder().setStatus("200").setBody(finalResultJsonArr.toString()).build();

        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    public static SQEMResponse getAllServices() {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");

            MongoCollection<Document> collection = db.getCollection("contextService");

            JSONArray finalResultJsonArr = new JSONArray();

            Block<Document> printBlock = document -> {
                JSONObject resultJSON = new JSONObject(document.toJson());
                finalResultJsonArr.put(resultJSON);
            };

            BasicDBObject filter = new BasicDBObject();
            filter.put("status","active");

            collection.find(filter).forEach(printBlock);
            return SQEMResponse.newBuilder().setStatus("200").setBody(finalResultJsonArr.toString()).build();

        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    public static SQEMResponse getContextServiceInfo(String contextProviderId) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");

            MongoCollection<Document> collection = db.getCollection("contextService");
            BasicDBObject query = new BasicDBObject() {{
                put("_id", new ObjectId(contextProviderId));
                put("status","active");
            }};

            Document cpInfo = collection.find(query).first();
            return SQEMResponse.newBuilder().setStatus("200").setBody(cpInfo.toJson()).build();

        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }
}
