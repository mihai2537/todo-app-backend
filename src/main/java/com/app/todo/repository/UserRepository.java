package com.app.todo.repository;

import com.app.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.items WHERE u.email = :userEmail")
    Optional<User> findByEmailWithItems(@Param("userEmail") String email);
    boolean existsByEmail(String email);
}
