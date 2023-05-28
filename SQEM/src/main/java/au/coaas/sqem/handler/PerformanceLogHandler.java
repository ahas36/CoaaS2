package au.coaas.sqem.handler;

import au.coaas.cpree.proto.CPREEServiceGrpc;
import au.coaas.cpree.proto.LearnedWeights;
import au.coaas.cqc.proto.CQCServiceGrpc;
import au.coaas.cqc.proto.RegisterState;
import au.coaas.grpc.client.CPREEChannel;
import au.coaas.grpc.client.CQCChannel;
import au.coaas.sqem.monitor.LogicalContextLevel;
import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.*;
import au.coaas.sqem.util.HostMonitor;
import au.coaas.sqem.util.HttpClient;
import au.coaas.sqem.util.LimitedQueue;
import au.coaas.sqem.util.PubSub.Event;
import au.coaas.sqem.util.PubSub.Message;
import au.coaas.sqem.util.Utilty;
import au.coaas.sqem.util.enums.DelayCacheLatency;
import au.coaas.sqem.util.enums.HttpRequests;
import au.coaas.sqem.util.enums.PerformanceStats;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.AtomicDouble;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import static au.coaas.sqem.util.StatisticalUtils.*;

public class PerformanceLogHandler {

    private static Connection connection;

    private static final int numberOfThreads = 20;
    private static final int numberOfItemsPerTask = 10;

