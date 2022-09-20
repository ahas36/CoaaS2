package au.coaas.sqem.timescale;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import java.sql.Connection;
import java.util.logging.Logger;
import java.sql.DriverManager;
import java.sql.SQLException;

/* @author: Shakthi */

public class ConnectionPool {

    private Connection tsConnection;

    private static ConnectionPool instance;

    public Connection getTSConnection() {
        return tsConnection;
    }

    private static Logger log = Logger.getLogger(ConnectionPool.class.getName());

    private ConnectionPool() {
        try{
            String connUrl = "jdbc:postgresql://localhost:5436/coaas?user=coaas";
            tsConnection = DriverManager.getConnection(connUrl);
        }
        catch(SQLException ex){
            log.severe(ex.getMessage());
        }
    }

    // SSH Connection to TimeScale instance in Deakin cluster
    private static void ssh() throws JSchException {
        String host = "";
        String user = "coaas";
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

    public static ConnectionPool getInstance() {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                // Implements a singleton
                if (instance == null) {
                    instance = new ConnectionPool();
                }
            }
        }
        return instance;
    }
}
