package au.coaas.sqem.handler;

import au.coaas.cre.proto.ContextEvent;
import au.coaas.sqem.proto.Empty;
import au.coaas.sqem.proto.Situation;
import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.CdqlSubscription;

import au.coaas.sqem.util.CollectionDiscovery;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import com.google.common.base.Joiner;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            Block<Document> printBlock = document -> {
                JSONObject resultJSON = new JSONObject(document.toJson());
                finalResultJsonArr.put(resultJSON);
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

    public static Empty updateSubscription(ContextEvent event){
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas");
            MongoCollection<Document> entityCollection = db.getCollection(CollectionDiscovery.discover(event.getContextEntity().getType()));

            String pattern = "\\[[^\\]]*\\]";
            Pattern regex = Pattern.compile(pattern);

            List<Bson> arrayFilters = new ArrayList<>();
            BasicDBObject updateFields = new BasicDBObject();

            for (Map.Entry<String, String> entry : event.getContextEntity().getContextAttributesMap().entrySet()) {
                String attributeName = entry.getKey();
                Object value = entry.getValue();
                Matcher m = regex.matcher(attributeName);
                while (m.find()) {
                    String res = m.group();
                    attributeName = attributeName.replace(res, ".$" + res.toLowerCase());
                    res = res.substring(1, res.length() - 1);
                    arrayFilters.add(Filters.eq(res.toLowerCase() + ".@id", res));
                }
                updateFields.append(attributeName, value);
            }

            BasicDBObject query = new BasicDBObject();
            if (event.getKey() == null) {
                query.put("_id", new ObjectId(event.getContextEntity().getEntityID()));
            } else {
                query.put(event.getKey(), event.getContextEntity().getEntityID());
            }
            entityCollection.updateOne(query, new Document("$set", updateFields),
                    new UpdateOptions().arrayFilters(arrayFilters));
        } catch (Exception ex) {
            log.severe("Error occured in subscription update: " + ex.getMessage());
        }

        return null;
    }
}
