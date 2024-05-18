package com.database.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <ul>
 *     <li>Create Database</li>
 *     <li>Delete Database</li>
 *     <li>Create Table</li>
 *     <li>Delete Table</li>
 *     <li>Insert To Table</li>
 *     <li>Show Line Table Content</li>
 *     <li>Show All Table Content</li>
 *     <li>Get Line Table Content</li>
 *     <li>Get All Table Content</li>
 * </ul>
 *
 * <p>
 *     Includes Column class
 *     <br>
 *     Column:
 *     <ul>
 *     <li>Name</li>
 *     <li>Type</li>
 *     <li>Is Nullable</li>
 *     </ul>
 * </p>
 */
public class SQLStatements {

    /**
     * Auxiliary Class to create Columns
     *
     * @param name       name for column
     * @param type       type for column
     * @param isNullable defines if columns can be null
     */
    public record Column(String name, String type, boolean isNullable) {
        public String getDefinition() {
            return String.format("%s %s %s", name, type, isNullable ? "NULL" : "NOT NULL");
        }
    }

    ;

    /**
     * Create Database
     *
     * @param conn
     * @param name
     */
    public static void createDatabase(Connection conn, String name) {
        //create statements
        Statement st = null;
        try {
            //try to create statement
            st = conn.createStatement();
            //create query
            String sql = String.format("CREATE DATABASE %s;", name);
            //execute
            st.execute(sql);
            //print log
            System.out.printf("Database %s created%n", name);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            //if st is valid then close
            //always wrap in try catch
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    /**
     * Delete database
     *
     * @param conn
     * @param name
     */
    public static void deleteDatabase(Connection conn, String name) {
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = String.format("DROP DATABASE %s;", name);
            st.execute(sql);
            System.out.printf("Database %s deleted\n", name);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    //Alter database

    /**
     * Create a new table in current database
     *
     * @param conn
     * @param newTableName
     * @param columns
     */
    public static void createTable(Connection conn, String newTableName, Column[] columns) {
        Statement st = null;
        String currentDbName = getCurrentDatabaseName(conn);
        try {
            st = conn.createStatement();
            StringBuilder columnDefinitions = buildColumnsStatement(columns);
            String sql = String.format("CREATE TABLE %s (%s);",
                    newTableName,
                    columnDefinitions.toString());

            st.execute(sql);

            System.out.println(String.format("Table Created: %s with columns " +
                            "[%s] in Database: %s", newTableName,
                    columnDefinitions, currentDbName));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            //ALWAYS CLOSE Statement after using
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    /**
     * Create a new Table directly into a database
     *
     * @param conn
     * @param newTableName
     * @param databaseName
     * @param columns
     */
    public static void createTable(Connection conn, String newTableName, String databaseName, Column[] columns) {
        //get currentDb for log
        Statement st = null;
        changeDatabase(conn, databaseName);
        String currentDb = getCurrentDatabaseName(conn);
        try {
            st = conn.createStatement();
            StringBuilder columnDefinitions = buildColumnsStatement(columns);
            String sql = String.format("CREATE TABLE %s (%s);",
                    newTableName, columnDefinitions.toString());

            st.execute(sql);
            System.out.println(String.format("Table Created: %s with columns " +
                            "[%s] in Database: %s", newTableName,
                    columnDefinitions, currentDb));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    /**
     * Deletes a table from current database
     *
     * @param conn
     * @param deleteTableName
     */
    public static void deleteTable(Connection conn, String deleteTableName) {
        Statement st = null;
        String currentDb = getCurrentDatabaseName(conn);
        try {
            st = conn.createStatement();
            String sql = String.format("DROP TABLE %s; ", deleteTableName);
            st.execute(sql);
            System.out.println(String.format("Table Created: %s Deleted in: %s",
                    deleteTableName, currentDb));

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    /**
     * Delete a table into a specific database
     *
     * @param conn
     * @param deleteTableName
     * @param newDatabaseName
     */
    public static void deleteTable(Connection conn, String deleteTableName, String newDatabaseName) {
        Statement st = null;
        changeDatabase(conn, newDatabaseName);
        String currentDbName = getCurrentDatabaseName(conn);
        try {
            st = conn.createStatement();
            String sql = String.format("DROP TABLE %s; ", deleteTableName);
            st.execute(sql);
            System.out.println(String.format("Table Deleted: %s in Database: ",
                    deleteTableName, currentDbName));

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    //Alter table

    /**
     * Insert into a table within the same database
     *
     * @param conn
     * @param tableName
     * @param data      is an object where you put ordered data into the insert statement
     */
    public static void insertRegisterToTable(Connection conn, String tableName, Object[] data) {
        Statement st = null;
        String currentDbName = getCurrentDatabaseName(conn);
        try {
            st = conn.createStatement();
            StringBuilder placeholders = generatePlaceholder(data);
            String sql = "INSERT INTO " +
                    tableName + " VALUES (" + placeholders.toString() + ");";

            PreparedStatement sqlStatement = conn.prepareStatement(sql);
            //set statements with appropriate index
            for (int i = 0; i < data.length; i++) {
                setObject(sqlStatement, i + 1, data[i]);
            }

            int affectedRows = sqlStatement.executeUpdate();
            String sqlPrintStatement = buildSqlWithValues(sql, data);
            System.out.println(sqlPrintStatement +
                    "\n" + "Affected rows: " + affectedRows);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    /**
     * show all data from a table
     *
     * @param conn
     * @param tableName
     */
    public static void showAllDataFromTable(Connection conn, String tableName) {
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = String.format("SELECT * FROM %s", tableName);

            ResultSet rs = st.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Print column names (header)
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            // Processing result set
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    row.append(rs.getString(i)).append("\t");
                }
                System.out.println(row);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    /**
     * show all tables in current tabase
     *
     * @param conn
     */
    public static void showAllDatabaseTables(Connection conn) {
        ResultSet rs = null;
        DatabaseMetaData dbMetaData = null;
        try {
            dbMetaData = conn.getMetaData();
            String[] types = {"TABLE"};
            rs = dbMetaData.getTables(null, null, "%", types);

            System.out.println("Tables in the database:");
            while (rs.next()) {
                // Usually, table name is in the third column
                String tableName = rs.getString(3);
                System.out.println(tableName);
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error closing ResultSet: " + e.getMessage());
                }
            }
        }
    }

    /**
     * show all databases within connection
     * @param conn
     */
    public static void showAllDatabases(Connection conn) {
        ResultSet rs = null;
        DatabaseMetaData dbMetaData = null;
        try {
            dbMetaData = conn.getMetaData();
            rs = dbMetaData.getCatalogs();

            System.out.println("Databases:");
            while (rs.next()) {
                // Usually, database name is in the first column
                String databaseName = rs.getString(1);  // Might need to check the index based on JDBC driver
                System.out.println(databaseName);
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error closing ResultSet: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Show all columns and their types for a given table in the database.
     * @param conn the database connection
     * @param tableName the name of the table for which to show columns and types
     */
    public static void showTableColumnsInfo(Connection conn, String tableName) {
        ResultSet rs = null;
        DatabaseMetaData dbMetaData = null;
        try {
            dbMetaData = conn.getMetaData();
            rs = dbMetaData.getColumns(null, null, tableName, null);

            System.out.println("Columns in " + tableName + ":");
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String columnType = rs.getString("TYPE_NAME");
                System.out.println(columnName + " - " + columnType);
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error closing ResultSet: " + e.getMessage());
                }
            }
        }
    }

    private static void setObject(PreparedStatement pstmt, int parameterIndex, Object value) throws SQLException, SQLException {
        switch (value) {
            case Integer i -> pstmt.setInt(parameterIndex, i);
            case String s -> pstmt.setString(parameterIndex, s);
            case Double v -> pstmt.setDouble(parameterIndex, v);
            case Float v -> pstmt.setFloat(parameterIndex, v);
            case Boolean b -> pstmt.setBoolean(parameterIndex, b);
            case Long l -> pstmt.setLong(parameterIndex, l);
            case null -> pstmt.setObject(parameterIndex, null);
            default ->
                // Fallback for other types, might need further expansion for specific types like Date, etc.
                    pstmt.setObject(parameterIndex, value);
        }
    }

    /**
     * Get name of the current database
     *
     * @param conn
     * @return
     */
    public static String getCurrentDatabaseName(Connection conn) {
        Statement st = null;
        String currentDb = "None";
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("SELECT DB_NAME() AS CurrentDatabase");
            if (rs.next()) {
                currentDb = rs.getString(1);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        return currentDb;
    }

    /**
     * Creates a string with definitions for columns
     *
     * @param columns
     * @return
     */
    private static StringBuilder buildColumnsStatement(Column[] columns) {
        StringBuilder columnDefinitions = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            columnDefinitions.append(columns[i].getDefinition());
            if (i < columns.length - 1) {
                columnDefinitions.append(", ");
            }
        }
        return columnDefinitions;
    }

    /**
     * Change the current database connection
     *
     * @param conn
     * @param newDatabaseName
     */
    public static void changeDatabase(Connection conn, String newDatabaseName) {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("USE " + newDatabaseName + ";");
            System.out.println(String.format("USING Database: %s", newDatabaseName));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            //ALWAYS CLOSE Statement after using
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    /**
     * Generates query placeholders base on lent of data
     *
     * @param data
     * @return
     */
    private static StringBuilder generatePlaceholder(Object[] data) {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            placeholders.append("?");
            if (i < data.length - 1) {
                placeholders.append(", ");
            }
        }
        return placeholders;
    }

    /**
     * Build Sql with values for print
     *
     * @param sql
     * @param values
     * @return
     */
    private static String buildSqlWithValues(String sql, Object[] values) {
        String[] parts = sql.split("\\?");
        StringBuilder sb = new StringBuilder();

        // Assume there is at least one part and one value less than or equal to the number of placeholders
        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i]);
            if (i < values.length) {
                sb.append(formatValue(values[i]));
            }
        }

        return sb.toString();
    }

    /**
     * Format values
     *
     * @param value
     * @return
     */
    private static String formatValue(Object value) {
        if (value instanceof String) {
            return "'" + value + "'";
        } else {
            return value.toString();
        }
    }

    /**
     * Remove a user from a specific database.
     * @param conn Connection to the database.
     * @param databaseName Name of the database from which the user will be removed.
     * @param userName Name of the user to remove.
     */
    public static void removeUserFromDatabase(Connection conn, String databaseName, String userName) {
        executeStatement(conn, String.format("USE %s; DROP USER IF EXISTS [%s];", databaseName, userName));
    }

    /**
     * Create a login at the server level if it does not exist.
     * @param conn Connection to the database server.
     * @param loginName Name of the login to create.
     * @param password Password for the new login.
     */
    public static void createServerLogin(Connection conn, String loginName, String password) {
        executeStatement(conn, String.format("IF NOT EXISTS (SELECT loginname FROM master.dbo.syslogins WHERE name = '%s') " +
                "CREATE LOGIN [%s] WITH PASSWORD = '%s';", loginName, loginName, password));
    }

    /**
     * Grant specific roles to a user in a database.
     * @param conn Connection to the database.
     * @param databaseName Database where the roles will be assigned.
     * @param userName Name of the user.
     */
    public static void grantDatabaseAccess(Connection conn, String databaseName, String userName) {
        executeStatement(conn, String.format("USE %s; " +
                "CREATE USER [%s] FOR LOGIN [%s]; " +
                "EXEC sp_addrolemember 'db_datareader', '%s'; " +
                "EXEC sp_addrolemember 'db_datawriter', '%s';", databaseName, userName, userName, userName, userName));
    }

    /**
     * Executes a given SQL statement.
     * @param conn Connection to the database.
     * @param sql SQL statement to execute.
     */
    private static void executeStatement(Connection conn, String sql) {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute(sql);
            System.out.println("Executed: " + sql);
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.err.println("Error closing Statement: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Retrieves all data from a specified table and returns it as a list of Object arrays.
     * Each Object array represents a row in the database.
     *
     * @param conn Connection to the database.
     * @param tableName The name of the table to retrieve data from.
     * @return List of Object arrays, where each Object array represents a row.
     */
    public static ArrayList<Object> getAllDataFromTable(Connection conn, String tableName) {
        ArrayList<Object> results = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM " + tableName);

            // Get number of columns in the result set
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Iterate through the result set and build the list of Object arrays
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);  // ResultSet is 1-indexed
                }
                results.add(row);
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        } finally {
            // Always close the ResultSet and Statement when done
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return results;
    }

    /**
     * Checks if a specified table exists in the database.
     *
     * @param conn Connection to the database.
     * @param tableName The name of the table to check.
     * @return true if the table exists, false otherwise.
     */
    public static boolean tableExists(Connection conn, String tableName) {
        boolean exists = false;
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
            exists = rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking for table existence: " + e.getMessage());
        }
        return exists;
    }

    /**
     * Checks if a specified database exists.
     *
     * @param conn Connection to the database server (typically requires connection to a default or master database).
     * @param databaseName The name of the database to check.
     * @return true if the database exists, false otherwise.
     */
    public static boolean databaseExists(Connection conn, String databaseName) {
        boolean exists = false;
        String query = "SELECT 1 FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?"; // This SQL works for many SQL databases

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, databaseName);
            try (ResultSet rs = ps.executeQuery()) {
                // If the ResultSet contains at least one row, the database exists
                exists = rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking for database existence: " + e.getMessage());
        }
        return exists;
    }
}


