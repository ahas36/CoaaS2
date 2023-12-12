package au.coaas.sqem.monitor;

import au.coaas.sqem.proto.PubSubMessage;

import java.util.Hashtable;
import java.util.List;

/**
 * @author shakthi
 */
public class ContentServer {
    private Hashtable<String, List<Subscriber>> subscriberLists;

    private static ContentServer serverInstance;

    public static ContentServer getInstance() {
        if (serverInstance == null) {
            serverInstance = new ContentServer();
        }
        return serverInstance;
    }

    private ContentServer() {
        this.subscriberLists = new Hashtable<>();
    }

    public void sendMessage(String topic, PubSubMessage msg) {
        List<Subscriber> subs = subscriberLists.get(topic);
        for (Subscriber s : subs) {
            s.receivedMessage(topic, msg);
        }
    }

    public void registerSubscriber(Subscriber sub, String topic) {
        subscriberLists.get(topic).add(sub);
    }
}
