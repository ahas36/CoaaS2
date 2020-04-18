package au.coaas.sqem.handler;

import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.RegisterEntityRequest;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.UpdateEntityRequest;
import au.coaas.sqem.util.CollectionDiscovery;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

public class ContextEntityHandler {

    public static SQEMResponse createEntity(RegisterEntityRequest registerRequest) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("mydb");

            String collectionName = CollectionDiscovery.discover(registerRequest.getEt());

            MongoCollection<Document> collection = db.getCollection(collectionName);

            Document myDoc = Document.parse(registerRequest.getJson());

            collection.insertOne(myDoc);

            return SQEMResponse.newBuilder().setStatus("200").setBody("1 entity created").build();
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }
    }

    public static SQEMResponse updateEntity(UpdateEntityRequest updateRequest) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("mydb");

            String collectionName = CollectionDiscovery.discover(updateRequest.getEt());

            MongoCollection<Document> collection = db.getCollection(collectionName);

            BasicDBObject updateFields = new BasicDBObject();

            JSONObject attributes = new JSONObject(updateRequest.getJson());

            for (String attributeName : attributes.keySet()) {
                Object item = attributes.get(attributeName);
                updateFields.append(attributeName, item.toString());
            }

            BasicDBObject query = new BasicDBObject();

            JSONObject key = new JSONObject(updateRequest.getKey());

            for (String keyName : key.keySet()) {
                query.put(keyName, key.get(keyName));
            }

            UpdateResult ur = collection.updateMany(query, new Document("$set", updateFields), new UpdateOptions().upsert(false));

            if (ur.getMatchedCount() == 0) {
                Document myDoc = Document.parse(updateRequest.getJson());
                collection.insertOne(myDoc);
                return SQEMResponse.newBuilder().setStatus("200").setBody("1 entity created").build();
            } else {
                return SQEMResponse.newBuilder().setStatus("200").setBody(ur.getMatchedCount() + " entity updated").build();
            }
        } catch (Exception e) {
            return SQEMResponse.newBuilder().setStatus("500").setBody(e.getMessage()).build();
        }

    }


}
