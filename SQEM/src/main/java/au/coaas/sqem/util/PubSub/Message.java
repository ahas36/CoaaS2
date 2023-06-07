package au.coaas.sqem.util.PubSub;

public class Message extends Post {
    private String hashKey;
    private double accessRate;
    private String entity;

    public Message(String hashKey, double accessRate) {
        super(hashKey, accessRate, null);
        this.hashKey = hashKey;
        this.accessRate = accessRate;
    }

    public Message(String entity, String hashKey) {
        super(hashKey, 0.0, entity);
        this.hashKey = hashKey;
        this.entity = entity;
    }

    public String getEntity() { return entity; }
    public String getHashkey() { return hashKey; }
    public double getAccessRate() {
        return accessRate;
    }
}