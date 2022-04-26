package au.coaas.sqem.util;

import com.google.common.hash.Hashing;

import java.util.Map;
import java.nio.charset.StandardCharsets;

public class Utilty {
    public static long convertTime2MilliSecond(String unit, int value) {
        switch (unit.toLowerCase()) {
            case "s":
                return value * 1000;
            case "min":
                return value * 1000 * 60;
            case "hour":
                return value * 1000 * 60 * 60;
            case "day":
                return value * 1000 * 60 * 60 * 24;
            default:
                return value;
        }
    }

    public static String getHashKey(Map<String,String> params){
        String hashKey = "";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            hashKey = hashKey + entry.getKey() + "@" + entry.getValue() + ";";
        }

        return Hashing.sha256().hashString(hashKey, StandardCharsets.UTF_8).toString();
    }

    public static String getStatus(String status){
        switch(status){
            case "200": return "ok";
            case "400": return "unauth";
            case "404": return "notfound";
            case "500": return "error";
        }
    }
}
