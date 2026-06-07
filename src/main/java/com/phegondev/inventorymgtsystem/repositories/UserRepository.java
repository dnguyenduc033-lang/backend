package com.phegondev.inventorymgtsystem.repositories;

import com.phegondev.inventorymgtsystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByManagerIdOrderByNameAsc(Long managerId);

    List<User> findByManagerIsNullOrderByNameAsc();

    boolean existsByManagerId(Long managerId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.manager ORDER BY u.name ASC")
    List<User> findAllWithManagerOrderedByName();
}
