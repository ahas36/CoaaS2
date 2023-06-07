package au.coaas.sqem.util.PubSub;

import au.coaas.sqem.handler.ContextCacheHandler;

public class Subscriber {
    String name;
    double lambda;

    String entity;
    String entHashkey;
    String situationName;
    String situHashkey;

    public Subscriber(String name, double lambda) {
        this.name = name;
        this.lambda = lambda;
    }

    // name: situ:entity-ID
    // situation: situ-hashkey
    public Subscriber(String name, String situation) {
        String entityId = (name.split(":"))[0];
        this.entity = (entityId.split("-"))[0];
        this.entHashkey = (entityId.split("-"))[1];

        this.situationName = (situation.split("-"))[0];
        this.situHashkey = (situation.split("-"))[1];;
    }

    @OnMessage
    private void onMessage(Message message) {
        if(message.getEntity() != null && message.getEntity().equals(entity)
                && message.getHashkey().equals(entHashkey)){
            boolean status = ContextCacheHandler.updateSituation(message.getEntity(), message.getHashkey(),
                    situationName, situHashkey);
            if(status)
                Event.operation.unsubscribe("situationUpdate", this);
        }
        else {
            if(message.getAccessRate() <= lambda){
                ContextCacheHandler.evictContext(message.getHashkey(), false);
            }
        }
    }
}
