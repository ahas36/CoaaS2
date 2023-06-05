package au.coaas.sqem.util;

import au.coaas.sqem.handler.ContextCacheHandler;
import au.coaas.sqem.proto.ScheduleTask;
import au.coaas.sqem.util.enums.ScheduleTasks;
import com.google.common.hash.Hashing;
import org.json.JSONObject;

import java.util.*;
import java.nio.charset.StandardCharsets;

// General utility functions used by the service
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
            hashKey = hashKey + entry.getKey() + "@" + entry.getValue().replace("\"","") + ";";
        }

        return Hashing.sha256().hashString(hashKey, StandardCharsets.UTF_8).toString();
    }

    public static String getHashKey(String key){
        return Hashing.sha256().hashString(key, StandardCharsets.UTF_8).toString();
    }

    public static String getStatus(String status){
        switch(status){
            case "200": return "ok";
            case "400": return "unauth";
            case "404": return "notfound";
            default: return "error";
        }
    }

    public static void schedulTask(ScheduleTasks task, ScheduleTask reference, long delay){
        switch(task){
            case EVICT: {
                TimerTask timetask = new TimerTask() {
                    public void run() {
                        ContextCacheHandler.fullyEvict(reference);
                    }
                };
                Timer timer = new Timer("Timer");
                timer.schedule(timetask, delay);
                break;
            }
        }
    }

    public JSONObject getLifetime(String entityType){
        JSONObject lifetime = new JSONObject();
        lifetime.put("unit","s");
        switch(entityType.toLowerCase()){
            case "car":
            case "vehicle": {
                lifetime.put("value",30.0);
                break;
            }
            case "thing":
            case "weather": {
                lifetime.put("value",3600.0);
                break;
            }
            case "place": {
                lifetime.put("value",1800.0);
                break;
            }
            case "carpark":
            case "parkingfacility": {
                lifetime.put("value",60.0);
                break;
            }
            default: lifetime.put("value",1200.0);
        }
        return lifetime;
    }

    public static String combineHashKeys(List<String> hashkeys) {
        String result = "";
        for(int i=0; i < hashkeys.size(); i++){
            if(i == 0){
                result = hashkeys.get(0);
                continue;
            }
            result = base64Encode(xorWithKey(result.getBytes(), hashkeys.get(i).getBytes())) ;
        }
        return result;
    }

    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i%key.length]);
        }
        return out;
    }

    private static String base64Encode(byte[] bytes) {
        Base64.Encoder enc = Base64.getEncoder();
        return new String(enc.encode(bytes)).replaceAll("\\s", "");
    }
}
