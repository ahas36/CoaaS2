package au.coaas.sqem.handler;

import au.coaas.sqem.proto.EdgeDevice;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DistributionManager {
    private static final Logger log = Logger.getLogger(ContextServiceHandler.class.getName());

    private static Connection connection;

//    statement.executeUpdate("insert into person values(1, 'leo')");
//    statement.executeUpdate("insert into person values(2, 'yui')");
//    ResultSet rs = statement.executeQuery("select * from person");

    public static long insertEdgDevice (String ipAddress, long index) {
        try {
            String queryString = "INSERT INTO edge_devices(ipAddress, index) " +
                    "VALUES('%s',%d);";
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery(String.format(queryString, ipAddress, index));
            while(rs.next()){
                index = rs.getLong("index");
                break;
            }
        } catch(SQLException ex) {
            log.severe("Could not retrieve the edge device.");
        }
        return index;
    }

    public static void insertCPSubscription (String cpId, long index) {
        try {
            String queryString = "INSERT INTO cp_subscription(cpId, index) " +
                    "VALUES('%s',%d);";
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery(String.format(queryString, cpId, index));
        } catch(SQLException ex) {
            log.severe("Could not retrieve the edge device.");
        }
    }

    public static long getEdgDeviceIndex (String ipAddress) {
        long index = 0L;
        try {
            String queryString = "SELECT index FROM edge_devices " +
                    "WHERE ipAddress = " + ipAddress + ";";
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery(queryString);
            while(rs.next()){
                index = rs.getLong("index");
                break;
            }
        } catch(SQLException ex) {
            log.severe("Could not retrieve the edge device.");
        }
        return index;
    }

    public static List<EdgeDevice> getEdgeDeviceIndexes () {
        synchronized (DistributionManager.class) {
            List<EdgeDevice> indexes = new ArrayList<>();
            try {
                String queryString = "SELECT index, ipAddress FROM edge_devices;";
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
                ResultSet rs = statement.executeQuery(queryString);
                while(rs.next()){
                    indexes.add(EdgeDevice.newBuilder()
                                    .setIndex(rs.getLong("index"))
                                    .setIpAddress(rs.getString("ipAddress"))
                                    .build());
                }
            } catch(SQLException ex) {
                log.severe("Could not retrieve the edge device.");
            }
            return indexes;
        }
    }

    // Setting up the databases to store distributions.
    private static void getDBConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:distribution.db");
        }
        catch(SQLException ex) {
            log.severe("Could not connect to distributions database.");
        }
    }

    public static void seed_distribution_db() {
        try {
            getDBConnection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("CREATE TABLE edge_devices (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ipAddress STRING, index INTEGER, UNIQUE(ipAddress) ON CONFLICT REPLACE)");
            statement.executeUpdate("CREATE TABLE cp_subscription (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "cpId STRING, index INTEGER, UNIQUE(cpId) ON CONFLICT REPLACE)");
        }
        catch(Exception ex) {
            log.severe("Could not seed the distributions database due to: " + ex.getMessage());
        }
    }
}
