package com.intellidb.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "query_history")
public class QueryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CORRECTED: Changed to TEXT to allow for very long queries
    @Column(nullable = false, columnDefinition = "TEXT")
    private String naturalQuery;

    // CORRECTED: Changed to TEXT to allow for very long SQL
    @Column(nullable = false, columnDefinition = "TEXT")
    private String generatedSql;

    @Column(nullable = false)
    private LocalDateTime executionTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNaturalQuery() { return naturalQuery; }
    public void setNaturalQuery(String naturalQuery) { this.naturalQuery = naturalQuery; }
    public String getGeneratedSql() { return generatedSql; }
    public void setGeneratedSql(String generatedSql) { this.generatedSql = generatedSql; }
    public LocalDateTime getExecutionTimestamp() { return executionTimestamp; }
    public void setExecutionTimestamp(LocalDateTime executionTimestamp) { this.executionTimestamp = executionTimestamp; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}