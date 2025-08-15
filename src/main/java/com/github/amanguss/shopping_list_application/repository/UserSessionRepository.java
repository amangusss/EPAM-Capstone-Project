package com.github.amanguss.shopping_list_application.repository;

import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.UserSession;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Integer> {

    List<UserSession> findByUserAndIsActiveTrueOrderByLoginTimeDesc(User user);
    Optional<UserSession> findBySessionTokenAndIsActiveTrue(String sessionToken);
    List<UserSession> findByUserOrderByLoginTimeDesc(User user);
    Long countByUserAndIsActiveTrue(User user);

    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false, us.logoutTime = :logoutTime " +
            "WHERE us.user = :user AND us.isActive = true")
    void deactivateAllUserSessions(@Param("user") User user, @Param("logoutTime") LocalDateTime logoutTime);

    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false, us.logoutTime = :logoutTime " +
            "WHERE us.lastActivityTime < :cutoffTime AND us.isActive = true")
    void deactivateExpiredSessions(@Param("cutoffTime") LocalDateTime cutoffTime, @Param("logoutTime") LocalDateTime logoutTime);

    @Query("SELECT us.userAgent, COUNT(us) FROM UserSession us GROUP BY us.userAgent ORDER BY COUNT(us) DESC")
    List<Object[]> getBrowserStatistics();
}