    private static final Logger log = Logger.getLogger(LogHandler.class.getName());
    private static List<String> all_tables = Stream.of(LogicalContextLevel.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private static final int max_history = 10;
    private static final long max_delay_cache_residence = 3600 * 1000;
    private static boolean registriesReady = false;
    private static final double processPrice = 0.00000024;

    private static boolean popularBased = false; // Should be False

    // TODO: the size of the queue should be adaptive to the size of the planning period
    // This is, based on the size of the planning, the queue should contain all the history up to maximum default
    // For example, if PP = 10mins, then (since W = 1min) 10mins/1min = 10.
    private static LimitedQueue<HashMap<String, Double>> slahistory = new LimitedQueue(max_history);
    private static final ArrayList<String> slaProperties =
            new ArrayList<>(Arrays.asList("exp_pen", "exp_fth", "exp_earn", "exp_rtmax"));

    /** Statistical Handling */
    // Refresh the expected quality metrics during the next window by estimating
    // Linear regression is used to estimate the expected values of the parameters
    public static void refershExpectedConsumerSLA(){
        try{
            ConcurrentHashMap<String, Double> exp_sla = new ConcurrentHashMap<>();
            slaProperties.parallelStream().forEach((param) -> {
                double[][] dataset = new double[max_history][2];
                for(int i = 0; i < slahistory.size(); i++){
                    dataset[i][0] = i;
                    dataset[i][1] = slahistory.get(i).get(param);
                }
                // There values are unconstrained. So, e.g., fthresh > 1 or < 0 which should be handled.
                exp_sla.put(param, predictExpectedValue(dataset, max_history + 1));
            });
            ContextCacheHandler.updatePerformanceStats(new HashMap<>(exp_sla), PerformanceStats.expected_sla);
        }
        catch(Exception ex){
            log.info("Failed to estimate the consumer SLA: " + ex.getMessage());
        }
    }

    // Get the expected quality metrics during the next window
    // This value is for the overall CMP (different from the Context Query Class expectation)
    public static SQEMResponse getExpectedSLAParameter(String key){
        return ContextCacheHandler.getPerformanceStats(key, PerformanceStats.expected_sla);
    }

    /** Performance Logging */
    // CSMS Performance Records
    // Inserts a new performance record
    public static void insertRecord(LogicalContextLevel level, String id, Boolean isHit, long rTime) {
        String queryString = "INSERT INTO %s(itemId,isHit,response_time,createdDatetime) VALUES('%s', %d, %d, CAST('%s' AS DATETIME2));";
        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            LocalDateTime now = LocalDateTime.now();

            statement.executeUpdate(String.format(queryString, level.toString().toLowerCase(),
                    id, isHit?1:0, rTime, now.format(formatter)));
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
    }

    // Recording teh recent performance of CSMS
    public static void genericRecord(String method, String status, long rTime) {
        String queryString = "INSERT INTO csms_performance(method,status,response_time,createdDatetime) "
                + "VALUES('%s', '%s', %d, CAST('%s' AS DATETIME2));";
        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            LocalDateTime now = LocalDateTime.now();

            statement.executeUpdate(String.format(queryString, method, status, rTime, now.format(formatter)));
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
    }

    // Recording for profiling context consumers
    public static void consumerRecord(SummarySLA conSummary) {
        String queryString = "INSERT INTO consumer_slas "
                + "VALUES('%s', %f, %f, %d, %f, '%s', %d, CAST('%s' AS DATETIME2));";
        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            LocalDateTime now = LocalDateTime.now();

            String exec_string = String.format(queryString,
                    conSummary.getCsid(), conSummary.getFthresh(), conSummary.getEarning(),
                    conSummary.getRtmax(), conSummary.getPenalty(), conSummary.getQueryId(),
                    conSummary.getQueryClass(), now.format(formatter));
            statement.executeUpdate(exec_string);
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
    }

    public static void summarizeAge (Statistic ents){
        JSONArray entities = new JSONArray(ents.getMethod());
        if(!entities.isEmpty()){
            for(Object ent: entities){
                JSONObject properEntity = (JSONObject) ent;

                Double ageInMS = properEntity.getJSONObject("age").getDouble("value") * 1000;
                JSONObject updated = properEntity.getJSONObject("updatedTime");
                Date now = new Date();
                long nowTime = now.getTime();

                long totalAge = 0;
                String date = updated.getString("$date").replace("Z","");
                ZonedDateTime datetime = LocalDateTime.parse(date).atZone(ZoneId.systemDefault());
                long diff = nowTime - datetime.toInstant().toEpochMilli();
                totalAge = ageInMS.longValue() + diff;

                coassPerformanceRecord(Statistic.newBuilder()
                        .setMethod("queryRetrieval").setAge(totalAge).build());
            }
        }
    }

    // COASS Performance
    public static void coassPerformanceRecord(Statistic request) {
        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String method = request.getMethod();
            switch(method){
                case "execute" :{
                    // Value at the Identifier column here is the Query ID
                    String queryString_1 = "INSERT INTO coass_performance(method,status,response_time,earning,"+
                            "cost,identifier,hashKey,createdDatetime,isDelayed,age) " +
                            "VALUES('%s', '%s', %d, %f, %f, '%s', '%s', CAST('%s' AS DATETIME2), %d, %d);";
                    LocalDateTime now = LocalDateTime.now();

                    String formatted_string = String.format(queryString_1,
                            method, // Method name
                            request.getStatus(), // Status of the request
                            request.getTime(), // Response time
                            request.getEarning(), // Earnings from the query
                            request.getCost(), // Cost from query
                            request.getIdentifier(), // Context Service Identifier
                            "NULL", // Hashkey of the cached item
                            now.format(formatter),
                            request.getIsDelayed() ? 1 : 0, // is Delayed?
                            0); // age
                    statement.executeUpdate(formatted_string);
                    break;
                }
                case "executeFetch":
                case "FetchJob-execute":
                case "executeStreamRead":{
                    JSONObject cs = new JSONObject(request.getCs().getContextService().getJson());
                    String cs_id = cs.getJSONObject("_id").getString("$oid");

                    // Value at the Identifier column here is the Context Service ID

                    LocalDateTime now = LocalDateTime.now();

                    if(request.getStatus().equals("200")){
                        String queryString = "INSERT INTO coass_performance(method,status,response_time,earning,"+
                                "cost,identifier,hashkey, createdDatetime,isDelayed,age,fthresh) " +
                                "VALUES('%s', '%s', %d, %f, %f, '%s', '%s', CAST('%s' AS DATETIME2), %d, %d, %f);";

                        int numberOfIterations = (int)(request.getHaskeysCount()/numberOfItemsPerTask) + 1;

                        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

                        for (int factor = 0; factor< numberOfIterations ; factor++)
                        {
                            int finalFactor = factor;
                            executorService.submit(() -> {
                                int start = numberOfItemsPerTask * finalFactor;
                                int end =  Math.min(request.getHaskeysCount(),start+numberOfItemsPerTask);
                                for(int i = start; i < end; i++){
                                    String formatted_string = String.format(queryString,
                                            method, // Method name
                                            request.getStatus(), // Status of the retrieval
                                            request.getTime(), // Response time
                                            request.getEarning(), // Earnings from the retrieval (penalties)
                                            request.getCost(), // Cost of retrieval
                                            cs_id, // Context Service Identifier
                                            request.getHaskeys(i), // Hashkey of the entity
                                            now.format(formatter),
                                            request.getIsDelayed() ? 1 : 0, // is Delayed?
                                            request.getAge(), //age
                                            cs.getJSONObject("sla").getJSONObject("freshness").getDouble("fthresh")); // fthresh
                                    try {
                                        statement.executeUpdate(formatted_string);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        executorService.shutdown();

                        try {
                            executorService.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
                        } catch (InterruptedException e) {
                            log.severe("Error when recording performance log: "+ e.getMessage());
                        }
                    }
                    else {
                        String queryString = "INSERT INTO coass_performance(method,status,response_time,earning,"+
                                "cost,identifier,createdDatetime,isDelayed,age,fthresh) " +
                                "VALUES('%s', '%s', %d, %f, %f, '%s', CAST('%s' AS DATETIME2), %d, %d, %f);";
                        String formatted_string = String.format(queryString,
                                method, // Method name
                                request.getStatus(), // Status of the retrieval
                                request.getTime(), // Response time
                                request.getEarning(), // Earnings from the retrieval (penalties)
                                request.getCost(), // Cost of retrieval
                                cs_id, // Context Service Identifier
                                now.format(formatter),
                                request.getIsDelayed() ? 1 : 0, // is Delayed?
                                request.getAge(), //age
                                cs.getJSONObject("sla").getJSONObject("freshness").getDouble("fthresh")); // fthresh
                        statement.executeUpdate(formatted_string);
                    }

                    String queryString_2 = "INSERT INTO retrieval_summary(status,response_time,"+
                            "cost,identifier,createdDatetime,isDelayed) " +
                            "VALUES('%s', %d, %f, '%s', CAST('%s' AS DATETIME2), %d);";
                    String formatted_string_2 = String.format(queryString_2,
                            request.getStatus(), // Status of the retrieval
                            request.getTime(), // Response time
                            request.getCost(), // Cost of retrieval
                            cs_id, // Context Service Identifier
                            now.format(formatter),
                            request.getIsDelayed() ? 1 : 0); // is Delayed?
                    statement.executeUpdate(formatted_string_2);
                    break;
                }
                case "queryRetrieval": {
                    LocalDateTime now = LocalDateTime.now();
                    String queryString_2 = "INSERT INTO retrieval_summary(status,response_time,"+
                            "cost,identifier,createdDatetime,isDelayed) " +
                            "VALUES('%s', %d, %f, '%s', CAST('%s' AS DATETIME2), %d);";
                    String formatted_string_2 = String.format(queryString_2,
                            "200", // Status of the retrieval
                            request.getAge(), // Response time
                            0.0, // Cost of retrieval
                            "coaas", // Context Service Identifier
                            now.format(formatter),
                            0); // is Delayed?
                    statement.executeUpdate(formatted_string_2);
                    break;
                }
                case "cacheSearch": {
                    String queryString_2 = "INSERT INTO coass_performance(method,status,response_time,identifier,createdDatetime,"+
                            "isDelayed, fthresh) VALUES('%s', '%s', %d, '%s', CAST('%s' AS DATETIME2), %d, %f);";
                    LocalDateTime now = LocalDateTime.now();

                    String formatted_string = String.format(queryString_2,
                            method, // Method name
                            request.getStatus(), // Status of the request
                            request.getTime(), // Response time
                            request.getIdentifier(), // Context Service Identifier
                            now.format(formatter),
                            0, // is Delayed?
                            request.getEarning()); //fthresh
                    statement.executeUpdate(formatted_string);
                    break;
                }
                case "infer": {
                    String queryString_2 = "INSERT INTO coass_performance(method,status,response_time,identifier,createdDatetime,"+
                            "isDelayed) VALUES('%s', '%s', %d, '%s', CAST('%s' AS DATETIME2), %d);";
                    LocalDateTime now = LocalDateTime.now();

                    String formatted_string = String.format(queryString_2,
                            method, // Method name
                            request.getStatus(), // Status of the request
                            request.getTime(), // Response time
                            request.getIdentifier(), // Context Identifier
                            now.format(formatter),
                            0); // is Delayed?
                    statement.executeUpdate(formatted_string);
                }
            }
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
    }

    // CPREE Performance
    public static void cpreePerformanceRecord(Statistic request) {
        String queryString = "INSERT INTO cpree_performance(method,status,response_time,"+
                "createdDatetime) " +
                "VALUES('%s', '%s', %d, CAST('%s' AS DATETIME2));";
        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            LocalDateTime now = LocalDateTime.now();

            String method = request.getMethod();
            String formatted_string = String.format(queryString,
                    method, // Method name
                    request.getStatus(), // Status of the request
                    request.getTime(), // Response time
                    now.format(formatter));
            statement.executeUpdate(formatted_string);
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
    }

    // Persist the decision history of the reinforcement agent
    public static void logDecisionLatency(String type, long latency, DelayCacheLatency decType){
        try{
                String queryString = "INSERT INTO cacheHistoryRegistry(type,latency,createdDatetime,horizon) " +
                        "VALUES('%s', %d, CAST('%s' AS DATETIME2), '%s');";
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);

                LocalDateTime now = LocalDateTime.now();
                if(decType.equals(DelayCacheLatency.INDEFINITE))
                    latency = max_delay_cache_residence;
                statement.executeUpdate(String.format(queryString, type, latency, now.format(formatter),
                        decType.equals(DelayCacheLatency.DEFINITE) ? "definite" : "indefinite"));
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
    }

    // Persist the caching decisions of context
    public static void logCacheDecision(JSONObject decision, String level){
        try{
            String finalString;
            String queryString = "INSERT INTO cacheDecisionHistory(retEff, cacheEff, reliability, complexity, accessTrend, normalizer, " +
                    "kappa, mu, delta, row, pi, threshold, isDefinite, level, decision, latency, decisionTime) " +
                    "VALUES(%f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %d, '%s', '%s', %f, CAST('%s' AS DATETIME2));";
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            LocalDateTime now = LocalDateTime.now();

            if(decision.getBoolean("definite")){
                String decs = decision.getString("label");
                finalString = String.format(queryString,
                        decision.getDouble("retEff"), decision.getDouble("cacheEff"), decision.getDouble("reli"),
                        decision.getDouble("complexity"), decision.getDouble("ar"), decision.getDouble("normalizer"),
                        decision.getDouble("kappa"), decision.getDouble("mu"), decision.getDouble("delta"),
                        decision.getDouble("row"), decision.getDouble("pi"), decision.getDouble("threshold"),
                        1, level, decs, decs.startsWith("cache") ? decision.getDouble("cache_life") : decision.getDouble("delay_time"),
                        now.format(formatter));
            }
            else {
                finalString = String.format(queryString,
                        decision.getDouble("retEff"), decision.getDouble("cacheEff"), decision.getDouble("reli"),
                        decision.getDouble("complexity"), decision.getDouble("ar"), decision.getDouble("normalizer"),
                        decision.getDouble("kappa"), decision.getDouble("mu"), decision.getDouble("delta"),
                        decision.getDouble("row"), decision.getDouble("pi"), decision.getDouble("threshold"),
                        0, level, decision.getString("label"), decision.getDouble("lambda_conf"),
                        now.format(formatter));
            }
            statement.executeUpdate(finalString);
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
    }

    // Context Accessing
    public static void insertAccess(String id, String outcome, double age) {
        String queryString = "INSERT INTO context_access(time,context_id,outcome,age) VALUES('%s','%s','%s', %f);";
        Connection conn = au.coaas.sqem.timescale.ConnectionPool.getInstance().getTSConnection();
        try{
            Statement statement = conn.createStatement();
            LocalDateTime now = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(now);
            String ts = timestamp.toString();
            statement.executeUpdate(String.format(queryString, ts, id, outcome, age));
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
    }

    /** Resetting/Refreshing Temporary Performance Records */
    // Clears older records earlier than the current window from the in-memory database
    // These records are persisted in MongoDB logs
    public static void clearOldRecords(int duration){
        String[] stockArr = new String[all_tables.size()];

        // Refreshing the expected consumer SLA in cache
        Executors.newCachedThreadPool().execute(() -> refershExpectedConsumerSLA() );

        LocalDateTime windowThresh = LocalDateTime.now().minusSeconds(duration);
        String forwT = windowThresh.format(formatter);

        Arrays.stream(all_tables.toArray(stockArr)).parallel().forEach((level)-> {
            if(level.equals("consumer_slas")) resetConsumerRecords(forwT);
            else removeAndPersistRecord(level, forwT);
        });
    }

    // Clear context consumer records for the past window
    private static void resetConsumerRecords(String forwT){
        try{
            String deleteString = "DELETE FROM consumer_slas WHERE createdDatetime <= CAST('%s' AS DATETIME2);";
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(String.format(deleteString, forwT));
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
        }
    }

    // Clear performance data in temporary storage and store them in persistent storage
    private static void removeAndPersistRecord(String level, String forwT) {
        level = level.toLowerCase();
        try{
            String getString = "SELECT * FROM %s WHERE createdDatetime <= CAST('%s' AS DATETIME2);";
            String deleteString = "DELETE FROM %s WHERE createdDatetime <= CAST('%s' AS DATETIME2);";

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            ResultSet rs = statement.executeQuery(String.format(getString, level, forwT));

            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");

            MongoCollection<Document> collection = db.getCollection("statistics_backup");

            ArrayList<Document> persRecords = new ArrayList();
            while(rs.next()){
                Document records = new Document();
                records.put("type", level);

                if(level == "coass_performance" || level == "csms_performance" || level == "cpree_performance"){
                    records.put("method", rs.getString("method"));
                    records.put("status", rs.getString("status"));
                    records.put("latency", rs.getString("response_time"));
                    if (level == "coass_performance"){
                        records.put("identifier", rs.getString("identifier"));
                        records.put("earning", rs.getString("earning"));
                        records.put("cost", rs.getString("cost"));
                        records.put("isdelayed", rs.getBoolean("isDelayed"));
                    }
                }
                else {
                    records.put("item_id", rs.getString("itemId"));
                    records.put("is_hit", rs.getBoolean("isHit"));
                }

                records.put("datetime", rs.getString("createdDatetime"));

                persRecords.add(records);
            }

            statement.executeUpdate(String.format(deleteString, level, forwT));

            if(persRecords.size() > 0)
                collection.insertMany(persRecords);
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
        }
    }

    // Persisting the performance summary in summary()
    private static void persistPerformanceSummary(Document persRecord){
        MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
        MongoDatabase db = mongoClient.getDatabase("coaas_log");
        MongoCollection<Document> collection = db.getCollection("performanceSummary");
        persRecord.put("created", new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(new java.util.Date()));
        collection.insertOne(persRecord);
    }

    // Persisting the current state of the adadptive context caching decision model
    private static void persistModelState(LearnedWeights weights, double cachelife, double delaytime, double avg_gain){
        MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
        MongoDatabase db = mongoClient.getDatabase("coaas_log");
        MongoCollection<Document> collection = db.getCollection("modelState");

        Document persRecord = new Document();

        persRecord.put("threshold", weights.getThreshold());
        persRecord.put("kappa", weights.getKappa());
        persRecord.put("mu", weights.getMu());
        persRecord.put("pi", weights.getPi());
        persRecord.put("delta", weights.getDelta());
        persRecord.put("row", weights.getRow());

        persRecord.put("avg_cachelife", cachelife);
        persRecord.put("avg_delaytime", delaytime);
        persRecord.put("avg_reward", avg_gain);

        persRecord.put("created", new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(new java.util.Date()));
        collection.insertOne(persRecord);
    }

    private static long countIdx = 0;

    /** Aggregation Routines */
    // Summarizes the performance data of the last window and stores in the logs.
    public static SQEMResponse summarize() {
        Document persRecord = new Document();

        try{
            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
            ArrayList<Future<?>> tasks = new ArrayList<>();

            tasks.add(executor.submit(() -> { getCSMSPerfromanceSummary(persRecord); }));
            tasks.add(executor.submit(() -> { getCPREEPerfromanceSummary(persRecord); }));
            tasks.add(executor.submit(() -> { getLevelPerformanceSummary(persRecord); }));
            tasks.add(executor.submit(() -> { getOverallPerfromanceSummary(persRecord); }));
            if(countIdx > 0){
                tasks.add(executor.submit(() -> { profileContextProviders(persRecord); }));
                tasks.add(executor.submit(() -> { profileConsumers(persRecord); }));
                tasks.add(executor.submit(() -> { profileCQClasses(persRecord); }));
                tasks.add(executor.submit(() -> {
                    ContextProfile result = getContextAgeProfile();
                    if(result.getStatus().equals("200")) persRecord.put("avgContextAge",
                            Double.valueOf(result.getTrend()));
                    else persRecord.put("avgContextAge", 0);
                }));
            }
            persRecord.put("cachememory", ContextCacheHandler.getMemoryUtility());

            while(!tasks.isEmpty()){
                Future<?> currTask = tasks.get(0);
                if(currTask.isDone() || currTask.isCancelled()){
                    currTask.get();
                    tasks.remove(0);
                }
            }

            // Backing up the performance data
            tasks.add(executor.submit(() -> { persistPerformanceSummary(persRecord); }));
            // Updating the reinforcement learning model
            if(countIdx > 0){
                tasks.add(executor.submit(() -> {
                    try {
                        JSONObject cachingSummary = getCacheLives();
                        BasicDBObject summary = persRecord.get("summary", BasicDBObject.class);
                        ConcurrentHashMap<String,BasicDBObject> levelSummary = (ConcurrentHashMap<String,BasicDBObject>) persRecord.get("levels");

                        JSONArray vector = new JSONArray();
                        vector.put((double) ContextCacheHandler.getCachePerfStat("cacheUtility") / 1024.0); // Size in cache (in KB)
                        vector.put(summary.getDouble("earning")); // Earnings
                        vector.put(summary.getDouble("retrieval_cost")); // Retrieval Cost
                        vector.put(summary.getDouble("penalty_cost")); // Penalties
                        vector.put((double) ContextCacheHandler.getCachePerfStat("processCost") * 60); // Processing Cost
                        vector.put((double) ContextCacheHandler.getCachePerfStat("cacheCost")); // Cache Cost
                        vector.put(summary.getDouble("no_of_queries") > 0 ?
                                summary.getDouble("delayed_queries")/summary.getDouble("no_of_queries") : 0); // Probability of Delay
                        vector.put(cachingSummary.getDouble("cachelife")); // Average Cache Lifetime (in miliseconds)
                        vector.put(cachingSummary.getDouble("delay")); // Average Delay Time (in miliseconds)

                        if(!popularBased){
                            // TODO: This should be the average of the context cache levels
                            BasicDBObject entitySummary = levelSummary.get("entity");
                            vector.put(entitySummary.getDouble("hitrate"));
                        }

                        double reward = 0.0;
                        if(popularBased){
                            BasicDBObject entitySummary = levelSummary.get("entity");
                            reward = entitySummary.getDouble("hitrate");
                        }
                        else
                            reward = summary.getDouble("avg_gain");;

                        JSONObject learnrequest = getRequestBody(vector, reward);
                        String result = HttpClient.call("http://localhost:9494/selections", HttpRequests.POST, learnrequest.toString());

                        if(result != null){
                            JSONObject actions = new JSONObject(result);
                            JSONArray weights = actions.getJSONArray("actions");
                            LearnedWeights request = LearnedWeights.newBuilder()
                                    .setThreshold(weights.getDouble(5))
                                    .setKappa(weights.getDouble(0))
                                    .setMu(weights.getDouble(1))
                                    .setPi(weights.getDouble(2))
                                    .setDelta(weights.getDouble(3))
                                    .setRow(weights.getDouble(4)).build();

                            CPREEServiceGrpc.CPREEServiceFutureStub asyncStub
                                    = CPREEServiceGrpc.newFutureStub(CPREEChannel.getInstance().getChannel());
                            asyncStub.updateWeights(request);

                            persistModelState(request, cachingSummary.getDouble("cachelife"),
                                    cachingSummary.getDouble("delay"), summary.getDouble("avg_gain"));
                        }
                    }
                    catch(Exception ex){
                        log.info("Error occured when attempting to re-learn the model: " + ex.getMessage());
                    }
                }));
            }
            countIdx++;

            while(!tasks.isEmpty()){
                Future<?> currTask = tasks.get(0);
                if(currTask.isDone() || currTask.isCancelled()){
                    currTask.get();
                    tasks.remove(0);
                }
            }

            executor.shutdown();
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
            return SQEMResponse.newBuilder().setStatus("500").build();
        }

        return SQEMResponse.newBuilder().setStatus("200").build();
    }

    private static JSONObject getRequestBody(JSONArray vector, double avg_gain){
        JSONObject req = new JSONObject();
        req.put("vector", vector);
        req.put("reward", avg_gain);
        return req;
    }

    // Summarizing Context Providers profiles
    private static Runnable profileContextProviders(Document persRecord){
        ConcurrentHashMap<String, BasicDBObject>  finalres = new ConcurrentHashMap<>();
        try {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            ArrayList<Future<?>> tasks = new ArrayList<>();

            tasks.add(executor.submit(() -> { sumCPProfile(finalres); }));
            tasks.add(executor.submit(() -> { sumCPReliability(finalres); }));

            int complete = 0;
            while(!tasks.isEmpty()){
                Future<?> curr = tasks.get(complete);
                if(curr.isDone() || curr.isCancelled()){
                    curr.get();
                    tasks.remove(complete);
                }
            }

            persRecord.put("rawContext", new HashMap<>(finalres));
            executor.shutdown();
        }
        catch (Exception ex){
            log.severe("Exception thrown when aggregating the context providers: "
                    + ex.getMessage());
        }
        return null;
    }

    private static Runnable sumCPReliability(ConcurrentHashMap<String, BasicDBObject>  finalres){
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            HashMap<String, HashMap<String,Double>>  res = new HashMap<>();
            ResultSet rs_1 = statement.executeQuery("SELECT identifier, status, count(status) AS cnt, " +
                    "avg(response_time) AS rt_avg, avg(cost) AS avg_cost, " +
                    "sum(CASE WHEN isDelayed = 1 THEN 1 ELSE 0 END) AS tdelay " +
                    "FROM retrieval_summary GROUP BY identifier, status;");

            while(rs_1.next()){
                Double count = rs_1.getInt("cnt") * 1.0;
                String status = rs_1.getString("status");

                // If the hash map does not contain the CP currently
                if(!res.containsKey(rs_1.getString("identifier"))){
                    res.put(rs_1.getString("identifier"),
                            new HashMap(){{
                                put("count", count);
                                put("delayed", status.equals("200") ? 0.0 : rs_1.getInt("tdelay") * 1.0);
                                put("failed", status.equals("200") ? 0.0 : count);
                                put("success", status.equals("200") ? count : 0.0);
                                put("retLatency", rs_1.getDouble("rt_avg") * count);
                                put("cost", rs_1.getDouble("avg_cost"));
                            }});
                }
                else {
                    HashMap<String,Double> temp = res.get(rs_1.getString("identifier"));

                    temp.put("count", temp.get("count") + count);
                    if(status.equals("200")) {
                        temp.put("success", temp.get("success") + count);
                        temp.put("delayed", temp.get("delayed") + rs_1.getInt("tdelay")*1.0);
                    }
                    else temp.put("failed", temp.get("failed") + count);

                    temp.put("retLatency", temp.get("retLatency") + (rs_1.getDouble("rt_avg") * count));

                    res.put(rs_1.getString("identifier"), temp);
                }
            }

            res.entrySet().parallelStream().forEach((entry) -> {
                String csId = entry.getKey();
                HashMap<String,Double> temp = entry.getValue();

                Double no_success = temp.get("success") == 0 ? 0.000001 : temp.get("success")/1.0;
                // Using all the successful retrievals to divide the total retrieval latency.
                temp.put("retLatency", temp.get("retLatency")/no_success);
                // Using only the successful AND timely retrievals to calculate reliability.
                Double no_success_and_timely = (temp.get("success") - temp.get("delayed"))/1.0;
                temp.put("reliability", temp.get("count") > 0 ? no_success_and_timely/temp.get("count") : 0.0);

                if(!finalres.containsKey(csId)){
                    finalres.put(csId, new BasicDBObject(){{
                        put("reliability", temp);
                    }});
                }
                else {
                    BasicDBObject curr = finalres.get(csId);
                    curr.put("reliability", temp);
                    finalres.put(csId, curr);
                }
            });
        }
        catch(Exception ex){
            log.severe("Exception thrown when measuring the reliability of context providers: "
                    + ex.getMessage());
        }
        return null;
    }

    private static Runnable sumCPProfile(ConcurrentHashMap<String, BasicDBObject>  finalres){
        AtomicDouble missTime = new AtomicDouble();
        AtomicDouble succsessTime = new AtomicDouble();
        AtomicDouble partialMissTime = new AtomicDouble();

        AtomicLong missCnt = new AtomicLong();
        AtomicLong successCnt = new AtomicLong();
        AtomicLong partialMissCnt = new AtomicLong();

        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            HashMap<String, HashMap<String,Double>>  res = new HashMap<>();
            ResultSet rs_1 = statement.executeQuery("SELECT identifier, fthresh, status, " +
                    "count(fthresh) AS cnt, avg(response_time) as rt " +
                    "FROM coass_performance WHERE method = 'cacheSearch' " +
                    "GROUP BY identifier, fthresh, status;");

            while(rs_1.next()){
                if(rs_1.getString("status").equals("200")){
                    Double count = rs_1.getInt("cnt") * 1.0;
                    if(!res.containsKey(rs_1.getString("identifier"))){
                        res.put(rs_1.getString("identifier"),
                                new HashMap(){{
                                    put("count", count);
                                    put("fthresh", rs_1.getDouble("fthresh") * count);
                                }});
                    }
                    else {
                        HashMap<String,Double> temp = res.get(rs_1.getString("identifier"));

                        temp.put("count", temp.get("count") + count);
                        temp.put("fthresh", temp.get("fthresh") + (rs_1.getDouble("fthresh")*count));

                        res.put(rs_1.getString("identifier"), temp);
                    }
                    succsessTime.addAndGet(rs_1.getDouble("rt") * rs_1.getLong("cnt"));
                    successCnt.addAndGet(rs_1.getLong("cnt"));
                }
                else {
                    if(rs_1.getString("status").equals("400")){
                        partialMissTime.addAndGet(rs_1.getDouble("rt") * rs_1.getLong("cnt"));
                        partialMissCnt.addAndGet(rs_1.getLong("cnt"));
                    }
                    else {
                        missTime.addAndGet(rs_1.getDouble("rt") * rs_1.getLong("cnt"));
                        missCnt.addAndGet(rs_1.getLong("cnt"));
                    }
                }
            }

            ContextCacheHandler.updatePerfRegister(successCnt.get() > 0 ? succsessTime.get()/successCnt.get() : 0.0,
                    partialMissCnt.get() > 0 ? partialMissTime.get()/partialMissCnt.get() : 0.0,
                    missCnt.get() > 0 ? missTime.get()/missCnt.get() : 0.0);

            registriesReady = true;
            CQCServiceGrpc.CQCServiceFutureStub stub
                    = CQCServiceGrpc.newFutureStub(CQCChannel.getInstance().getChannel());
            stub.changeRegisterState(RegisterState.newBuilder().setState(registriesReady).build());

            res.entrySet().parallelStream().forEach((entry) -> {
                String csId = entry.getKey();
                HashMap<String,Double> temp = entry.getValue();

                Double count = temp.get("count");
                temp.put("fthresh", count > 0 ? temp.get("fthresh")/count : 0.7);

                if(!finalres.containsKey(csId)){
                    finalres.put(csId, new BasicDBObject(){{
                        put("profile", temp);
                    }});
                }
                else {
                    BasicDBObject curr = finalres.get(csId);
                    curr.put("profile", temp);
                    finalres.put(csId, curr);
                }
            });
        }
        catch(Exception ex){
            log.severe("Exception thrown when summarizing the context providers: "
                    + ex.getMessage());
            log.info(String.valueOf(ex.getStackTrace()));
        }
        return null;
    }


    // Summarizing the performance of each context cache level
    private static Runnable getLevelPerformanceSummary(Document persRecord){
        Map<String, BasicDBObject> level_res = new ConcurrentHashMap<>();
        String query = "SELECT itemId, isHit, count(id) AS cnt, avg(response_time) AS average " +
                "FROM %s GROUP BY isHit, itemId";

        Arrays.stream(LogicalContextLevel.values()).parallel().forEach((lvl) -> {
            level_res.put(lvl.toString().toLowerCase(),
                    new BasicDBObject(getCacheLevelPerfromanceSummary(query,lvl)));
        });

        persRecord.put("levels", level_res);
        return null;
    }

    // Summarizing the performance of the CSMS (Overall)
    private static Runnable getCSMSPerfromanceSummary(Document persRecord){
        HashMap<String, BasicDBObject>  res_1= new HashMap<>();
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            ResultSet rs_1 = statement.executeQuery("SELECT method, status, count(*) AS cnt, avg(response_time) AS average " +
                    "FROM csms_performance GROUP BY method, status;");

            while(rs_1.next()){
                if(res_1.containsKey(rs_1.getString("method"))){
                    res_1.put(rs_1.getString("method"),
                            (BasicDBObject) res_1.get(rs_1.getString("method"))
                                    .put(Utilty.getStatus(rs_1.getString("status")), new BasicDBObject(){{
                                        put("count", rs_1.getInt("cnt"));
                                        put("average", rs_1.getInt("average"));
                                    }}));
                }
                else {
                    res_1.put(rs_1.getString("method"), new BasicDBObject(){{
                        put(Utilty.getStatus(rs_1.getString("status")), new BasicDBObject(){{
                            put("count", rs_1.getInt("cnt"));
                            put("average", rs_1.getInt("average"));
                        }});
                    }});
                }
            }
        }
        catch (Exception ex){
            log.severe("Exception thrown when summarizing CSMS performance data: "
                    + ex.getMessage());
        }

        persRecord.put("csms", res_1);
        return null;
    }

    // Summarizing the performance of the CPREE
    private static Runnable getCPREEPerfromanceSummary(Document persRecord){
        HashMap<String, BasicDBObject>  res_1= new HashMap<>();
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            ResultSet rs_1 = statement.executeQuery("SELECT method, status, count(*) AS cnt, avg(response_time) AS average " +
                    "FROM cpree_performance GROUP BY method, status;");

            while(rs_1.next()){
                if(res_1.containsKey(rs_1.getString("method"))){
                    res_1.put(rs_1.getString("method"),
                            (BasicDBObject) res_1.get(rs_1.getString("method"))
                                    .put(Utilty.getStatus(rs_1.getString("status")), new BasicDBObject(){{
                                        put("count", rs_1.getInt("cnt"));
                                        put("average", rs_1.getInt("average"));
                                    }}));
                }
                else {
                    res_1.put(rs_1.getString("method"), new BasicDBObject(){{
                        put(Utilty.getStatus(rs_1.getString("status")), new BasicDBObject(){{
                            put("count", rs_1.getInt("cnt"));
                            put("average", rs_1.getInt("average"));
                        }});
                    }});
                }
            }
        }
        catch (Exception ex){
            log.severe("Exception thrown when summarizing CPREE performance data: "
                    + ex.getMessage());
        }

        persRecord.put("cpree", res_1);
        return null;
    }

    // Get the processing cost per window
    private static double getProcessingCostPerSecond(){
        // Since the JVM only runs in a single machine (via the IDE), the following line gives the CPU time of the JVM used to run CoaaS.
        return HostMonitor.getCpuUsage() * processPrice;
        // TODO:
        // In Cloud: This method should make a request to the ClodProvider and request the cost of computing for the last window.
        // In IaaS: When deployed in Docker using containers, this should broadcast to all the services and get their individual CPU times.
    }

    // Summarizing the performance of COAAS Overall
    private static Runnable getOverallPerfromanceSummary(Document persRecord){
        HashMap<String, BasicDBObject> res_2 = new HashMap<>();
        BasicDBObject dbo = new BasicDBObject();

        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            ResultSet rs_2 = statement.executeQuery("SELECT method, status, " +
                    "count(id) AS cnt, avg(response_time) AS average, sum(earning) AS tearn, sum(cost) AS tcost, " +
                    "sum(CASE WHEN isDelayed = 1 THEN 1 ELSE 0 END) AS tdelay " +
                    "FROM coass_performance WHERE method != 'cacheSearch' " +
                    "GROUP BY method, status;");

            double totalEarning = 0;
            double totalPenalties = 0;
            double totalRetrievalCost = 0;
            double penaltyEarning = 0;

            long totalQueries = 0;
            long totalRetrievals = 0;

            long queryOverhead = 0;
            long totalNetworkOverhead = 0;

            long delayedResponses = 0;
            long delayedRetrievals = 0;

            while(rs_2.next()){
                String method = rs_2.getString("method");
                String status = rs_2.getString("status");

                if(method.equals("execute")){
                    totalQueries += rs_2.getLong("cnt");
                    queryOverhead += (rs_2.getLong("average")*rs_2.getLong("cnt"));
                    delayedResponses += rs_2.getLong("tdelay");

                    if(status.equals("200")){
                        totalEarning += rs_2.getDouble("tearn");
                        totalPenalties += rs_2.getDouble("tcost");
                    }
                }
                else if(method.equals("executeFetch") || method.equals("FetchJob-execute")){
                    if(status.equals("200")){
                        totalRetrievals += rs_2.getLong("cnt");
                        totalRetrievalCost += rs_2.getDouble("tcost");
                        // penaltyEarning is a special case of an earning which is additional to the CMP.
                        penaltyEarning += rs_2.getDouble("tearn");
                    }
                    delayedRetrievals += rs_2.getLong("tdelay");
                    totalNetworkOverhead += (rs_2.getLong("average")*rs_2.getLong("cnt"));
                }

                if(res_2.containsKey(method)){
                    res_2.put(method,
                            (BasicDBObject) res_2.get(method)
                                    .put(Utilty.getStatus(status), new BasicDBObject(){{
                                        put("count", rs_2.getLong("cnt"));
                                        put("average", rs_2.getLong("average"));
                                    }}));
                }
                else {
                    res_2.put(method, new BasicDBObject(){{
                        put(Utilty.getStatus(status), new BasicDBObject(){{
                            put("count", rs_2.getLong("cnt"));
                            put("average", rs_2.getLong("average"));
                        }});
                    }});
                }
            }

            dbo.put("no_of_queries", totalQueries);
            dbo.put("delayed_queries", delayedResponses);
            dbo.put("no_of_retrievals", totalRetrievals);
            dbo.put("no_of_delayed_retrievals", delayedRetrievals);
            dbo.put("avg_query_overhead", totalQueries > 0 ? queryOverhead / totalQueries : 0);
            dbo.put("avg_network_overhead", totalRetrievals > 0 ? totalNetworkOverhead / totalRetrievals : 0);
            dbo.put("avg_processing_overhead", totalQueries > 0 ? HostMonitor.getCpuUsage() / (double) totalQueries : 0.0);
            // This is a wrong calculation. Need to get theis from the cloud provider.

            double proc_cost = getProcessingCostPerSecond();
            ContextCacheHandler.updatePerfRegister("processCost", proc_cost);
            double cacheCost = (double) ContextCacheHandler.getCachePerfStat("cacheCost");

            // TODO: Should include any other storage costs and other services costs
            double total_costs = totalPenalties + totalRetrievalCost + proc_cost + cacheCost;
            double monetaryGain = totalEarning - total_costs;
            double totalGain = monetaryGain + penaltyEarning;

            dbo.put("gain", monetaryGain);
            dbo.put("avg_gain", totalQueries > 0 ? monetaryGain / totalQueries : 0);
            dbo.put("total_gain", totalGain);
            dbo.put("avg_total_gain", totalQueries > 0 ? totalGain / totalQueries : 0);

            dbo.put("earning", totalEarning);
            dbo.put("cache_cost", cacheCost);
            dbo.put("processing_cost", proc_cost);
            dbo.put("penalty_cost", totalPenalties);
            dbo.put("penalty_earning", penaltyEarning);
            dbo.put("retrieval_cost", totalRetrievalCost);

            ContextCacheHandler.updatePerformanceStats(dbo.toMap(), PerformanceStats.perf_stats);
        }
        catch (Exception ex){
            log.severe("Exception thrown when summarizing COAAS performance data: "
                    + ex.getMessage());
        }

        persRecord.put("coaas", res_2);
        persRecord.put("summary", dbo);
        return null;
    }

    // Summarizing the performance of each individual context cache level
    private static HashMap<String, Object> getCacheLevelPerfromanceSummary(String query, LogicalContextLevel lvl){
        HashMap<String, Object> res_lvl = new HashMap<>();
        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            ResultSet rs_3 = statement.executeQuery(String.format(query, lvl.toString().toLowerCase()));

            long acc_hits = 0;
            long acc_misses = 0;

            double res_avg_hit = 0;
            double res_avg_miss = 0;

            HashMap<String, Object> res_3 = new HashMap<>();
            while(rs_3.next()){
                Boolean isHit = rs_3.getBoolean("isHit");
                long curr_value = rs_3.getLong("cnt");

                if(isHit) {
                    acc_hits += curr_value;
                    res_avg_hit += rs_3.getLong("average");
                } else {
                    acc_misses += curr_value;
                    res_avg_miss += rs_3.getLong("average");
                }

                if(res_3.containsKey(rs_3.getString("itemId"))){
                    String itemId = rs_3.getString("itemId");
                    BasicDBObject cur_Rec = (BasicDBObject) res_3.get(itemId);
                    if(!cur_Rec.containsField(isHit ? "hits" : "misses"))
                        cur_Rec.put(isHit ? "hits" : "misses", 0);

                    long curr_saved_value = cur_Rec.getLong(isHit ? "misses" : "hits");

                    double hitRate = (curr_saved_value + curr_value) > 0 ?
                            (isHit ? Double.valueOf(curr_value) : Double.valueOf(curr_saved_value))/(curr_saved_value + curr_value) : 0;

                    double accessRate = (curr_saved_value + curr_value)/60;

                    cur_Rec.put(isHit?"hits":"misses", curr_value);
                    cur_Rec.put("hitrate", hitRate);
                    cur_Rec.put("accessrate", accessRate);

                    if(Event.operation.checkChannel(itemId)){
                        Message message = new Message(itemId, accessRate);
                        Event.operation.publish(itemId, message);
                    }

                    res_3.put(itemId,cur_Rec);
                }
                else {
                    BasicDBObject newObj = new BasicDBObject();
                    newObj.put(isHit?"hits":"misses", curr_value);
                    newObj.put("hitrate", isHit? 1.0 : 0.0);
                    newObj.put("id", rs_3.getString("itemId"));
                    res_3.put(rs_3.getString("itemId"), newObj);
                }
            }

            res_lvl.put("items", res_3.values());
            double hitrate = (acc_hits + acc_misses) > 0 ? Double.valueOf(acc_hits)/(acc_hits + acc_misses) : 0;
            res_lvl.put("hitrate", hitrate);
            res_lvl.put("hit_response_time", acc_hits > 0 ? res_avg_hit/acc_hits : 0);
            res_lvl.put("miss_response_time", acc_misses > 0 ? res_avg_miss/acc_misses : 0);
        }
        catch(Exception ex){
            log.severe("Exception thrown when summarizing " + lvl.toString() +" performance data: "
                    + ex.getMessage());
        }
        return res_lvl;
    }

    // Summarizing the Context Consumers interactions with the CMP
    private static Runnable profileConsumers(Document persRecord){
        HashMap<String, BasicDBObject>  res = new HashMap<>();
        HashMap<String, Double> expected = new HashMap<>();

        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            ResultSet rs_1 = statement.executeQuery("SELECT consumerId, count(*) AS cnt, avg(fthresh) AS avg_freshness, " +
                    "avg(earning) AS avg_earning, avg(rtmax) AS avg_rtmax, avg(penalty) AS avg_penalty " +
                    "FROM consumer_slas GROUP BY consumerId;");

            double ex_fth = 0;
            double ex_ern = 0;
            double ex_rtm = 0;
            double ex_pen = 0;
            long total_queries = 0;

            while(rs_1.next()){
                if(!res.containsKey(rs_1.getString("consumerId"))){
                    int count = rs_1.getInt("cnt");
                    total_queries += count;

                    ex_rtm += (count*rs_1.getDouble("avg_rtmax"));
                    ex_pen += (count*rs_1.getDouble("avg_penalty"));
                    ex_ern += (count*rs_1.getDouble("avg_earning"));
                    ex_fth += (count*rs_1.getDouble("avg_freshness"));

                    res.put(rs_1.getString("consumerId"),
                            new BasicDBObject(){{
                                put("count", count);
                                put("rtmax", rs_1.getDouble("avg_rtmax"));
                                put("penalty", rs_1.getDouble("avg_penalty"));
                                put("earning", rs_1.getDouble("avg_earning"));
                                put("fthresh", rs_1.getDouble("avg_freshness"));
                            }});
                }
            }

            expected.put("exp_pen", ex_pen/total_queries);
            expected.put("exp_fth", ex_fth/total_queries);
            expected.put("exp_earn", ex_ern/total_queries);
            expected.put("exp_rtmax", ex_rtm/total_queries);
        }
        catch (Exception ex){
            log.severe("Exception thrown when aggregating the context consumers: "
                    + ex.getMessage());
        }

        persRecord.put("consumers", res);
        persRecord.put("expectedSLA", expected);
        slahistory.add(expected);

        return null;
    }

    // Summarizing and profiling each Context Query Classes
    private static Runnable profileCQClasses(Document persRecord){
        ConcurrentHashMap<String, BasicDBObject>  finalres = new ConcurrentHashMap<>();

        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            HashMap<String, HashMap<String,Double>>  res = new HashMap<>();
            ResultSet rs_1 = statement.executeQuery("SELECT queryClass, count(*) AS cnt, avg(fthresh) AS avg_freshness, " +
                    "avg(earning) AS avg_earning, avg(rtmax) AS avg_rtmax, avg(penalty) AS avg_penalty " +
                    "FROM consumer_slas GROUP BY queryClass, consumerId;");

            while(rs_1.next()){
                Double count = rs_1.getInt("cnt") * 1.0;
                if(!res.containsKey(rs_1.getString("queryClass"))){
                    res.put(rs_1.getString("queryClass"),
                            new HashMap(){{
                                put("count", count);
                                put("rtmax", rs_1.getDouble("avg_rtmax")*count);
                                put("earning", rs_1.getDouble("avg_earning")*count);
                                put("penalty", rs_1.getDouble("avg_penalty")*count);
                                put("fthresh", rs_1.getDouble("avg_freshness")*count);
                            }});
                }
                else {
                    HashMap<String,Double> temp = res.get(rs_1.getString("queryClass"));

                    temp.put("count", temp.get("count") + count);
                    temp.put("rtmax", temp.get("rtmax") + (rs_1.getDouble("avg_rtmax")*count));
                    temp.put("earning", temp.get("earning") + (rs_1.getDouble("avg_earning")*count));
                    temp.put("penalty", temp.get("penalty") + (rs_1.getDouble("avg_penalty")*count));
                    temp.put("fthresh", temp.get("fthresh") + (rs_1.getDouble("avg_freshness")*count));

                    res.put(rs_1.getString("queryClass"), temp);
                }
            }

            res.entrySet().parallelStream().forEach((entry) -> {
                String classId = entry.getKey();
                HashMap<String,Double> temp = entry.getValue();

                Double count = temp.get("count");

                temp.put("rtmax", temp.get("rtmax")/count);
                temp.put("earning", temp.get("earning")/count);
                temp.put("penalty", temp.get("penalty")/count);
                temp.put("fthresh", temp.get("fthresh")/count);

                finalres.put(classId, new BasicDBObject(temp));
                // TODO: Should update the context class tree with this data
            });
        }
        catch (Exception ex){
            log.severe("Exception thrown when aggregating the context query classes: "
                    + ex.getMessage());
        }

        persRecord.put("classes", finalres);
        return null;
    }

