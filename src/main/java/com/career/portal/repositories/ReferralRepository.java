package com.career.portal.repositories;

import com.career.portal.models.Referral;
import com.career.portal.models.ReferralStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findByReferrerId(Long referrerId);

    List<Referral> findByReferredUserId(Long referredUserId);

    List<Referral> findByStatus(ReferralStatus status);

//    @Query("SELECT r FROM Referral r WHERE r.referredUser.id = :userId AND r.status = 'PENDING'")
    @Query("SELECT r FROM Referral r WHERE r.referredUser.id = :userId")
    List<Referral> findPendingReferralsForUser(@Param("userId") Long userId);

    @Query("SELECT r FROM Referral r WHERE r.referrer.id = :referrerId AND r.jobVacancy.id = :jobVacancyId")
    List<Referral> findByReferrerAndJobVacancy(@Param("referrerId") Long referrerId,
                                               @Param("jobVacancyId") Long jobVacancyId);

    boolean existsByReferrerIdAndReferredUserIdAndJobVacancyId(Long referrerId, Long userId, Long jobVacancyId);

    @Query("SELECT r FROM Referral r WHERE r.status = 'PENDING' AND r.createdAt < :expiryData")
    List<Referral> findExpiredReferrals(@Param("expiryData") LocalDateTime expiryData);

    Optional<Referral> findByReferredUserIdAndJobVacancyId(Long userId, Long jobVacancyId);
}
