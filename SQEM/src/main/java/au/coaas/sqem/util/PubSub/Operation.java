package au.coaas.sqem.util.PubSub;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Operation extends Event{
    private static final Logger log = Logger.getLogger(Operation.class.getName());

    public void subscribe(String channelName, Object subscriber) {
        if (!channels.containsKey(channelName)) {
            channels.put(channelName, new ConcurrentHashMap<>());
        }
        channels.get(channelName).put(subscriber.hashCode(), new WeakReference<>(subscriber));
    }

    public void publish(String channelName, Post message) {
        try {
            for(Map.Entry<Integer, WeakReference<Object>> subs : channels.get(channelName).entrySet()) {
                WeakReference<Object> subscriberRef = subs.getValue();

                Object subscriberObj = subscriberRef.get();

                for (final Method method : subscriberObj.getClass().getDeclaredMethods()) {
                    Annotation annotation = method.getAnnotation(OnMessage.class);
                    if (annotation != null) {
                        deliverMessage(subscriberObj, method, message);
                    }
                }
            }
        }
        catch(Exception ex){
            log.severe("Exception when publishing an event: " + ex.getMessage());
        }
    }

    public void unsubscribe(String channelName, Object subscriber) {
        if (channels.containsKey(channelName)) {
            channels.get(channelName).remove(subscriber.hashCode());
        }
    }

    public void deleteChannel(String channelName) {
        if (channels.containsKey(channelName)) {
            channels.remove(channelName);
        }
    }

    public boolean checkChannel(String channelName) {
        return channels.containsKey(channelName);
    }

    private <T, P extends Post> boolean deliverMessage(T subscriber, Method method, Post message) {
        try {
            boolean methodFound = false;
            for (final Class paramClass : method.getParameterTypes()) {
                if (paramClass.equals(message.getClass())) {
                    methodFound = true;
                    break;
                }
            }
            if (methodFound) {
                method.setAccessible(true);
                method.invoke(subscriber, message);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
