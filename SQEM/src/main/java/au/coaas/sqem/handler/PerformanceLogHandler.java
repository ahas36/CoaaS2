package au.coaas.sqem.handler;

import au.coaas.sqem.monitor.LogicalContextLevel;
import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.Statistic;
import au.coaas.sqem.proto.SummarySLA;
import au.coaas.sqem.util.LimitedQueue;
import au.coaas.sqem.util.Utilty;

import au.coaas.sqem.util.enums.PerformanceStats;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONObject;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import static au.coaas.sqem.util.StatisticalUtils.predictExpectedValue;

public class PerformanceLogHandler {

    private static Connection connection = null;
    private static final Logger log = Logger.getLogger(LogHandler.class.getName());
    private static List<String> all_tables = Stream.of(LogicalContextLevel.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private static final int max_history = 10;

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
        String queryString = "INSERT INTO %s(itemId,isHit,response_time,createdDatetime) VALUES('%s', %d, %d, GETDATE());";
        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(String.format(queryString, level.toString().toLowerCase(),
                    id, isHit?1:0, rTime));
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
    }

    // Recording teh recent performance of CSMS
    public static void genericRecord(String method, String status, long rTime) {
        String queryString = "INSERT INTO csms_performance(method,status,response_time,createdDatetime) "
                + "VALUES('%s', '%s', %d, GETDATE());";
        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(String.format(queryString, method, status, rTime));
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
    }

    // Recording for profiling context consumers
    public static void consumerRecord(SummarySLA conSummary) {
        String queryString = "INSERT INTO consumer_slas "
                + "VALUES('%s', '%d', %d, %d, %d, %s, %d, GETDATE());";
        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(String.format(queryString,
                    conSummary.getCsid(), conSummary.getFthresh(), conSummary.getEarning(),
                    conSummary.getRtmax(), conSummary.getPenalty(), conSummary.getQueryId(),
                    conSummary.getQueryClass()));
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
    }

