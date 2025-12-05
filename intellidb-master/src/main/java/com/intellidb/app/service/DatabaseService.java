package com.intellidb.app.service;

import com.intellidb.app.dto.ConnectionDetails;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

@Service
public class DatabaseService {

    public boolean testConnection(ConnectionDetails details) {
        String url = getJdbcUrl(details);
        try (Connection connection = DriverManager.getConnection(url, details.getUsername(), details.getPassword())) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection Failed! " + e.getMessage());
            return false;
        }
    }

    public List<String> getTables(ConnectionDetails details) {
        List<String> tableNames = new java.util.ArrayList<>();
        String url = getJdbcUrl(details);
        try (Connection connection = DriverManager.getConnection(url, details.getUsername(), details.getPassword())) {
            java.sql.DatabaseMetaData metaData = connection.getMetaData();
            java.sql.ResultSet tables = metaData.getTables(details.getDatabase(), null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                tableNames.add(tables.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve tables: " + e.getMessage());
        }
        return tableNames;
    }

    // --- NEW METHOD TO GET FULL SCHEMA ---
    public String getSchemaDefinition(ConnectionDetails details) {
        StringBuilder schemaBuilder = new StringBuilder();
        String url = getJdbcUrl(details);
        List<String> tableNames = getTables(details);

        try (Connection connection = DriverManager.getConnection(url, details.getUsername(), details.getPassword());
             java.sql.Statement statement = connection.createStatement()) {

            for (String tableName : tableNames) {
                // This query is specific to MySQL
                try (java.sql.ResultSet rs = statement.executeQuery("SHOW CREATE TABLE " + tableName)) {
                    if (rs.next()) {
                        // The result of SHOW CREATE TABLE has 2 columns, the second one is the statement
                        schemaBuilder.append(rs.getString(2)).append(";\n\n");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve schema definition: " + e.getMessage());
            return ""; // Return empty string on failure
        }
        return schemaBuilder.toString();
    }


    public Map<String, Object> executeQuery(ConnectionDetails details, String sql) {
        java.util.List<Map<String, Object>> resultList = new java.util.ArrayList<>();
        java.util.List<String> columns = new java.util.ArrayList<>();
        String url = getJdbcUrl(details);

        try (Connection connection = DriverManager.getConnection(url, details.getUsername(), details.getPassword());
             java.sql.Statement statement = connection.createStatement();
             java.sql.ResultSet resultSet = statement.executeQuery(sql)) {

            java.sql.ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columns.add(metaData.getColumnName(i));
            }

            while (resultSet.next()) {
                Map<String, Object> row = new java.util.LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                resultList.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            return Map.of("error", e.getMessage());
        }
        return Map.of("columns", columns, "rows", resultList);
    }

    private String getJdbcUrl(ConnectionDetails details) {
        return "jdbc:mysql://" + details.getHost() + ":" + details.getPort() + "/" + details.getDatabase()
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }
    // Add this new method to the DatabaseService class
    public Map<String, Object> executeModificationQuery(ConnectionDetails details, String sql) {
        String url = getJdbcUrl(details);
        try (Connection connection = DriverManager.getConnection(url, details.getUsername(), details.getPassword());
             java.sql.Statement statement = connection.createStatement()) {

            int rowsAffected = statement.executeUpdate(sql);
            return Map.of("message", "Query executed successfully. " + rowsAffected + " row(s) affected.");

        } catch (SQLException e) {
            System.err.println("Error executing modification query: " + e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }
    // Add this new method to the DatabaseService class
    public Map<String, Object> getTablePreview(ConnectionDetails details, String tableName) {
        Map<String, Object> previewData = new HashMap<>();
        String url = getJdbcUrl(details);

        // Get Table Structure (Schema)
        List<Map<String, String>> schema = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, details.getUsername(), details.getPassword());
             java.sql.Statement statement = connection.createStatement();
             java.sql.ResultSet rs = statement.executeQuery("DESCRIBE " + tableName)) {

            while (rs.next()) {
                Map<String, String> columnInfo = new LinkedHashMap<>();
                columnInfo.put("Field", rs.getString("Field"));
                columnInfo.put("Type", rs.getString("Type"));
                columnInfo.put("Null", rs.getString("Null"));
                columnInfo.put("Key", rs.getString("Key"));
                schema.add(columnInfo);
            }
            previewData.put("schema", schema);

        } catch (SQLException e) {
            System.err.println("Error describing table: " + e.getMessage());
            return Map.of("error", "Could not retrieve table structure.");
        }

        // Get Table Data Preview (Top 10 rows)
        Map<String, Object> dataPreview = executeQuery(details, "SELECT * FROM " + tableName + " LIMIT 10");
        previewData.putAll(dataPreview);

        return previewData;
    }
}