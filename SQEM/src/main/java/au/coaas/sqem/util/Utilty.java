package au.coaas.sqem.util;

import au.coaas.sqem.handler.ContextCacheHandler;
import au.coaas.sqem.proto.ScheduleTask;
import au.coaas.sqem.util.enums.ScheduleTasks;
import com.google.common.hash.Hashing;

import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

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
            hashKey = hashKey + entry.getKey() + "@" + entry.getValue() + ";";
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
}
