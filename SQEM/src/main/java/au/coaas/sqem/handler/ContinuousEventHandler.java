package au.coaas.sqem.handler;

import au.coaas.sqem.proto.Situation;
import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.CdqlSubscription;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import com.google.common.base.Joiner;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;
import java.util.logging.Logger;

public class ContinuousEventHandler {
    private static final Logger log = Logger.getLogger(ContinuousEventHandler.class.getName());

    public static CdqlSubscription findRelatedSubscriptions(Situation event){
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_subscription");

            MongoCollection<Document> collection = db.getCollection("subscriptions");

            JSONArray finalResultJsonArr = new JSONArray();

            Set<String> keySet = event.getContextEntity().getContextAttributesMap().keySet();
            String attributes = "[\"" + Joiner.on("\",\"").join(keySet) + "\",\"*\"]";

            Document matchElems = new Document();
            matchElems.put("entityType.type", event.getContextEntity().getType().getType());
            matchElems.put("entityType.vocabURI", event.getContextEntity().getType().getVocabURI());
            Document attrs = new Document();
            attrs.put("$in", attributes);
            matchElems.put("attributes", attrs);
            Document relEntities = new Document();
            attrs.put("$elemMatch", matchElems);
            Document mongoQuery = new Document();
            mongoQuery.put("relatedEntities", relEntities);

            Document projection = new Document();
            projection.put("_id", 1);
            projection.put("callback", 1);
            projection.put("situation", 1);
            projection.put("parsedQuery", 1);
            projection.put("relatedEntities", relEntities);

            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    JSONObject resultJSON = new JSONObject(document.toJson());
                    finalResultJsonArr.put(resultJSON);
                }
            };

            collection.find(mongoQuery).projection(projection).forEach(printBlock);

            return CdqlSubscription.newBuilder().setStatus("200")
                    .setBody(finalResultJsonArr.toString()).build();

        } catch (Exception e) {
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return CdqlSubscription.newBuilder().setStatus("500")
                    .setBody(body.toString()).build();
        }
    }
}
