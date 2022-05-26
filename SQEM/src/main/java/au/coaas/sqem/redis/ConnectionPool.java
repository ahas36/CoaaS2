package au.coaas.sqem.redis;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/* @author: Shakthi */

public class ConnectionPool {

    private RedissonClient redisClient;
    private RedissonSpringCacheManager redisManager;

    private static au.coaas.sqem.redis.ConnectionPool instance;

    public RedissonClient getRedisClient() {
        return redisClient;
    }

    private static Logger log = Logger.getLogger(ConnectionPool.class.getName());

    private ConnectionPool() {
        // Using Clustered Node
        // Could be connected to AWS ElasticCache or Azure Redis Cache (Change only the connection string)
        try{
            String currdir = System.getProperty("user.dir");
            // When executing locally
            String yamlFile = "/SQEM/src/main/java/au/coaas/sqem/redis/cacheconfig.yaml";
            // When executing in a container
            // String yamlFile = "/cacheconfig.yaml";

            Config config = Config.fromYAML(new File(currdir + yamlFile));
            redisClient = Redisson.create(config);

            // TODO:
            // Cache Manager
            // MutableConfiguration<String, String> jcacheConfig = new MutableConfiguration<>();
            //
            // Configuration<String, String> mgrconfig = RedissonConfiguration.fromInstance(redisClient, jcacheConfig);
            //
            // CacheManager manager = Caching.getCachingProvider().getCacheManager();
            // Cache<String, String> cache = manager.createCache("namedCache", mgrconfig);
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
