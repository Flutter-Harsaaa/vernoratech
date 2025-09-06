package com.vernoraTech.repository;

import com.vernoraTech.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {
    
    // Find user by email
    Optional<UserCredentials> findByEmail(String email);
    
    // Find user by email and active status
    Optional<UserCredentials> findByEmailAndIsActive(String email, Boolean isActive);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find all active users
    List<UserCredentials> findByIsActive(Boolean isActive);
    
    // Find users by email verification status
    List<UserCredentials> findByEmailVerified(Boolean emailVerified);
    
    // Update last login time
    @Modifying
    @Query("UPDATE UserCredentials u SET u.lastLogin = :loginTime WHERE u.id = :id")
    void updateLastLogin(@Param("id") Long id, @Param("loginTime") LocalDateTime loginTime);
    
    // Update email verification status
    @Modifying
    @Query("UPDATE UserCredentials u SET u.emailVerified = true WHERE u.email = :email")
    void verifyEmail(@Param("email") String email);
    
    // Deactivate inactive users
    @Modifying
    @Query("UPDATE UserCredentials u SET u.isActive = false WHERE u.lastLogin < :cutoffDate")
    void deactivateInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);
}