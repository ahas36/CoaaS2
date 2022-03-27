package au.coaas.sqem.redis;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.api.RedissonClient;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/* Author: Shakthi */

public class ConnectionPool {

    private RedissonClient redisClient;

    private static au.coaas.sqem.redis.ConnectionPool instance;

    public RedissonClient getRedisClient() {
        return redisClient;
    }

    private static Logger log = Logger.getLogger(ConnectionPool.class.getName());

    private ConnectionPool() {
        // Using Clustered Node
        // Could be connected to AWS ElasticCache or Azure Redis Cache (Change only the connection string)
        try{
            Config config = Config.fromYAML(new File("cacheconfig.yaml"));
            redisClient = Redisson.create(config);
        }
        catch(IOException ex){
            log.severe(ex.getMessage());
        }
    }

    // SSH Connection to Redis cache instance in Deakin cluster
    private static void ssh() throws JSchException {
        String host = "";
        String user = "shakthi";
        String password = "z3#@KrUayVmiHIgN";

        int port = 22;
        int tunnelLocalPort = 3309;
        int tunnelRemotePort = 6379;
        String tunnelRemoteHost = "localhost";

        JSch jsch = new JSch();
        com.jcraft.jsch.Session session = jsch.getSession(user, host, port);
        session.setPassword(password);

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();
        session.setPortForwardingL(tunnelLocalPort, tunnelRemoteHost, tunnelRemotePort);
    }

    public static au.coaas.sqem.redis.ConnectionPool getInstance() {
        if (instance == null) {
            synchronized (au.coaas.sqem.redis.ConnectionPool.class) {
                // Implements a singleton
                if (instance == null) {
                    instance = new au.coaas.sqem.redis.ConnectionPool();
                }
            }
        }
        return instance;
    }
}
