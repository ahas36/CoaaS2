package au.coaas.sqem.handler;

import au.coaas.cqc.proto.CordinatesIndex;
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

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ContextServiceHandler {
    private static final Logger log = Logger.getLogger(ContextServiceHandler.class.getName());

    public static SQEMResponse register(RegisterContextServiceRequest registerRequest) {
        try {
            long sub_edge_id = 0;
            String ipAddress = "0.0.0.0"; // Which means the master should handle.
            Long cpIndex = registerRequest.getIndex(); // Still if this is 0, means the location need to be resolved from the response.

            if(cpIndex > 0) {
                // If the index is zero (not assigned), there is either a problem with the coordinates,
                // or the location can not be found. So, will be attached to the Master node until the
                // location can be resolved.
                EdgeDevice res_index = resolveAttachedEdgeIndex(cpIndex);
                if(res_index != null) {
                    sub_edge_id = res_index.getId();
                    ipAddress = res_index.getIpAddress();
                }
                // If no edge node can be resolved then attached to the master node.
            }

            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");

            MongoCollection<Document> collection = db.getCollection("contextService");

            Document myDoc = Document.parse(registerRequest.getJson());
            myDoc.put("info.index", cpIndex);
            myDoc.put("status","active");

            collection.insertOne(myDoc);
            DistributionManager.insertCPSubscription(myDoc.get("_id").toString(), cpIndex, sub_edge_id);

            JSONObject body = new JSONObject();
            body.put("index", cpIndex);
            body.put("id",myDoc.get("_id").toString());

            JSONObject sub_node = new JSONObject();
            sub_node.put("id", sub_edge_id);
            sub_node.put("ipAddress", ipAddress);
            body.put("subEdge", sub_node);

            body.put("message","A Context Service has been registered.");

            return SQEMResponse.newBuilder().setStatus("200").setBody(body.toString()).build();
        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    protected static EdgeDevice resolveAttachedEdgeIndex (long cpIndex) {
        List<EdgeDevice> edges = DistributionManager.getEdgeDeviceIndexes();
        if(edges.size() > 0) {
            GeoIndexer indexer = GeoIndexer.getInstance();
            List<EdgeDevice> parentIndexes = edges.parallelStream()
                    .filter(ed -> indexer.isParent(cpIndex, ed.getIndex()))
                    .collect(Collectors.toList());

            if(parentIndexes.size() > 0) return parentIndexes.get(0);

            List<EdgeDevice> distances = edges.parallelStream()
                    .map(ed -> ed.toBuilder().setDistance(indexer.distance(cpIndex, ed.getIndex())).build())
                    .collect(Collectors.toList());
            distances.sort(Comparator.comparing(EdgeDevice::getDistance));

            // Load balancing if there are multiple nodes by the same distance or in the same index.
            long min_dist = distances.get(0).getDistance();
            Stream<EdgeDevice> similarNodes = distances.stream().filter(c -> c.getDistance() == min_dist);
            EdgeDevice selectedNode;
            if(similarNodes.count() > 1)
                selectedNode = loadBalance(similarNodes.collect(Collectors.toList()));
            else selectedNode = distances.get(0);

            return selectedNode;
        }

        return null;
    }

    private static EdgeDevice loadBalance(List<EdgeDevice> similarNodes) {
        // For this POC, we are using the Least Connections-based Load Balancing considering the edge nodes are
        // equal and the context providers perform equally.
        // TODO:
        // But the best approach is to use "Resource-based" taking into consideration the following parameters:
        // 1. Distance to the edge node/s
        // 2. Current number of subscriptions that the candidate node has (and expected longidivity to have the connection)
        // 3. System specs (available CPU, Memory, Storage)
        // 4. Direction to the node (whether moving away or towards)
        // 5. Predicted load.
        HashMap<Long, Integer> loadDistribution = DistributionManager
                .currentLoad(similarNodes
                        .stream().map(c -> c.getId())
                        .collect(Collectors.toList()));
        Long sel_node = Collections.min(loadDistribution.entrySet(),
                Map.Entry.comparingByValue()).getKey();

        return similarNodes.stream().filter(nd -> nd.getId() == sel_node)
                .findFirst().get();
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

            collection.updateMany(query, new Document("$set", updateFields), new UpdateOptions().upsert(false));

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

    public static String changeRegisteredLocation(String id, Long cpIndex) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");
            MongoCollection<Document> collection = db.getCollection("contextService");

            // Find Conditions
            BasicDBObject query = new BasicDBObject();
            query.put("_id", id);
            // Updates
            BasicDBObject updateFields = new BasicDBObject();
            updateFields.put("info.index", cpIndex);

            collection.updateMany(query, new Document("$set", updateFields), new UpdateOptions().upsert(false));

            EdgeDevice res_index = resolveAttachedEdgeIndex(cpIndex);
            DistributionManager.updateCPSubscription(id, cpIndex, res_index.getId());

            return res_index.getIpAddress();
        } catch (Exception e) {
            log.severe(e.getMessage());
            return null;
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
