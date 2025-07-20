package com.career.portal.repositories;

import com.career.portal.models.User;
import com.career.portal.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByRole(UserRole role);

    @Query("SELECT u FROM User u WHERE u.role = :role ORDER BY u.profileScore DESC")
    List<User> findByRoleOrderByProfileScoreDesc(@Param("role") UserRole role);

    @Query("SELECT u FROM User u WHERE u.role = 'USER' AND u.profileScore >= :minScore")
    List<User> findByProfileScoreGreaterThan(@Param("minScore") Double minScore);

    boolean existsByEmail(String email);

    Optional<User> findByVerificationToken(String verificationToken);

    Optional<User> findByResetToken(String resetToken);

}
