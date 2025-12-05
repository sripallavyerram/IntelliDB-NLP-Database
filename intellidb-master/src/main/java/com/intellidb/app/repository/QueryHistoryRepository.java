package com.intellidb.app.repository;

import com.intellidb.app.model.QueryHistory;
import com.intellidb.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QueryHistoryRepository extends JpaRepository<QueryHistory, Long> {
    // Find the 10 most recent queries for a specific user
    List<QueryHistory> findTop10ByUserOrderByExecutionTimestampDesc(User user);
}