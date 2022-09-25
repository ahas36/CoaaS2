package au.coaas.sqem.util.PubSub;

import au.coaas.sqem.handler.ContextCacheHandler;

public class Subscriber {
    String name;
    double lambda;

    public Subscriber(String name, double lambda) {
        this.name = name;
        this.lambda = lambda;
    }

    @OnMessage
    private void onMessage(Message message) {
        if(message.getAccessRate() <= lambda){
            ContextCacheHandler.evictEntity(message.getHashkey(), false);
        }
    }
}
