package au.coaas.sqem.mongo;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.UserInfo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;

public class ConnectionPool {

    private static ConnectionPool instance;

    private MongoClient mongoClient;

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    //    private final static String Mongo_Connection_STRING =  "mongodb://localhost:3309";
    private final static String Mongo_Connection_STRING = "mongodb://mongodb:27017";

    private static void ssh() throws JSchException {
        // This is a remote server used by Ali
        String host = "10.133.130.85";
        String user = "alihassani";
        // Is this password encypted?
        String password = "dob!7thDey1367!dob";
        int port = 22;

        int tunnelLocalPort = 3309;
        String tunnelRemoteHost = "localhost";
        int tunnelRemotePort = 27017;

        JSch jsch = new JSch();
        com.jcraft.jsch.Session session = jsch.getSession(user, host, port);
        session.setPassword(password);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();

        session.setPortForwardingL(tunnelLocalPort, tunnelRemoteHost, tunnelRemotePort);
    }


    private ConnectionPool() {
//        try {
//            ssh();
//        } catch (JSchException e) {
//            e.printStackTrace();
//        }
        MongoClientOptions.Builder options = MongoClientOptions.builder()
                .connectionsPerHost(400)
                .maxConnectionIdleTime((60 * 1_000))
                .maxConnectionLifeTime((120 * 1_000));
        MongoClientURI connectionString = new MongoClientURI(Mongo_Connection_STRING, options);
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
