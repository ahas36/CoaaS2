package au.coaas.sqem.handler;

import au.coaas.sqem.monitor.LogicalContextLevel;
import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.Statistic;
import au.coaas.sqem.util.Utilty;

import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONObject;

import java.sql.*;
import java.time.LocalDateTime;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class PerformanceLogHandler {

    private static Connection connection = null;
    private static final Logger log = Logger.getLogger(LogHandler.class.getName());
    private static List<String> all_tables = Stream.of(LogicalContextLevel.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    // CSMS Performance Records
    // Inserts a new performance record
    public static void insertRecord(LogicalContextLevel level, String id, Boolean isHit, long rTime) {

        // Connection connection = null;
        String queryString = "INSERT INTO %s(itemId,isHit,response_time,createdDatetime) VALUES('%s', %d, %d, GETDATE());";

        try{
            // connection = DriverManager.getConnection("jdbc:sqlite::memory:");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(String.format(queryString, level.toString().toLowerCase(),
                    id, isHit?1:0, rTime));
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
//        finally
//        {
//            try
//            {
//                if(connection != null){
//                    connection.commit();
//                    connection.close();
//                }
//            }
//            catch(SQLException e)
//            {
//                log.severe(e.getMessage());
//            }
//        }
    }

    public static void genericRecord(String method, String status, long rTime) {

        // Connection connection = null;
        String queryString = "INSERT INTO csms_performance(method,status,response_time,createdDatetime) "
                + "VALUES('%s', '%s', %d, GETDATE());";

        try{
            // connection = DriverManager.getConnection("jdbc:sqlite::memory:");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(String.format(queryString, method, status, rTime));
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
//        finally
//        {
//            try
//            {
//                if(connection != null){
//                    connection.commit();
//                    connection.close();
//                }
//            }
//            catch(SQLException e)
//            {
//                log.severe(e.getMessage());
//            }
//        }
    }

    // Clears older records earlier than the current window from the in-memory database
    // These records are persisted in MongoDB logs
    public static void clearOldRecords(int duration){
        all_tables.add("coass_performance");
        all_tables.add("csms_performance");

        String[] stockArr = new String[all_tables.size()];

        Arrays.stream(all_tables.toArray(stockArr)).parallel().forEach((level)-> {
            removeAndPersistRecord(level, duration);
        });
    }

    private static void removeAndPersistRecord(String level, int duration) {
        // Connection connection = null;
        level = level.toLowerCase();
        try{
            // connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            String getString = "SELECT * FROM %s WHERE createdDatetime <= '%s';";
            String deleteString = "DELETE FROM %s WHERE createdDatetime <= '%s';";

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

//            ArrayList<String> test = new ArrayList<>();
//            ResultSet rs1 = statement.executeQuery("SELECT name FROM sqlite_master WHERE type = \"table\"");
//            while(rs1.next()){
//                test.add(rs1.getString("name"));
//            }

            LocalDateTime windowThresh = LocalDateTime.now().minusSeconds(duration);
            String qStr = String.format(getString, level, windowThresh);
            ResultSet rs = statement.executeQuery(qStr);

            statement.executeUpdate(String.format(deleteString,
                    level, windowThresh));

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

            if(persRecords.size() > 0)
                collection.insertMany(persRecords);
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
        }
//        finally
//        {
//            try
//            {
//                if(connection != null){
//                    connection.commit();
//                    connection.close();
//                }
//            }
//            catch(SQLException e)
//            {
//                // Failed to close connection.
//                log.severe(e.getMessage());
//            }
//        }
    }

    // Returns the current hit rate of a context item
    public static double getHitRate(LogicalContextLevel level, String id, int duration) {

        // Connection connection = null;
        String queryString = "SELECT SUM(isHit)/COUNT(*) AS hitrate" +
                "FROM %s WHERE itemId = '%s' AND createdDatetime >= '%s';";

        try{
            // connection = DriverManager.getConnection("jdbc:sqlite::memory:");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            LocalDateTime windowThresh = LocalDateTime.now().minusSeconds(duration);
            ResultSet rs = statement.executeQuery(String.format(queryString,
                    level.toString().toLowerCase(), id, windowThresh.toString()));

            // Returns the actual hit rate as recorded
            return rs.getDouble("hitrate");
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
//        finally
//        {
//            try
//            {
//                if(connection != null){
//                    connection.commit();
//                    connection.close();
//                }
//            }
//            catch(SQLException e)
//            {
//                // Failed to close connection.
//                log.severe(e.getMessage());
//            }
//        }

        // Returns HR = 0 since there are no performance records
        return 0;
    }

    // COASS Performance
    public static void coassPerformanceRecord(Statistic request) {

        // Connection connection = null;
        String queryString = "INSERT INTO coass_performance(method,status,response_time,earning,"+
                "cost,identifier,hashKey,createdDatetime,isDelayed) " +
                "VALUES('%s', '%s', %d, %f, %f, '%s', '%s', GETDATE(), %d);";

        try{
            // connection = DriverManager.getConnection("jdbc:sqlite::memory:");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String method = request.getMethod();
            switch(method){
                case "execute" :{
                    // Value at the Identifier column here is the Query ID
                    statement.executeUpdate(String.format(queryString, method, request.getStatus(), request.getTime(),
                            request.getEarning(), request.getCost(),
                            request.getIdentifier(), "NULL", request.getIsDelayed()));
                    break;
                }
                case "executeFetch": {
                    String hashKey = Utilty.getHashKey(request.getCs().getParamsMap());

                    JSONObject cs = new JSONObject(request.getCs().getContextService().getJson());
                    String cs_id = cs.getJSONObject("_id").getString("$oid");

                    // Value at the Identifier column here is the Context Service ID
                    String formatted_string = String.format(queryString,
                            method, // Method name
                            request.getStatus(), // Status of the request
                            request.getTime(), // Response time
                            request.getEarning(), // Earnings from the query
                            request.getCost(), // Cost from query
                            cs_id, // Context Service Identifier
                            hashKey, // Hashkey of the cached item
                            0); // is Delayed?
                    statement.executeUpdate(formatted_string);
                    break;
                }
            }
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
//        finally
//        {
//            try
//            {
//                if(connection != null){
//                    connection.commit();
//                    connection.close();
//                }
//            }
//            catch(SQLException e)
//            {
//                log.severe(e.getMessage());
//            }
//        }
    }

    // Context Service Performance for retrievals

    public static double getLastRetrievalTime(String csId){
        // Connection connection = null;
        String queryString = "SELECT TOP 1 response_time" +
                "FROM coass_performance WHERE status = '200' AND identifier = '%s' ORDER BY id DESC;";

        try{
            // connection = DriverManager.getConnection("jdbc:sqlite::memory:");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            csId = csId.startsWith("{") ? (new JSONObject(csId)).getString("$oid") : csId;
            String finalString = String.format(queryString,csId);

            ResultSet rs = statement.executeQuery(finalString);

            if(!rs.next()) {
                SQEMResponse avg_latency = ContextCacheHandler.getPerformanceStats("avg_network_overhead");
                if(avg_latency.getStatus().equals("200"))
                    return Double.valueOf(avg_latency.getBody());

                return 0;
            }

            return rs.getDouble("response_time");
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
//        finally
//        {
//            try
//            {
//                if(connection != null){
//                    connection.commit();
//                    connection.close();
//                }
//            }
//            catch(SQLException e)
//            {
//                // Failed to close connection.
//                log.severe(e.getMessage());
//            }
//        }

        // We can ignore the retrieval latency of context services which has retrievals earlier than a window size.
        // That is because, as the lifetime grows larger in value, network latency can be ignored.
        return 0;
    }

    private static void getPerfDBConnection() throws SQLException {
        String connectionString = "jdbc:sqlserver://localhost;database=AdventureWorks;integratedSecurity=true;" +
                "user=sa;password=coaas@PerfDB2k22";
        connection = DriverManager.getConnection(connectionString);
    }

    private static Boolean checkForDB(Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM sys.databases WHERE name = 'coassPerformance'");
        if(!rs.next())
            return false;
        return true;
    }

    // Creating the tables at the start
    public static void seed_performance_db(){
        try{
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if(!checkForDB(statement)){
                statement.execute("CREATE DATABASE coassPerformance;");
                statement.execute("USE coassPerformance;");

                statement.execute(
                        "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='csms_performance')\n" +
                        "CREATE TABLE csms_performance(\n" +
                        "    id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,\n" +
                        "    method VARCHAR NOT NULL,\n" +
                        "    status VARCHAR NOT NULL,\n" +
                        "    response_time BIGINT NOT NULL,\n" +
                        "    createdDatetime DATETIME NOT NULL)\n" +
                        "GO");

                statement.execute("IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='coass_performance')\n" +
                        "CREATE TABLE coass_performance(\n" +
                        "    id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,\n" +
                        "    method VARCHAR NOT NULL,\n" +
                        "    status VARCHAR NOT NULL,\n" +
                        "    response_time BIGINT NOT NULL,\n" +
                        "    earning REAL NULL,\n" +
                        "    cost REAL NULL,\n" +
                        "    identifier VARCHAR NOT NULL,\n" +
                        "    hashKey VARCHAR NULL,\n" +
                        "    createdDatetime DATETIME NOT NULL,\n" +
                        "    isDelayed BIT NOT NULL)\n" +
                        "GO");

                for(LogicalContextLevel level : LogicalContextLevel.values()){
                    statement.execute(String.format("IF NOT EXISTS (SELECT * FROM sys.tables WHERE name='%s')\n" +
                            "CREATE TABLE %s(\n" +
                            "    id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,\n" +
                            "    itemId VARCHAR NOT NULL,\n" +
                            "    isHit BIT NOT NULL,\n" +
                            "    response_time BIGINT NOT NULL,\n" +
                            "    createdDatetime DATETIME NOT NULL)\n" +
                            "GO", level.toString().toLowerCase(), level.toString().toLowerCase()));
                }
            }
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
//        finally
//        {
//            try
//            {
//                if(connection != null){
//                    connection.commit();
//                    connection.close();
//                }
//            }
//            catch(SQLException e)
//            {
//                log.severe(e.getMessage());
//            }
//        }
    }

    // Summarizes the performance data of the last window and stores in the logs.
    public static SQEMResponse summarize() {

        Document persRecord = new Document();
        try{
            // Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // CSMS Method performances
            ResultSet rs_1 = statement.executeQuery("SELECT method, status, count(*) AS cnt, avg(response_time) AS average " +
                    "FROM csms_performance GROUP BY method, status;");
            HashMap<String, BasicDBObject> res_1 = new HashMap<>();
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

            persRecord.put("csms", res_1);

            // Overall COASS performance
            ResultSet rs_2 = statement.executeQuery("SELECT method, status, " +
                    "count(id) AS cnt, avg(response_time) AS average, sum(earning) AS tearn, sum(cost) AS tcost, sum(isDelayed) AS tdelay " +
                    "FROM coass_performance " +
                    "GROUP BY method, status;");

            double totalEarning = 0;
            double totalPenalties = 0;
            double totalRetrievalCost = 0;

            long totalQueries = 0;
            long totalRetrievals = 0;

            long queryOverhead = 0;
            long totalNetworkOverhead = 0;

            long delayedResponses = 0;

            HashMap<String, BasicDBObject> res_2 = new HashMap<>();
            while(rs_2.next()){
                String method = rs_2.getString("method");
                String status = rs_2.getString("status");

                if(method == "execute"){
                    totalQueries += rs_2.getLong("cnt");
                    queryOverhead += (rs_2.getLong("average")*rs_2.getLong("cnt"));
                    delayedResponses += rs_2.getLong("tdelay");

                    if(status.equals("200")){
                        totalEarning += rs_2.getDouble("tearn");
                        totalPenalties += rs_2.getDouble("tcost");
                    }
                }
                else if(method == "executeFetch"){
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

            persRecord.put("coass", res_2);

            BasicDBObject dbo = new BasicDBObject();
            dbo.put("no_of_queries", totalQueries);
            dbo.put("delayed_queries", delayedResponses);
            dbo.put("no_of_retrievals", totalRetrievals);
            dbo.put("avg_query_overhead", totalQueries > 0 ? queryOverhead / totalQueries : 0);
            dbo.put("avg_network_overhead", totalRetrievals > 0 ? totalNetworkOverhead / totalRetrievals : 0);
            dbo.put("avg_processing_overhead", totalQueries > 0 ? (queryOverhead - totalNetworkOverhead) / totalQueries : 0);

            double monetaryGain = totalEarning - totalPenalties - totalRetrievalCost;
            dbo.put("gain", monetaryGain);
            dbo.put("avg_gain", totalQueries > 0 ? monetaryGain / totalQueries : 0);
            dbo.put("earning", totalEarning);
            dbo.put("penalty_cost", totalPenalties);
            dbo.put("retrieval_cost", totalRetrievalCost);

            persRecord.put("summary", dbo);

            ContextCacheHandler.updatePerformanceStats(dbo.toMap());

            // TODO:
            // Utility from saving response time from caching.

            // Individual context cache level performance
            String query = "SELECT itemId, isHit, count(id) AS cnt, avg(response_time) AS average " +
                    "FROM %s GROUP BY isHit, itemId";
            HashMap<String, BasicDBObject> level_res = new HashMap<>();
            for(LogicalContextLevel lvl : LogicalContextLevel.values()){
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
                        BasicDBObject cur_Rec = (BasicDBObject) res_3.get(rs_1.getString("itemId"));
                        long curr_saved_value = cur_Rec.getLong(isHit ? "hits" : "misses");

                        double hitRate = (curr_saved_value + curr_value) > 0 ?
                                (isHit ? curr_saved_value : curr_value)/(curr_saved_value + curr_value) : 0;

                        cur_Rec.put(isHit?"hits":"misses",curr_value);
                        cur_Rec.put("hitrate",hitRate);

                        res_3.put(rs_3.getString("itemId"),cur_Rec);
                    }
                    else {
                        res_3.put(rs_3.getString("itemId"), new BasicDBObject(){{
                            put(isHit?"hits":"misses", curr_value);
                            put("id", rs_3.getString("itemId"));
                        }});
                    }
                }

                HashMap<String, Object> res_lvl = new HashMap<>();

                res_lvl.put("items", res_3.values());
                res_lvl.put("hitrate", (acc_hits + acc_misses) > 0 ? acc_hits/(acc_hits + acc_misses) : 0);
                res_lvl.put("hit_response_time", acc_hits > 0 ? res_avg_hit/acc_hits : 0);
                res_lvl.put("miss_response_time", acc_misses > 0 ? res_avg_miss/acc_misses : 0);
                level_res.put(lvl.toString().toLowerCase(), new BasicDBObject(res_lvl));
            }

            persRecord.put("levels", level_res);
            persRecord.put("cachememory", ContextCacheHandler.getMemoryUtility());

            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");

            MongoCollection<Document> collection = db.getCollection("performanceSummary");

            collection.insertOne(persRecord);
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
            return SQEMResponse.newBuilder().setStatus("500").build();
        }

        return SQEMResponse.newBuilder().setStatus("200").build();
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

}
