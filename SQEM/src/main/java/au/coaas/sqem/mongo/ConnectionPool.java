package au.coaas.sqem.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;

public class ConnectionPool {

    private static ConnectionPool instance;

    private MongoClient mongoClient;

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    private final static String Mongo_Connection_STRING =  "mongodb://mongodb:27017";

    private ConnectionPool() {
        MongoClientOptions.Builder options = MongoClientOptions.builder()
                .connectionsPerHost(400)
                .maxConnectionIdleTime((60 * 1_000))
                .maxConnectionLifeTime((120 * 1_000));
        MongoClientURI connectionString = new MongoClientURI(Mongo_Connection_STRING,options);
        mongoClient = new MongoClient(connectionString);
    }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            //synchronized block to remove overhead
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    // if instance is null, initialize
                    instance = new ConnectionPool();
                }

            }
        }
        return instance;
    }


}
