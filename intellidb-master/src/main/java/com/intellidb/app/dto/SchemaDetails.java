package com.intellidb.app.dto;

import java.util.List;

public class SchemaDetails {
    private String databaseName;
    private List<String> tableNames;

    public SchemaDetails(String databaseName, List<String> tableNames) {
        this.databaseName = databaseName;
        this.tableNames = tableNames;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }
}