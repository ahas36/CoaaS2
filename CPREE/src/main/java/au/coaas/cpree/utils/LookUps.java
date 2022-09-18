package au.coaas.cpree.utils;

import au.coaas.cpree.utils.enums.DynamicRegistry;

import java.time.LocalDateTime;
import java.util.Hashtable;

public class LookUps {
    // Dynamic Registries
    private static Hashtable<String, Double> indefDelayRegistry = new Hashtable<>();
    private static Hashtable<String, LocalDateTime> delayRegistry = new Hashtable<>();

    public static void write(DynamicRegistry registry, String key, Object value){
        switch(registry) {
            case INDEFDELAYREGISTRY:
                synchronized (LookUps.class){
                    if(!indefDelayRegistry.containsKey(key)){
                        indefDelayRegistry.put(key,(double)value);
                    }
                }
                break;
            case DELAYREGISTRY:
                synchronized (LookUps.class){
                    if(!delayRegistry.containsKey(key)){
                        delayRegistry.put(key,(LocalDateTime)value);
                    }
                }
                break;
        }
    }

    public static boolean lookup(DynamicRegistry registry, String key, Object value){
        switch(registry) {
            case INDEFDELAYREGISTRY:
                if(!indefDelayRegistry.containsKey(key))
                    return true;
                else if(indefDelayRegistry.containsKey(key) && indefDelayRegistry.get(key) <= (double)value) {
                    indefDelayRegistry.remove(key);
                    return true;
                }
                break;
            case DELAYREGISTRY:
                if(!delayRegistry.containsKey(key))
                    return true;
                else if (delayRegistry.get(key).isAfter((LocalDateTime)value)){
                    delayRegistry.remove(key);
                    return true;
                }
                break;
        }
        return false;
    }
}
