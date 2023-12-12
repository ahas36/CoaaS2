package au.coaas.sqem.monitor;

import au.coaas.sqem.proto.PubSubMessage;

/**
 * @author shakthi
 */
public class Subscriber {
    public Subscriber(String[] topics) {
        for (String t : topics) {
            ContentServer.getInstance().registerSubscriber(this, t);
        }
    }

    public void receivedMessage(String topic, PubSubMessage msg) {
        switch(topic) {
            case "evict": break;
            case "refresh": break;
            case "performance": break;
        }
    }
}
