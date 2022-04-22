package au.coaas.sqem.handler;

import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.monitor.LogicalContextLevel;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class PerformanceLogHandler {

    private static final Logger log = Logger.getLogger(LogHandler.class.getName());

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

    // Clears older records earlier than the current window from the in-memory database
    // These records are persisted in MongoDB logs
    public static void clearOldRecords(int duration){
        Arrays.stream(LogicalContextLevel.values()).parallel().forEach((level)-> {
            removeAndPersistRecord(level, duration);
        });
    }

    private static void removeAndPersistRecord(LogicalContextLevel level, int duration) {
        Connection connection = null;
        try{
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            String getString = "SELECT * FROM %s WHERE createdTime <= %s";
            String deleteString = "DELETE * FROM %s WHERE createdTime <= %s";

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            LocalDateTime windowThresh = LocalDateTime.now().minusSeconds(duration);
            ResultSet rs = statement.executeQuery(String.format(getString,
                    level.toString().toLowerCase(), windowThresh));

            statement.executeUpdate(String.format(deleteString,
                    level.toString().toLowerCase(), windowThresh));

            MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
            MongoDatabase db = mongoClient.getDatabase("coaas_log");

            MongoCollection<Document> collection = db.getCollection("queryPerformance");

            ArrayList<Document> persRecords = new ArrayList();
            while(rs.next()){
                Document records = new Document();
                records.put("type", level.toString());
                records.put("item_id", rs.getString("itemId"));
                records.put("is_hit", rs.getBoolean("isHit"));
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
                "FROM %s WHERE itemId = %s AND createdTime => %s";

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
}
