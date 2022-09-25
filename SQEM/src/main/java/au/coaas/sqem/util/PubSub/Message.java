package au.coaas.sqem.util.PubSub;

public class Message extends Post {
    private String hashKey;
    private double accessRate;

    public Message(String hashKey, double accessRate) {
        super(hashKey, accessRate);
        this.hashKey = hashKey;
        this.accessRate = accessRate;
    }

    public String getHashkey() {
        return hashKey;
    }
    public double getAccessRate() {
        return accessRate;
    }
}