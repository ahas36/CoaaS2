package au.coaas.sqem.util;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * @author shakthi
 */
public class ResponseUtils {
    public static List<Object> parseJsonArray(JSONArray ja) {
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            Object subItem = ja.get(i);
            if (subItem instanceof JSONObject) {
                result.add(convertJSONObject2Document(subItem));
            } else if (subItem instanceof JSONArray) {
                result.add(parseJsonArray((JSONArray) subItem));
            } else {
                result.add(subItem);
            }
        }
        return result;
    }

    // Converts a JSON Object to a Document type
    public static Document convertJSONObject2Document(Object item) {
        Document document = new Document();
        if (item instanceof JSONObject) {
            JSONObject jo = (JSONObject) item;
            for (String key : jo.keySet()) {
                Object subItem = jo.get(key);
                if (subItem instanceof JSONObject) {
                    document.put(key, convertJSONObject2Document(subItem));
                } else if (subItem instanceof JSONArray) {
                    document.put(key,parseJsonArray((JSONArray) subItem));
                } else {
                    document.put(key, subItem);
                }
            }
        }
        return document;
    }

    // Converts a Bson list to a string
    public static String queryToString(List<Bson> query) {
        String result = "[\n";
        for (Bson item : query) {
            result += item.toBsonDocument(org.bson.BsonDocument.class, MongoClient.getDefaultCodecRegistry()).toJson() + ",\n";
        }
        result += "]";
        return result;
    }

    public static Bson generateDateFilter(Long startDate, Long endDate, String attributeName, boolean isExact) {
        Bson dateFilterMatch = null;
        if (startDate != 0 && endDate != 0) {
            dateFilterMatch = Filters.and(Filters.gte(attributeName, getDateFromLong(startDate, isExact)), Filters.lte(attributeName, getDateFromLong(endDate, isExact)));
        } else {
            if (startDate != 0) {
                dateFilterMatch = Filters.gte(attributeName, getDateFromLong(startDate, isExact));
            } else if (endDate != 0) {
                dateFilterMatch = Filters.lte(attributeName, getDateFromLong(endDate, isExact));
            }
        }
        return dateFilterMatch;
    }

    // Converts time epochs to DateTime format
    public static Object getDateFromLong(Long dateLong, boolean isExact) {
        if (isExact) {
            return dateLong;
        }
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date(dateLong));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