    /** Performance Data Retrieval */
    // Returns the current hit rate of a context item
    public static double getHitRate(LogicalContextLevel level, String id, int duration) {
        String queryString = "SELECT SUM(isHit)/COUNT(*) AS hitrate" +
                "FROM %s WHERE itemId = '%s' AND createdDatetime >= CAST('%s' AS DATETIME2);";
        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            LocalDateTime windowThresh = LocalDateTime.now().minusSeconds(duration);
            String forwT = windowThresh.format(formatter);

            ResultSet rs = statement.executeQuery(String.format(queryString,
                    level.toString().toLowerCase(), id, forwT));

            // Returns the actual hit rate as recorded
            return rs.getDouble("hitrate");
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }

        // Returns HR = 0 since there are no performance records
        return 0;
    }

    // Context Service Performance for retrievals
    public static double getLastRetrievalTime(String csId, String hashKey){
        String queryString = "SELECT TOP 1 response_time, age " +
                "FROM coass_performance WHERE status = '200' AND identifier = '%s' AND hashKey = '%s' ORDER BY id DESC;";
        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            csId = csId.startsWith("{") ? (new JSONObject(csId)).getString("$oid") : csId;
            String finalString = String.format(queryString, csId, hashKey);

            ResultSet rs = statement.executeQuery(finalString);

            if(!rs.next()) {
                SQEMResponse avg_latency = ContextCacheHandler.getPerformanceStats("avg_network_overhead", PerformanceStats.perf_stats);
                if(avg_latency.getStatus().equals("200"))
                    return Double.valueOf(avg_latency.getBody())/1000;

                return 0;
            }

            double retrieval_latency = rs.getDouble("response_time")/1000;
            long age = rs.getLong("age");

            // This is in seconds
            return retrieval_latency+age;
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }

        // We can ignore the retrieval latency of context services which has retrievals earlier than a window size.
        // That is because, as the lifetime grows larger in value, network latency can be ignored.
        return 0;
    }

    // Retrieves the current summary of performance
    public static SQEMResponse getCurrentPerformanceSummary(){
        try{
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");

            MongoCollection<Document> collection = db.getCollection("performanceSummary");

            Document sort = new Document();
            sort.put("_id",-1); // Auto generated _id embeds the timestamp. So, ordering from newest to oldest
            Document data = collection.find().sort(sort).first();

            return SQEMResponse.newBuilder().setStatus("200")
                    .setBody(data.toJson()).build();
        }
        catch(Exception e){
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    // Retrieves the variation of the model state
    public static SQEMResponse getModelStateVariation(){
        try{
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");

            MongoCollection<Document> collection = db.getCollection("modelState");

            Document sort = new Document();
            sort.put("_id",-1); // Auto generated _id embeds the timestamp. So, ordering from newest to oldest
            String data = collection.find().sort(sort).first().toJson();

            return SQEMResponse.newBuilder().setStatus("200")
                    .setBody(data).build();
        }
        catch(Exception e){
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
        }
    }

    // TODO: If the Context Provider is popular, this statistic should also be cached.
    // Retrieves the summary of context provider's access profile to cache
    public static ContextProviderProfile getContextProviderProfile(String cpId, String hashKey){
        try{
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");

            MongoCollection<Document> collection = db.getCollection("performanceSummary");

            // Not defining any conditions in the find because there is a need to get the whole sequence
            // retrievals in each window which is not guranteed in each.

            Document sort = new Document();
            sort.put("_id",-1); // Auto generated _id embeds the timestamp. So, ordering from newest to oldest

            Document project = new Document();
            project.put("rawContext." + cpId, 1);

            // Document filter = new Document();
            // filter.put("rawContext."+cpId, new Document(){{ put("$exists", true); }});

            // Search the performance DB
            // FindIterable<Document> result = collection.find(filter).projection(project).sort(sort).limit(max_history);
            FindIterable<Document> result = collection.find().sort(sort).limit(max_history).projection(project);

            AtomicReference<Double> exp_ar = new AtomicReference<>(Double.NaN);
            AtomicReference<Double> exp_fthr = new AtomicReference<>(Double.NaN);
            AtomicReference<Double> exp_cost = new AtomicReference<>(Double.NaN);
            AtomicReference<Double> exp_retLatency = new AtomicReference<>(Double.NaN);
            AtomicReference<Double> var_ratLatency = new AtomicReference<>(Double.NaN);
            AtomicReference<Double> exp_reliability = new AtomicReference<>(Double.NaN);
            AtomicReference<SimpleRegression> exp_count = new AtomicReference<>(null);

            int count = Iterables.size(result);
            double lastfthr = 0.5; // Default

            if(count > 1 ) {
                int index = 0;

                List<double[]> dataset = new ArrayList<>();
                List<double[]> dataset_2 = new ArrayList<>();
                List<double[]> dataset_3 = new ArrayList<>();
                List<double[]> dataset_4 = new ArrayList<>();
                List<double[]> dataset_5 = new ArrayList<>();

                double[] retLatList = new double[count];
                List<Double> counts = new ArrayList();

                for(Document document : result){
                    // Expected freshness threshold, reliability, and retrieval latency
                    Document rCon = document.get("rawContext", Document.class);
                    if(rCon != null && rCon.containsKey(cpId)){
                        Document cp = rCon.get(cpId, Document.class);
                        Document rel = cp.get("reliability", Document.class);

                        if(rel != null){
                            dataset.add(new double[] { index, cp.containsKey("profile") ?
                                    cp.get("profile", Document.class).getDouble("fthresh") : 0.7 });
                            dataset_2.add(new double[] { index, rel.getDouble("reliability") });
                            dataset_3.add(new double[] { index, rel.getDouble("retLatency") });
                            dataset_5.add(new double[] { index, rel.getDouble("cost") });

                            retLatList[index] = rel.getDouble("retLatency");
                            counts.add(rel.getDouble("count")/60);

                            index ++;
                        }
                    }
                    else {
                        counts.add(0.0);
                    }
                }

                index = 0;
                Collections.reverse(counts);
                for(Double cnt: counts){
                    dataset_4.add(new double[] { index, cnt });
//                    dataset_4[index][0] = index;
//                    dataset_4[index][1] = cnt;
                    index++;
                }

                ExecutorService estimation_executor = Executors.newFixedThreadPool(8);
                ArrayList<Future<?>> taskList = new ArrayList<>();

                // The list is inverted, so, estimating from x=-1.
                int finalIndex = index;
                taskList.add(estimation_executor.submit(() -> { exp_count.set(getSlope(dataset_4.stream().toArray(double[][]::new))); }));
                taskList.add(estimation_executor.submit(() -> { var_ratLatency.set(getVariance(retLatList)); }));
                taskList.add(estimation_executor.submit(() -> { exp_fthr.set(predictExpectedValue(dataset.stream().toArray(double[][]::new), -1)); }));
                taskList.add(estimation_executor.submit(() -> { exp_cost.set(predictExpectedValue(dataset_5.stream().toArray(double[][]::new), -1)); }));
                taskList.add(estimation_executor.submit(() -> { exp_retLatency.set(predictExpectedValue(dataset_3.stream().toArray(double[][]::new), -1)); }));
                taskList.add(estimation_executor.submit(() -> { exp_reliability.set(predictExpectedValue(dataset_2.stream().toArray(double[][]::new), -1)); }));
                taskList.add(estimation_executor.submit(() -> { exp_ar.set(predictExpectedValue(dataset_4.stream().toArray(double[][]::new), finalIndex)); }));

                while(!taskList.isEmpty()){
                    Future<?> curr = taskList.get(0);
                    if(curr.isDone() || curr.isCancelled()){
                        curr.get();
                        taskList.remove(0);
                    }
                }
                estimation_executor.shutdown();
            }
            else if (count == 1) {
                Document cp = result.first().get("rawContext", Document.class).get(cpId, Document.class);
                lastfthr = cp.get("profile", Document.class).getDouble("fthresh");
            }

            return ContextProviderProfile.newBuilder().setStatus("200")
                    .setExpFthr(Double.isNaN(exp_fthr.get()) ? "NaN" : (exp_fthr.get() < 0 || exp_fthr.get() > 1 ?
                            Double.toString(lastfthr) : Double.toString(exp_fthr.get())))
                    .setRelaibility(Double.isNaN(exp_reliability.get()) ? "NaN" : exp_retLatency.get() < 0 ? "0" :
                            exp_retLatency.get() > 1 ? "1" : Double.toString(exp_reliability.get()))
                    .setExpRetLatency(Double.toString(exp_retLatency.get()/1000)) // In seconds
                    .setAccessTrend(exp_count.get() == null ? "NaN" : Double.toString(exp_count.get().getSlope()))
                    .setArRegression(exp_count.get() == null ? "NaN" : Double.toString(exp_count.get().getRSquare()))
                    .setArIntercept(exp_count.get() == null ? "NaN" : Double.toString(exp_count.get().getIntercept()))
                    .setExpCost(Double.isNaN(exp_cost.get()) ? "NaN" : Double.toString(exp_cost.get()))
                    .setExpAR(Double.isNaN(exp_ar.get()) ? "NaN" : Double.toString(exp_ar.get()/60)) // This is because the window is 60s
                    .setRetVariance(var_ratLatency.get())
                    .setLastRetLatency(getLastRetrievalTime(cpId, hashKey)) // In seconds already
                    .build();
        }
        catch(Exception e){
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return ContextProviderProfile.newBuilder().setStatus("500").build();
        }
    }

    public static ContextProfile getContextProfile(String contextId, int limit) {
        String queryString = "SELECT time_bucket_gapfill('1 minutes', time) AS bucket, COUNT(context_id) AS cnt\n " +
                " FROM context_access\n" +
                " WHERE context_id = '%s' AND (time BETWEEN '%s' AND '%s')\n" +
                " GROUP BY bucket \n" +
                " ORDER BY bucket LIMIT %d";
        Connection conn = au.coaas.sqem.timescale.ConnectionPool.getInstance().getTSConnection();
        try{
            Statement statement = conn.createStatement();
            int fixedLimit = limit <= 0 ? 10 : limit;
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime history = now.minusSeconds(fixedLimit * 60);
            ResultSet rs = statement.executeQuery(String.format(queryString, contextId,
                    Timestamp.valueOf(history).toString(), Timestamp.valueOf(now).toString(),
                    fixedLimit));
            ContextProfile.Builder accesses = ContextProfile.newBuilder();

            int index = 0;
            int accessCnt = 0;
            double[][] dataset = new double[fixedLimit][2];
            
            while(rs.next()){
                int acs = rs.getInt("cnt");
                accessCnt += acs;
                dataset[index][0] = index;
                dataset[index][1] = acs/60.0;
                index++;
            }

            SimpleRegression result = getSlope(dataset);
            if(result != null){
                return accesses
                        .addAccess(accessCnt)
                        .setTrend(String.valueOf(result.getSlope()))
                        .setIntercept(String.valueOf(result.getIntercept()))
                        .setExpAR(String.valueOf(result.predict(index)))
                        .setStatus("200").build();
            }
            return ContextProfile.newBuilder()
                    .setTrend("NaN").setIntercept("NaN").setExpAR("NaN")
                    .setStatus("500").build();
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
            return ContextProfile.newBuilder()
                    .setTrend("NaN").setIntercept("NaN").setExpAR("NaN")
                    .setStatus("500").build();
        }
    }

    public static ContextProfile getContextAgeProfile() {
        String queryString = "SELECT AVG(age) AS avgAge, COUNT(*) AS avgCnt\n " +
                " FROM context_access\n" +
                " WHERE (time BETWEEN '%s' AND '%s') AND outcome = 'hit'";
        Connection conn = au.coaas.sqem.timescale.ConnectionPool.getInstance().getTSConnection();
        try {
            Statement statement = conn.createStatement();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime history = now.minusSeconds(60);
            ResultSet rs = statement.executeQuery(String.format(queryString,
                    Timestamp.valueOf(history).toString(),
                    Timestamp.valueOf(now).toString()));

            AbstractMap.SimpleEntry<Double, Double> fetcedhAge = getFetchedContextAge();
            if(rs.next()){
                Double avgAge = ((fetcedhAge.getKey() * fetcedhAge.getValue()) +
                        (rs.getDouble("avgAge") * rs.getDouble("avgCnt"))) /
                        (fetcedhAge.getValue() + rs.getDouble("avgCnt"));
                return ContextProfile.newBuilder()
                        .setTrend(String.valueOf(avgAge))
                        .setStatus("200").build();
            }
            else {
                return ContextProfile.newBuilder()
                        .setTrend(String.valueOf(fetcedhAge.getKey()))
                        .setStatus("200").build();
            }
        }
        catch(SQLException ex) {
            log.severe(ex.getMessage());
            return ContextProfile.newBuilder()
                    .setTrend("NaN")
                    .setStatus("500").build();
        }
    }

    private static AbstractMap.SimpleEntry<Double,Double> getFetchedContextAge() throws SQLException {
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);

        HashMap<String, HashMap<String,Double>>  res = new HashMap<>();
        ResultSet rs_1 = statement.executeQuery("SELECT avg(age) AS avg_age, count(*) AS cnt " +
                "FROM coass_performance WHERE method = 'executeFetch' or method = 'FetchJob-execute';");
        if(rs_1.next()){
            return new AbstractMap.SimpleEntry<>(rs_1.getDouble("avg_age"),
                    rs_1.getDouble("cnt"));
        }
        return new AbstractMap.SimpleEntry<>(0.0,0.0);
    }


    public static QueryClassProfile getQueryClassProfile(String classId){
        try{
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");

            MongoCollection<Document> collection = db.getCollection("performanceSummary");

            Document sort = new Document();
            sort.put("_id",-1); // Auto generated _id embeds the timestamp. So, ordering from newest to oldest

            Document project = new Document();
            project.put("classes."+classId,1);

            Document filter = new Document();
            filter.put("classes."+classId, new Document(){{ put("$exists", true); }});

            // Search the performance DB
            FindIterable<Document> result = collection.find(filter).projection(project).sort(sort).limit(max_history);

            AtomicReference<Double> exp_rtmax = new AtomicReference<>(Double.NaN);
            AtomicReference<Double> exp_earning = new AtomicReference<>(Double.NaN);
            AtomicReference<Double> exp_penalty = new AtomicReference<>(Double.NaN);

            int count = Iterables.size(result);

            if(count > 1){
                int index = 0;
                List<double[]> dataset = new ArrayList<>();
                List<double[]> dataset_2 = new ArrayList<>();
                List<double[]> dataset_3 = new ArrayList<>();

                for(Document document : result){
                    // Expected rtmax, earning, penalty
                    Document cqc = document.get("classes", Document.class).get(classId, Document.class);
                    if(cqc != null){
                        dataset.add(new double[] { index, cqc.getDouble("rtmax") });
                        dataset_2.add(new double[] { index, cqc.getDouble("earning") });
                        dataset_3.add(new double[] { index, cqc.getDouble("penalty") });

                        index++;
                    }
                };

                ExecutorService regression_executor = Executors.newFixedThreadPool(6);
                ArrayList<Future<?>> taskList = new ArrayList<>();

                // The list is inverted, so, estimating from x=-1.
                taskList.add(regression_executor.submit(() -> { exp_rtmax.set(predictExpectedValue(dataset.stream().toArray(double[][]::new), -1)); }));
                taskList.add(regression_executor.submit(() -> { exp_earning.set(predictExpectedValue(dataset_2.stream().toArray(double[][]::new), -1)); }));
                taskList.add(regression_executor.submit(() -> { exp_penalty.set(predictExpectedValue(dataset_3.stream().toArray(double[][]::new), -1)); }));

                while(!taskList.isEmpty()){
                    Future<?> curr = taskList.get(0);
                    if(curr.isDone() || curr.isCancelled()){
                        curr.get();
                        taskList.remove(0);
                    }
                }

                regression_executor.shutdown();
            }

            double rtmaxValid = exp_rtmax.get() < 0 ? 0 :  exp_rtmax.get();
            double earnValid = exp_earning.get() < 0 ? 0 :  exp_earning.get();
            double penaltyValid = exp_penalty.get() < 0 ? 0 :  exp_penalty.get();

            return QueryClassProfile.newBuilder().setStatus("200")
                    .setRtmax(Double.isNaN(exp_rtmax.get()) ? "NaN" : Double.toString(rtmaxValid/1000))
                    .setEarning(Double.isNaN(exp_earning.get()) ? "NaN" : Double.toString(earnValid))
                    .setPenalty(Double.isNaN(exp_penalty.get()) ? "NaN" : Double.toString(penaltyValid))
                    .build();
        }
        catch(Exception e){
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return QueryClassProfile.newBuilder().setStatus("500").build();
        }
    }

    private static final int distribution_history = 1000;

    public static ProbDelay getProbabilityOfDelay(ProbDelayRequest request) {
        try {
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");

            MongoCollection<Document> collection = db.getCollection("performanceSummary");

            Document sort = new Document();
            sort.put("_id",-1); // Auto generated _id embeds the timestamp. So, ordering from newest to oldest

            Document project = new Document();
            project.put("rawContext." + request.getPrimaryKey() + ".reliability", 1);

            Document filter = new Document();
            filter.put("rawContext." + request.getPrimaryKey(), new Document(){{ put("$exists", true); }});

            // Search the performance DB
            FindIterable<Document> result = collection.find(filter).projection(project).sort(sort).limit(distribution_history);

            AtomicInteger count = new AtomicInteger();
            AtomicInteger total = new AtomicInteger(Iterables.size(result));
            double rtmax = request.getThreshold() * 1000; // Converting to miliseconds

            result.forEach((Block<Document>) document -> {
                Document rel = document.get("rawContext", Document.class)
                                .get(request.getPrimaryKey(), Document.class)
                                .get("reliability", Document.class);
                if(rel != null){
                    if(rel.getDouble("retLatency") >=  rtmax)
                        count.getAndIncrement();
                }
                else total.addAndGet(-1);
            });

            if(count.get()==0)  return ProbDelay.newBuilder().setValue(0.0).build();
            return ProbDelay.newBuilder()
                    .setValue(total.get() > 0 ? count.get()/ total.get() : 1.0)
                    .build();
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
        }

        return ProbDelay.newBuilder().setValue(0.0).build();
    }

    // Retrieve the decision history of the estimated life and delay times
    private static JSONObject getCacheLives() throws Exception{
        String queryString = "SELECT type, horizon, sum(latency) as sum_lat, count(horizon) as cnt_hor " +
                "FROM cacheHistoryRegistry " +
                "WHERE createdDatetime >= CAST('%s' AS DATETIME2) AND createdDatetime < CAST('%s' AS DATETIME2) " +
                "GROUP BY type, horizon;";
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusSeconds(300);

        String fnlString = String.format(queryString,start.format(formatter), end.format(formatter));
        ResultSet rs = statement.executeQuery(fnlString);

        long cacheSum = 0;
        long cacheDefCount = 0;

        long delaySum = 0;
        long delayDefCount = 0;

        while(rs.next()){
            if(rs.getString("type").equals("cachelife")){
                cacheSum += rs.getLong("sum_lat");
                if(rs.getString("horizon").equals("definite")){
                    cacheDefCount += rs.getLong("cnt_hor");
                }
            }
            else {
                delaySum += rs.getLong("sum_lat");
                if(rs.getString("horizon").equals("definite")){
                    delayDefCount += rs.getLong("cnt_hor");
                }
            }
        }

        JSONObject body = new JSONObject();
        body.put("cachelife", cacheDefCount > 0 ? cacheSum/cacheDefCount : 0.0);
        body.put("delay", delayDefCount > 0 ? delaySum/delayDefCount : 0.0);

        return body;
    }

    public static SQEMResponse getCacheLifeSummary(){

        try{
            JSONObject result = getCacheLives();
            return SQEMResponse.newBuilder().setStatus("200")
                    .setBody(result.toString()).build();
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
            JSONObject body = new JSONObject();
            body.put("message",ex.getMessage());
            body.put("cause",ex.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500")
                    .setBody(body.toString()).build();
        }
    }

    /** Initializing performance data monitoring */
    // Setting up the databases to store and persist the performance logs
    private static void getPerfDBConnection() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        String connectionString = "jdbc:sqlserver://localhost:1433;database=coassPerformance;" +
                "user=sa;password=coaas@PerfDB2k22;encrypt=true;trustServerCertificate=true";
        connection = DriverManager.getConnection(connectionString);
    }

    public static void seed_performance_db(){
        try{
            all_tables.add("coass_performance");
            all_tables.add("csms_performance");
            all_tables.add("cpree_performance");
            all_tables.add("consumer_slas");

            getPerfDBConnection();

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // Create the database tables if not existing
            statement.execute(
                    "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='cacheHistoryRegistry')\n" +
                            "CREATE TABLE cacheHistoryRegistry(\n" +
                            "    id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,\n" +
                            "    type VARCHAR(15) NOT NULL,\n" +
                            "    latency BIGINT NOT NULL,\n" +
                            "    createdDatetime DATETIME NOT NULL,\n" +
                            "    horizon VARCHAR(10) NOT NULL)");

            statement.execute(
                    "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='csms_performance')\n" +
                            "CREATE TABLE csms_performance(\n" +
                            "    id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,\n" +
                            "    method VARCHAR(255) NOT NULL,\n" +
                            "    status VARCHAR(5) NOT NULL,\n" +
                            "    response_time BIGINT NOT NULL,\n" +
                            "    createdDatetime DATETIME NOT NULL)");

            statement.execute("IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='coass_performance')\n" +
                    "CREATE TABLE coass_performance(\n" +
                    "    id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,\n" +
                    "    method VARCHAR(255) NOT NULL,\n" +
                    "    status VARCHAR(5) NOT NULL,\n" +
                    "    response_time BIGINT NULL,\n" +
                    "    earning REAL NULL,\n" +
                    "    cost REAL NULL,\n" +
                    "    identifier VARCHAR(MAX) NOT NULL,\n" +
                    "    hashKey VARCHAR(MAX) NULL,\n" +
                    "    createdDatetime DATETIME NOT NULL,\n" +
                    "    isDelayed BIT NOT NULL,\n" +
                    "    age BIGINT NULL,\n" +
                    "    fthresh REAL NULL)");

            statement.execute("IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='retrieval_summary')\n" +
                    "CREATE TABLE retrieval_summary(\n" +
                    "    id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,\n" +
                    "    status VARCHAR(5) NOT NULL,\n" +
                    "    response_time BIGINT NULL,\n" +
                    "    cost REAL NULL,\n" +
                    "    identifier VARCHAR(MAX) NOT NULL,\n" +
                    "    createdDatetime DATETIME NOT NULL,\n" +
                    "    isDelayed BIT NOT NULL)");

            statement.execute("IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='cpree_performance')\n" +
                    "CREATE TABLE cpree_performance(\n" +
                    "    id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,\n" +
                    "    method VARCHAR(255) NOT NULL,\n" +
                    "    status VARCHAR(5) NOT NULL,\n" +
                    "    response_time BIGINT NULL,\n" +
                    "    createdDatetime DATETIME NOT NULL)");

            for(LogicalContextLevel level : LogicalContextLevel.values()){
                statement.execute(String.format("IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='%s')\n" +
                                "CREATE TABLE %s(\n" +
                                "    id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,\n" +
                                "    itemId VARCHAR(MAX) NOT NULL,\n" +
                                "    isHit BIT NOT NULL,\n" +
                                "    response_time BIGINT NOT NULL,\n" +
                                "    createdDatetime DATETIME NOT NULL)",
                        level.toString().toLowerCase(), level.toString().toLowerCase()));
            }

            statement.execute("IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='consumer_slas')\n" +
                    "CREATE TABLE consumer_slas(\n" +
                    "    consumerId VARCHAR(MAX) NOT NULL,\n" +
                    "    fthresh REAL NOT NULL,\n" +
                    "    earning REAL NOT NULL,\n" +
                    "    rtmax BIGINT NOT NULL,\n" +
                    "    penalty REAL NOT NULL,\n" +
                    "    queryId VARCHAR(MAX) NULL,\n" +
                    "    queryClass BIGINT NOT NULL,\n" +
                    "    createdDatetime DATETIME NOT NULL)");

            statement.execute("IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='cacheDecisionHistory')\n" +
                    "CREATE TABLE cacheDecisionHistory(" +
                    "   id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,\n" +
                    "   retEff REAL NOT NULL,\n" +
                    "   cacheEff REAL NOT NULL,\n" +
                    "   reliability REAL NOT NULL,\n" +
                    "   complexity REAL NOT NULL,\n" +
                    "   accessTrend REAL NOT NULL,\n" +
                    "   normalizer REAL NOT NULL,\n" +
                    "   kappa REAL NOT NULL,\n" +
                    "   mu REAL NOT NULL,\n" +
                    "   delta REAL NOT NULL,\n" +
                    "   row REAL NOT NULL,\n" +
                    "   pi REAL NOT NULL,\n" +
                    "   threshold DECIMAL NOT NULL,\n" +
                    "   isDefinite BIT NOT NULL,\n" +
                    "   level VARCHAR(20) NOT NULL,\n" +
                    "   decision VARCHAR(20) NOT NULL,\n" +
                    "   latency REAL NOT NULL,\n" +
                    "   decisionTime DATETIME NOT NULL)");
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
        }
    }

    public static void seed_timeseries_db(){
        try{
            Connection conn = au.coaas.sqem.timescale.ConnectionPool.getInstance().getTSConnection();
            Statement statement = conn.createStatement();
            statement.execute("CREATE TABLE context_access(\n" +
                    "    time TIMESTAMPTZ NOT NULL," +
                    "    context_id TEXT," +
                    "    outcome VARCHAR(10)," +
                    "    age DOUBLE PRECISION)");
            statement.execute("SELECT create_hypertable('context_access', 'time');");
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
        }
    }
}
