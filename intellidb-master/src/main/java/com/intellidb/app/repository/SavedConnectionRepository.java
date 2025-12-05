package com.intellidb.app.repository;

import com.intellidb.app.model.SavedConnection;
import com.intellidb.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedConnectionRepository extends JpaRepository<SavedConnection, Long> {
    List<SavedConnection> findByUser(User user);
}