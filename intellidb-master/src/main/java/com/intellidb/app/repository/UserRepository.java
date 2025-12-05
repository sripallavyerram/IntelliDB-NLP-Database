package com.intellidb.app.repository;

import com.intellidb.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA will automatically create the query for this method
    Optional<User> findByUsername(String username);
}