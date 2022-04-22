package au.coaas.sqem.monitor;

import au.coaas.sqem.proto.PubSubMessage;

public class Subscriber {
    public Subscriber(String[] topics) {
        for (String t : topics) {
            ContentServer.getInstance().registerSubscriber(this, t);
        }
    }

    public void receivedMessage(String topic, PubSubMessage msg) {
        switch(topic) {
            case "evict": break;
            case "refresg": break;
            case "performance": break;
        }
    }
}