    // COASS Performance
    public static void coassPerformanceRecord(Statistic request) {
        String queryString = "INSERT INTO coass_performance(method,status,response_time,earning,"+
                "cost,identifier,hashKey,createdDatetime,isDelayed,age,fthresh) " +
                "VALUES('%s', '%s', %d, %f, %f, '%s', '%s', GETDATE(), %d, %d, %d);";
        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String method = request.getMethod();
            switch(method){
                case "execute" :{
                    // Value at the Identifier column here is the Query ID
                    String formatted_string = String.format(queryString,
                            method, // Method name
                            request.getStatus(), // Status of the request
                            request.getTime(), // Response time
                            request.getEarning(), // Earnings from the query
                            request.getCost(), // Cost from query
                            request.getIdentifier(), // Context Service Identifier
                            "NULL", // Hashkey of the cached item
                            request.getIsDelayed() ? 1 : 0, // is Delayed?
                            0, "NULL"); // age, fthresh
                    statement.executeUpdate(formatted_string);
                    break;
                }
                case "executeFetch":
                case "executeStreamRead":{
                    String hashKey = Utilty.getHashKey(request.getCs().getParamsMap());

                    JSONObject cs = new JSONObject(request.getCs().getContextService().getJson());
                    String cs_id = cs.getJSONObject("_id").getString("$oid");

                    // Value at the Identifier column here is the Context Service ID
                    String formatted_string = String.format(queryString,
                            method, // Method name
                            request.getStatus(), // Status of the retrieval
                            request.getTime(), // Response time
                            0, // Earnings from the retrieval (which is always 0)
                            request.getCost(), // Cost of retrieval
                            cs_id, // Context Service Identifier
                            hashKey, // Hashkey (cached or not cached)
                            0, // is Delayed?
                            request.getAge(), //age
                            cs.getJSONObject("sla").getJSONObject("freshness").getDouble("fthresh")); // fthresh
                    statement.executeUpdate(formatted_string);
                    break;
                }
                case "cacheSearch": {
                    String formatted_string = String.format(queryString,
                            method, // Method name
                            request.getStatus(), // Status of the request
                            "NULL", // Response time
                            "NULL", // Earnings from the query
                            "NULL", // Cost from query
                            request.getIdentifier(), // Context Service Identifier
                            "NULL", // Hashkey of the cached item
                            0, // is Delayed?
                            "NULL",//age
                            request.getEarning()); //fthresh
                    statement.executeUpdate(formatted_string);
                }
            }
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

                if(level == "coass_performance" || level == "csms_performance"){
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
        collection.insertOne(persRecord);
    }

    /** Aggregation Routines */
    // Summarizes the performance data of the last window and stores in the logs.
    public static SQEMResponse summarize() {
        Document persRecord = new Document();

        try{
            ExecutorService executor = Executors.newFixedThreadPool(12);
            Collection<Future<?>> tasks = new LinkedList<>();

            tasks.add(executor.submit(() -> { getCSMSPerfromanceSummary(persRecord); }));
            tasks.add(executor.submit(() -> { getLevelPerformanceSummary(persRecord); }));
            tasks.add(executor.submit(() -> { getOverallPerfromanceSummary(persRecord); }));
            tasks.add(executor.submit(() -> { profileContextProviders(persRecord); }));
            tasks.add(executor.submit(() -> { profileConsumers(persRecord); }));
            tasks.add(executor.submit(() -> { profileCQClasses(persRecord); }));
            persRecord.put("cachememory", ContextCacheHandler.getMemoryUtility());

            for (Future<?> currTask : tasks) {
                currTask.get();
            }

            executor.submit(() -> { persistPerformanceSummary(persRecord); });
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
            return SQEMResponse.newBuilder().setStatus("500").build();
        }

        return SQEMResponse.newBuilder().setStatus("200").build();
    }

    // Summarizing Context Providers profiles
    private static Runnable profileContextProviders(Document persRecord){
        ConcurrentHashMap<String, BasicDBObject>  finalres = new ConcurrentHashMap<>();
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            HashMap<String, HashMap<String,Double>>  res = new HashMap<>();
            ResultSet rs_1 = statement.executeQuery("SELECT identifier, fthresh, count(fthresh) AS cnt" +
                    "FROM coass_performance WHERE status = '200' AND method = 'cacheSearch' " +
                    "GROUP BY identifier, fthresh;");

            while(rs_1.next()){
                long count = rs_1.getInt("cnt");
                if(!res.containsKey(rs_1.getString("identifier"))){
                    res.put(rs_1.getString("identifier"),
                            new HashMap(){{
                                put("count", count);
                                put("fthresh", rs_1.getDouble("fthresh")*count);
                            }});
                }
                else {
                    HashMap<String,Double> temp = res.get(rs_1.getString("identifier"));

                    temp.put("count", temp.get("count") + count);
                    temp.put("fthresh", temp.get("fthresh") + (rs_1.getDouble("fthresh")*count));

                    res.put(rs_1.getString("identifier"), temp);
                }
            }

            res.entrySet().parallelStream().forEach((entry) -> {
                String csId = entry.getKey();
                HashMap<String,Double> temp = entry.getValue();

                Double count = temp.get("count");
                temp.put("fthresh", temp.get("fthresh")/count);

                finalres.put(csId, new BasicDBObject(temp));
            });
        }
        catch (Exception ex){
            log.severe("Exception thrown when aggregating the context consumers: "
                    + ex.getMessage());
        }

        persRecord.put("rawContext", finalres);
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

            long totalQueries = 0;
            long totalRetrievals = 0;

            long queryOverhead = 0;
            long totalNetworkOverhead = 0;

            long delayedResponses = 0;

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
                else if(method.equals("executeFetch")){
                    if(status.equals("200")){
                        totalRetrievals += rs_2.getLong("cnt");
                        totalRetrievalCost += rs_2.getDouble("tcost");
                    }
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
            dbo.put("avg_query_overhead", totalQueries > 0 ? queryOverhead / totalQueries : 0);
            dbo.put("avg_network_overhead", totalRetrievals > 0 ? totalNetworkOverhead / totalRetrievals : 0);

            double monetaryGain = totalEarning - totalPenalties - totalRetrievalCost;
            dbo.put("gain", monetaryGain);
            dbo.put("avg_gain", totalQueries > 0 ? monetaryGain / totalQueries : 0);
            dbo.put("earning", totalEarning);
            dbo.put("penalty_cost", totalPenalties);
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

                    cur_Rec.put(isHit?"hits":"misses",curr_value);
                    cur_Rec.put("hitrate",hitRate);

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
                long count = rs_1.getInt("cnt");
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
            log.severe("Exception thrown when aggregating the context consumers: "
                    + ex.getMessage());
        }

        persRecord.put("consumers", finalres);
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
                    return Double.valueOf(avg_latency.getBody());

                return 0;
            }

