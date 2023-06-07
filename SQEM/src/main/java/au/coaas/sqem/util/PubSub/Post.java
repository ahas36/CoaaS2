package au.coaas.sqem.util.PubSub;

abstract class Post {
    String hashKey;
    double accessRate;
    String entity;

    Post(String hashKey, double accessRate, String entity) {
        this.hashKey = hashKey;
        this.accessRate = accessRate;
        this.entity = entity;
    }
}
