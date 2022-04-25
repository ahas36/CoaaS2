package au.coaas.sqem.handler;

import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.monitor.LogicalContextLevel;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.Statistic;
import au.coaas.sqem.util.Utilty;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PerformanceLogHandler {

    private static final Logger log = Logger.getLogger(LogHandler.class.getName());
    private static List<String> all_tables = Stream.of(LogicalContextLevel.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    // CSMS Performance Records
    // Inserts a new performance record
    public static void insertRecord(LogicalContextLevel level, String id, Boolean isHit, long rTime) {

        Connection connection = null;
        String queryString = "INSERT INTO %s VALUES(%s, %d, %d, datetime('now'))";

        try{
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(String.format(queryString, level.toString().toLowerCase(),
                    id, isHit?1:0, rTime));
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                log.severe(e.getMessage());
            }
        }
    }

    public static void genericRecord(String method, String status, long rTime) {

        Connection connection = null;
        String queryString = "INSERT INTO csms_performance VALUES(%s, %s, %d, datetime('now'))";

        try{
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(String.format(queryString, method, status, rTime));
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                log.severe(e.getMessage());
            }
        }
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
        Connection connection = null;
        level = level.toLowerCase();
        try{
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            String getString = "SELECT * FROM %s WHERE createdTime <= %s";
            String deleteString = "DELETE * FROM %s WHERE createdTime <= %s";

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            LocalDateTime windowThresh = LocalDateTime.now().minusSeconds(duration);
            ResultSet rs = statement.executeQuery(String.format(getString,
                    level, windowThresh));

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
                    }
                }
                else {
                    records.put("item_id", rs.getString("itemId"));
                    records.put("is_hit", rs.getBoolean("isHit"));
                }

                records.put("datetime", rs.getString("createdTime"));

                persRecords.add(records);
            }

            collection.insertMany(persRecords);
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // Failed to close connection.
                log.severe(e.getMessage());
            }
        }
    }

    // Returns the current hit rate of a context item
    public static double getHitRate(LogicalContextLevel level, String id, int duration) {

        Connection connection = null;
        String queryString = "SELECT SUM(isHit)/COUNT(*) AS hitrate" +
                "FROM %s WHERE itemId = %s AND createdTime >= %s";

        try{
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");

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
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // Failed to close connection.
                log.severe(e.getMessage());
            }
        }

        // Returns HR = 0 since there are no performance records
        return 0;
    }

    // COASS Performance
    public static void coassPerformanceRecord(Statistic request) {

        Connection connection = null;
        String queryString = "INSERT INTO coass_performance VALUES(%s, %s, %d, %f, %f, %s, %s, datetime('now'))";

        try{
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String method = request.getMethod();
            switch(method){
                case "execute" :{
                    // Value at the Identifier column here is the Query ID
                    statement.executeUpdate(String.format(queryString, method, request.getStatus(), request.getTime(),
                            request.getEarning(), request.getCost(),
                            request.getIdentifier(), "NULL"));
                    break;
                }
                case "executeFetch": {
                    String hashKey = Utilty.getHashKey(request.getCs().getParamsMap());

                    JSONObject cs = new JSONObject(request.getCs().getContextService());
                    String cs_id = cs.getString("_id");

                    // Value at the Identifier column here is the Context Service ID
                    statement.executeUpdate(String.format(queryString, method, request.getStatus(), request.getTime(),
                            request.getEarning(), request.getCost(), cs_id, hashKey));
                    break;
                }
            }
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                log.severe(e.getMessage());
            }
        }
    }

    // Creating the tables at the start
    public static void seed_performance_db(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            statement.executeUpdate("CREATE TABLE csms_performance(" +
                    "id INT AUTOINCREMENT, " +
                    "method TEXT NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "response_time BIGINT NOT NULL, " +
                    "createdDatetime DATETIME NOT NULL, PRIMARY KEY (id))");

            statement.executeUpdate("CREATE TABLE coass_performance(" +
                    "id INT AUTOINCREMENT, " +
                    "method TEXT NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "response_time BIGINT NOT NULL, " +
                    "earning REAL NULL, cost REAL NULL, " +
                    "identifier TEXT NOT NULL, " +
                    "hashKey TEXT NULL, " +
                    "createdDatetime DATETIME NOT NULL, PRIMARY KEY (id))");

            for(LogicalContextLevel level : LogicalContextLevel.values()){
                statement.executeUpdate(String.format("CREATE TABLE %s (" +
                        "id INT AUTOINCREMENT, " +
                        "itemId TEXT NOT NULL, " +
                        "isHit BOOLEAN NOT NULL, " +
                        "response_time BIGINT NOT NULL, " +
                        "createdDatetime DATETIME NOT NULL, " +
                        "PRIMARY KEY (id))", level.toString().toLowerCase()));
            }
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                log.severe(e.getMessage());
            }
        }
    }

    // Summarizes the performance data of the last window and stores in the logs.
    public static SQEMResponse summarize() {

        Document persRecord = new Document();
        try{
            Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // CSMS Method performances
            ResultSet rs_1 = statement.executeQuery("SELECT method, status, COUNT(id) AS cnt, AVERAGE(response_time) AS avg" +
                    "FROM csms_performance " +
                    "GROUP BY (method, status)");
            HashMap<String, BasicDBObject> res_1 = new HashMap<>();
            while(rs_1.next()){
                if(res_1.containsKey(rs_1.getString("method"))){
                    res_1.put(rs_1.getString("method"),
                            (BasicDBObject) res_1.get(rs_1.getString("method"))
                                    .put(rs_1.getString("status"), new BasicDBObject(){{
                                        put("count", rs_1.getInt("cnt"));
                                        put("average", rs_1.getInt("avg"));
                                    }}));
                }
                else {
                    res_1.put(rs_1.getString("method"), new BasicDBObject(){{
                        put(rs_1.getString("status"), new BasicDBObject(){{
                            put("count", rs_1.getInt("cnt"));
                            put("average", rs_1.getInt("avg"));
                        }});
                    }});
                }
            }

            persRecord.put("csms", res_1);

            // Overall COASS performance
            ResultSet rs_2 = statement.executeQuery("SELECT method, status, " +
                    "COUNT(id) AS cnt, AVERAGE(response_time) AS avg, SUM(earning) AS tearn, SUM(cost) AS tcost" +
                    "FROM coass_performance " +
                    "GROUP BY (method, status)");

            double totalEarning = 0;
            double totalPenalties = 0;
            double totalRetrievalCost = 0;

            long totalQueries = 0;
            long totalRetrievals = 0;

            long queryOverhead = 0;
            long totalNetworkOverhead = 0;

            HashMap<String, BasicDBObject> res_2 = new HashMap<>();
            while(rs_2.next()){
                String method = rs_2.getString("method");
                String status = rs_2.getString("status");

                if(method == "execute"){
                    totalQueries += rs_2.getInt("cnt");
                    if(status == "200"){
                        totalEarning += rs_2.getInt("tearn");
                        totalPenalties += rs_2.getInt("tcost");
                    }
                    queryOverhead += (rs_2.getInt("avg")*totalQueries);
                }
                else if(method == "executeFetch"){
                    if(status == "200"){
                        totalRetrievals += rs_2.getInt("cnt");
                        totalRetrievalCost += rs_2.getDouble("tcost");
                    }
                    totalNetworkOverhead += (rs_2.getInt("avg")*rs_2.getInt("cnt"));
                }

                if(res_2.containsKey(method)){
                    res_2.put(method,
                            (BasicDBObject) res_2.get(method)
                                    .put(status, new BasicDBObject(){{
                                        put("count", rs_2.getInt("cnt"));
                                        put("average", rs_2.getInt("avg"));
                                    }}));
                }
                else {
                    res_2.put(method, new BasicDBObject(){{
                        put(status, new BasicDBObject(){{
                            put("count", rs_2.getInt("cnt"));
                            put("average", rs_2.getInt("avg"));
                        }});
                    }});
                }
            }

            persRecord.put("coass", res_2);
            persRecord.put("no_of_queries", totalQueries);
            persRecord.put("no_of_retrievals", totalRetrievals);

            persRecord.put("response_latency", queryOverhead);
            persRecord.put("network_overhead", totalNetworkOverhead);
            persRecord.put("processing_overhead", queryOverhead - totalNetworkOverhead);

            double monetaryGain = totalEarning - totalPenalties - totalRetrievalCost;
            persRecord.put("gainOrLoss", monetaryGain);

            // Individual context cache level performance
            String query = "SELECT itemId, isHit, COUNT(id) AS cnt, AVERAGE(response_time) AS avg" +
                    "FROM %s GROUP BY (isHit, itemId)";
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
                        res_avg_hit += rs_3.getLong("avg");
                    } else {
                        acc_misses += curr_value;
                        res_avg_miss += rs_3.getLong("avg");
                    }

                    if(res_3.containsKey(rs_3.getString("itemId"))){
                        BasicDBObject cur_Rec = (BasicDBObject) res_3.get(rs_1.getString("itemId"));
                        long curr_saved_value = cur_Rec.getLong(isHit ? "hits" : "misses");

                        double hitRate = (isHit ? curr_saved_value : curr_value)/(curr_saved_value + curr_value);

                        cur_Rec.put(isHit?"hits":"misses",curr_value);
                        cur_Rec.put("hitrate",hitRate);

                        res_3.put(rs_3.getString("itemId"),cur_Rec);
                    }
                    else {
                        res_3.put(rs_3.getString("itemId"), new BasicDBObject(){{
                            put(isHit?"hits":"misses", curr_value);
                        }});
                    }
                }

                res_3.put("hitrate", acc_hits/(acc_hits + acc_misses));
                res_3.put("hit_response_time", res_avg_hit/acc_hits);
                res_3.put("miss_response_time", res_avg_miss/acc_misses);
                level_res.put(lvl.toString().toLowerCase(), new BasicDBObject(res_3));
            }

            persRecord.put("levels", level_res);

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
