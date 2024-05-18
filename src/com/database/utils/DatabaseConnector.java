package com.database.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DatabaseConnector {
    private Connection connection;

    /**
     * Create credentials to connect to database
     *
     * @param database
     * @param user
     * @param password
     */
    public record Credentials(String database, String user, String password) {
    }

    /**
     * Connect to database with credentials
     *
     * @param credentials
     * @throws UnknownHostException
     * @throws SocketException
     */
    public void connect(Credentials credentials) throws UnknownHostException, SocketException {
        // IP address
        String interfaceName = "en0";
        String ipAddress = "";
        String database = credentials.database;
        String user = credentials.user;
        String password = credentials.password;
        getConnection(interfaceName, ipAddress, database, user, password);
    }

    /**
     * Connect to database with credentials and ipAddress
     *
     * @param credentials
     * @param ipAddress
     * @throws UnknownHostException
     * @throws SocketException
     */
    public void connect(Credentials credentials, String ipAddress) throws UnknownHostException, SocketException {
        // IP address
        String interfaceName = "en0";
        String database = credentials.database;
        String user = credentials.user;
        String password = credentials.password;
        getConnection(interfaceName, ipAddress, database, user, password);
    }

    /**
     * Connect to database without credentials
     *
     * @throws UnknownHostException
     * @throws SocketException
     */
    public void connect() throws UnknownHostException, SocketException {
        // IP address
        String interfaceName = "en0";
        String ipAddress = "";
        String databaseName = "master";
        String user = "sa";
        String password = "MyPass!1234;";
        getConnection(interfaceName, ipAddress, databaseName, user, password);
    }

    /**
     * Get connection
     *
     * @param interfaceName
     * @param ipAddress
     * @param databaseName
     * @param user
     * @param password
     * @throws SocketException
     */
    private void getConnection(String interfaceName, String ipAddress, String databaseName, String user, String password) throws SocketException {
        NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
        if (networkInterface == null) {
            System.out.println("No such interface: " + interfaceName);
        } else {
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress.getAddress().length == 4) { // Check for IPv4 address
                    ipAddress = inetAddress.getHostAddress();
                    break;
                }
            }
        }

        if (ipAddress.isEmpty()) {
            System.out.println("No IPv4 address found for the interface: " + interfaceName);
            return; // Exit if no IP address is found
        }

        System.out.println(String.format("Connecting to ip: %s", ipAddress));
        String connectionUrl = String.format("jdbc:sqlserver://%s:1433;" +
                "databaseName=%s;" +
                "user=%s;" +
                "password=%s" +
                "encrypt=true;" +
                "trustServerCertificate=true;", ipAddress, databaseName, user, password);

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(connectionUrl);
            System.out.println(String.format("Connected to database: %s", databaseName));

        } catch (ClassNotFoundException e) {
            System.err.println("Could not load JDBC driver");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Could not connect to database");
            e.printStackTrace();
        }
    }

    /**
     * Disconnect from database
     */
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Disconnected from database");
            } catch (SQLException e) {
                System.err.println("Could not close the connection");
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
