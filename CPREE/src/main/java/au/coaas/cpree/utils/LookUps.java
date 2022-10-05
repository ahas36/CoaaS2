package au.coaas.cpree.utils;

import au.coaas.cpree.utils.enums.DynamicRegistry;

import java.time.LocalDateTime;
import java.util.Hashtable;

public class LookUps {
    // Dynamic Registries
    private static Hashtable<String, IndefRecord> indefDelayRegistry = new Hashtable<>();
    private static Hashtable<String, DefRecord> delayRegistry = new Hashtable<>();

    public static void write(DynamicRegistry registry, String key, Object value){
        switch(registry) {
            case INDEFDELAYREGISTRY:
                synchronized (LookUps.class){
                    if(!indefDelayRegistry.containsKey(key)){
                        LocalDateTime expiry = LocalDateTime.now().plusHours(1);
                        indefDelayRegistry.put(key, new IndefRecord((double)value,expiry));
                    }
                }
                break;
            case DELAYREGISTRY:
                synchronized (LookUps.class){
                    if(!delayRegistry.containsKey(key)){
                        LocalDateTime expiry = LocalDateTime.now().plusHours(1);
                        delayRegistry.put(key, new DefRecord((LocalDateTime)value, expiry));
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
                else if(indefDelayRegistry.containsKey(key) &&
                        (indefDelayRegistry.get(key).getExpAR() <= (double)value ||
                                indefDelayRegistry.get(key).getExpiryTime().isBefore(LocalDateTime.now()))) {
                    indefDelayRegistry.remove(key);
                    return true;
                }
                break;
            case DELAYREGISTRY:
                if(!delayRegistry.containsKey(key))
                    return true;
                else if (delayRegistry.containsKey(key) &&
                        (delayRegistry.get(key).getDelayTime().isBefore((LocalDateTime)value) ||
                                delayRegistry.get(key).getExpiryTime().isBefore((LocalDateTime)value))) {
                    delayRegistry.remove(key);
                    return true;
                }
                break;
        }
        return false;
    }
}
