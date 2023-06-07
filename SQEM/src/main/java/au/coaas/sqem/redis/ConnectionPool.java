package au.coaas.sqem.redis;

import au.coaas.sqem.handler.ContextCacheHandler;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.api.listener.PatternMessageListener;
import org.redisson.spring.cache.RedissonSpringCacheManager;

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

            // Comment this line out when the cluster is set
            // Config config = Config.fromYAML(new File(currdir + yamlFile));

            // The following lines are only for local testing purposes.
            Config config = new Config();
            config.useSingleServer()
                    .setAddress("redis://localhost:6379");
            // Test lines, end here.

            redisClient = Redisson.create(config);
            redisClient.getPatternTopic("__keyevent@*__:expired", StringCodec.INSTANCE)
                    .addListener(String.class, new PatternMessageListener<String>() {
                        @Override
                        public void onMessage(CharSequence pattern, CharSequence channel, String key) {
                            log.info(key + "expired in cache. Starting to evict item.");
                            ContextCacheHandler.evictContext(key, true);
                        }
                    });

            // TODO:
            // Cache Manager
            // MutableConfiguration<String, String> jcacheConfig = new MutableConfiguration<>();
            //
            // Configuration<String, String> mgrconfig = RedissonConfiguration.fromInstance(redisClient, jcacheConfig);
            //
            // CacheManager manager = Caching.getCachingProvider().getCacheManager();
            // Cache<String, String> cache = manager.createCache("namedCache", mgrconfig);
        }
        catch(Exception ex){
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