            double retrieval_latency = rs.getDouble("response_time")/1000;
            long age = rs.getLong("age");

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
            sort.put("_id",-1);
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

    // TODO: If the Context Provider is popular, this statistic should also be cached.
    // Retrieves the summary of context provider's access profile to cache
    public static SQEMResponse getContextProviderProfile(String cpId, String hashKey){
        try{
            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");

            MongoCollection<Document> collection = db.getCollection("performanceSummary");

            Document sort = new Document();
            sort.put("_id",-1);

            Document project = new Document();
            project.put("rawContext."+cpId+".fthresh",1);

            Document filter = new Document();
            filter.put("rawContext."+cpId, new Document(){{ put("$exists", true); }});

            int index = 0;
            double[][] dataset = new double[max_history][2];
            collection.find(filter).projection(project)
                    .sort(sort).limit(10).forEach((Block<Document>) document -> {
                        dataset[index][0] = index;
                        dataset[index][1] = document.getDouble("rawContext."+cpId+".fthresh");
            });

            // The list is inverted, so, estimating from x=-1.
            double exp_fthr = index == 0 ? Double.NaN : predictExpectedValue(dataset, -1);
            return SQEMResponse.newBuilder().setStatus("200")
                    .setBody(Double.isNaN(exp_fthr) ? "NaN" : (exp_fthr < 0 || exp_fthr > 1 ?
                            Double.toString(dataset[0][1]) : Double.toString(exp_fthr))) // quality expectation
                    .setMeta(Double.toString(getLastRetrievalTime(cpId, hashKey))) // last retrieval loss (retLatency + age)
                    .build();
        }
        catch(Exception e){
            JSONObject body = new JSONObject();
            body.put("message",e.getMessage());
            body.put("cause",e.getCause().toString());
            return SQEMResponse.newBuilder().setStatus("500").setBody(body.toString()).build();
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
            all_tables.add("consumer_slas");

            getPerfDBConnection();

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // Create the database tables if not existing
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
                    "    identifier VARCHAR(255) NOT NULL,\n" +
                    "    hashKey VARCHAR(255) NULL,\n" +
                    "    createdDatetime DATETIME NOT NULL,\n" +
                    "    isDelayed BIT NOT NULL,\n" +
                    "    age BIGINT NULL,\n" +
                    "    fthresh REAL NULL)");

            for(LogicalContextLevel level : LogicalContextLevel.values()){
                statement.execute(String.format("IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='%s')\n" +
                                "CREATE TABLE %s(\n" +
                                "    id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,\n" +
                                "    itemId VARCHAR(255) NOT NULL,\n" +
                                "    isHit BIT NOT NULL,\n" +
                                "    response_time BIGINT NOT NULL,\n" +
                                "    createdDatetime DATETIME NOT NULL)",
                        level.toString().toLowerCase(), level.toString().toLowerCase()));
            }

            statement.execute("IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='consumer_slas')\n" +
                    "CREATE TABLE consumer_slas(\n" +
                    "    consumerId VARCHAR(40) NOT NULL,\n" +
                    "    fthresh REAL NOT NULL,\n" +
                    "    earning REAL NOT NULL,\n" +
                    "    rtmax BIGINT NOT NULL,\n" +
                    "    penalty REAL NOT NULL,\n" +
                    "    queryId VARCHAR(40) NULL,\n" +
                    "    queryClass BIGINT NOT NULL,\n" +
                    "    createdDatetime DATETIME NOT NULL)");
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
        }
    }
}
