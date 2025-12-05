package com.intellidb.app.dto;

public class ConnectionDetails {

    // --- NEW FIELD ADDED HERE ---
    private String connectionName;

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    // --- GETTER AND SETTER FOR THE NEW FIELD ---
    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    // --- Existing Getters and Setters ---
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}