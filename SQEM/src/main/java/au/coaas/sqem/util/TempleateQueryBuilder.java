package au.coaas.sqem.util;

import com.mongodb.client.model.Filters;
import org.bson.BsonType;
import org.bson.conversions.Bson;

public class TempleateQueryBuilder {
    public static Bson buildMatch(String attributeName, long currentTime, Bson eqObject, Bson eqDefault, Bson eqArray) {
        currentTime=currentTime/1000;
        Bson result = eqDefault;
        return result;
    }

    public static Bson buildMatchOrExpiers(String attributeName, long currentTime, Bson eqObject, Bson eqArray) {
        currentTime=currentTime/1000;

        Bson result = Filters.or(
                Filters.and(
                        Filters.not(Filters.type(attributeName, BsonType.ARRAY)),
                        Filters.or(
                                Filters.and(
                                        Filters.or(
                                                Filters.exists(attributeName + ".coaas:metaData.coaas:expieryTime", false),
                                                Filters.gt(attributeName + ".coaas:metaData.coaas:expieryTime", currentTime)
                                        ),
                                        eqObject
                                ),
                                Filters.and(
                                        Filters.exists(attributeName + ".coaas:metaData.coaas:expieryTime"),
                                        Filters.lt(attributeName + ".coaas:metaData.coaas:expieryTime", currentTime)
                                )
                        )
                ),
                Filters.elemMatch(attributeName,
                        Filters.or(
                                Filters.and(
                                        Filters.or(
                                                Filters.exists(attributeName + ".coaas:metaData.coaas:expieryTime", false),
                                                Filters.gt(attributeName + ".coaas:metaData.coaas:expieryTime", currentTime)
                                        ),
                                        eqArray
                                ),
                                Filters.and(
                                        Filters.exists(attributeName + ".coaas:metaData.coaas:expieryTime"),
                                        Filters.lt(attributeName + ".coaas:metaData.coaas:expieryTime", currentTime)
                                )
                        )
                )
        );
        return result;
    }

}
