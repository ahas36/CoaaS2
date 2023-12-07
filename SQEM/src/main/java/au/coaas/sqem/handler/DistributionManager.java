package au.coaas.sqem.handler;

import au.coaas.cqc.proto.CordinatesIndex;
import au.coaas.sqem.proto.EdgeDevice;
import au.coaas.sqem.proto.EdgeStatus;
import au.coaas.sqem.proto.HostSpecs;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.util.GeoIndexer;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

import java.io.File;
import java.net.InetAddress;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DistributionManager {
    private static final Logger log = Logger.getLogger(ContextServiceHandler.class.getName());

    private static Connection connection;

    // Comment following when deploying.
    private static final String dbLocation = "/target/lib/geolite2-city.mmdb";
    // Uncomment the following when deploying.
    // private static final String dbLocation = "/app/lib/geolite2-city.mmdb";

    // Resolve index and register a new edge device to the distribution.
    public static SQEMResponse registerDevice (EdgeStatus newDevice){
        // Resolve the index that the edge would cover based on:
        // 1. Its system specifications,
        // 2. Intersection with other edge devices, and
        // 3. Number of context providers in the area.
        long index = resolveIndex(newDevice);
        insertEdgDevice(newDevice, index);
        return SQEMResponse.newBuilder().setStatus("200")
                .setBody(String.valueOf(index)).build();
    }

    public static void heartBeatUpdate (EdgeStatus device) {
        try {
            String queryString = "UPDATE edge_devices SET createdTime=%d, freeMemory=%f, cpuUsage=%f WHERE ipAddress='%s';";
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeQuery(String.format(queryString, System.currentTimeMillis(),
                    device.getSpecs().getFreeMemory(), device.getSpecs().getCpuUsage(),
                    device.getIpAddress()));
        } catch(SQLException ex) {
            log.severe("Could not update heart beat details for edge device.");
        }
    }

    public static void insertEdgDevice (EdgeStatus newDevice, long index) {
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            if(newDevice.getIpAddress().equals("0.0.0.0")) {
                String queryString = "INSERT INTO edge_devices(ipAddress, index, createdTime) VALUES('%s',%d, %d);";
                statement.executeQuery(String.format(queryString, newDevice.getIpAddress(),
                        System.currentTimeMillis()));
            }
            else {
                String queryString = "INSERT INTO edge_devices(ipAddress, index, processors, freeMemory, maxMemory, " +
                        "cpuUsage, os, archi, createdTime) VALUES('%s',%d, %d, %f, %f, %f, '%s', '%s', %d);";
                HostSpecs specs = newDevice.getSpecs();
                statement.executeQuery(String.format(queryString, newDevice.getIpAddress(), index,
                                specs.getProcessors(), specs.getFreeMemory(), specs.getMaxMemory(),
                                specs.getCpuUsage(), specs.getOperatingSystem(), specs.getArchi(),
                                System.currentTimeMillis()));
            }
        } catch(SQLException ex) {
            log.severe("Could not retrieve the edge device.");
        }
    }

    private static long master_id = 0;
    public static void insertCPSubscription (String cpId, long index, long sub_edge) {
        try {
            if(sub_edge == 0) {
                if(master_id == 0) {
                    String queryString = "SELECT id FROM edge_devices WHERE ipAddress = '0.0.0.0';";
                    Statement statement = connection.createStatement();
                    statement.setQueryTimeout(30);
                    ResultSet rs = statement.executeQuery(queryString);
                    rs.next();
                    master_id = rs.getLong("id");
                }
                sub_edge = master_id;
            }
            String queryString = "INSERT INTO cp_subscription(cpId, index, sub_edge, subTime) " +
                    "VALUES('%s',%d, %d, %d);";
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeQuery(String.format(queryString, cpId, index, sub_edge, System.currentTimeMillis()));
        } catch(SQLException ex) {
            log.severe("Could not add a new CP subscription.");
        }
    }

    protected static HashMap<Long,Integer> currentLoad (List<Long> subEdgeIds) {
        HashMap<Long,Integer> load = new HashMap<>();
        for(Long edgeId: subEdgeIds) {
            try {
                String queryString = "SELECT COUNT(*) AS cnt FROM cp_subscription WHERE sub_edge = %d;";
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
                ResultSet rs = statement.executeQuery(String.format(queryString, edgeId));
                rs.next();
                load.put(edgeId, rs.getInt("cnt"));
            } catch(SQLException ex) {
                log.severe("Could not retrieve the edge device.");
            }
        }
        return load;
    }

    public static long getEdgDeviceIndex (String ipAddress) {
        long index = 0L;
        try {
            String queryString = "SELECT index FROM edge_devices WHERE ipAddress = " + ipAddress + ";";
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
                long pastTime = System.currentTimeMillis() - 60000;
                String queryString = "SELECT id, index, ipAddress, createdTime FROM edge_devices " +
                        "WHERE createdTime >= " + pastTime + ";";
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
                ResultSet rs = statement.executeQuery(queryString);

                while(rs.next()){
                    indexes.add(EdgeDevice.newBuilder()
                                    .setId(rs.getLong("id"))
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
                    "ipAddress STRING, index INTEGER, processors INTEGER, freeMemory REAL, maxMemory REAL, cpuUsage REAL, " +
                    "os STRING, archi STRING, createdTime INTEGER, UNIQUE(ipAddress) ON CONFLICT REPLACE)");
            statement.executeUpdate("CREATE TABLE cp_subscription (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "cpId STRING, index INTEGER, sub_edge INTEGER, subTime INTEGER, FOREIGN KEY (sub_edge) " +
                    "REFERENCES edge_devices (id), UNIQUE(cpId) ON CONFLICT REPLACE)");
        }
        catch(Exception ex) {
            log.severe("Could not seed the distributions database due to: " + ex.getMessage());
        }
    }

    private static long resolveIndex (EdgeStatus specifications) {
        // TODO: Index assignement is independent from what context providers get assigned to it.
        /** So, a separate load balancer should be able to predict the load that will come from within this area, consider the current load,
         * then map that to the specifications of the node and existing nodes in the area
         * then, load balance the context provider assignment. */

        // For this POC, Master Node is at Index = 85be6357fffffff (Melbourne area, res=5),
        // worker node_1 is at index = 88be63cdedfffff (Wattle Park area, res=8).
        // and node_2 is at index = 88be63cd3bfffff (Deakin Burwood Campus area, res=8).

        GeoIndexer indexer = GeoIndexer.getInstance();
        if(specifications.getIpAddress().equals("0.0.0.0")) {
            CordinatesIndex res = indexer.getGeoIndex(-37.84732999340094, 145.11569778933645, 5);
            return res.getIndex();
        } else {
            // String approxLocation = "-37.840103016024436;145.10771553529105";
            String approxLocation = convertIpToLocation(specifications.getIpAddress());

            String[] loc_cords = approxLocation.split(";");
            log.info("New edge node resolved from (Postal Code): " + loc_cords[2]);
            CordinatesIndex res = indexer.getGeoIndex(Double.valueOf(loc_cords[0]), Double.valueOf(loc_cords[1]), 8);

            return res.getIndex();
        }
    }

    private static String convertIpToLocation (String ip) {
        try {
            File database = new File(dbLocation);
            DatabaseReader dbReader = new DatabaseReader.Builder(database).build();

            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = dbReader.city(ipAddress);

            String postalCode = response.getPostal().getCode();
            String latitude = response.getLocation().getLatitude().toString();
            String longitude = response.getLocation().getLongitude().toString();

            return latitude + ";" + longitude + ";" + postalCode;
        }
        catch(Exception ex) {
            log.severe("Could not convert the ip address to approximate location due to: " + ex.getMessage());
            return "-37.84732999340094;145.11569778933645;3000"; // Master
        }
    }
}
