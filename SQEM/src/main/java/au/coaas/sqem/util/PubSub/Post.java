package au.coaas.sqem.util.PubSub;

abstract class Post {
    String hashKey;
    double accessRate;

    Post(String hashKey, double accessRate) {
        this.hashKey = hashKey;
        this.accessRate = accessRate;
    }
}
