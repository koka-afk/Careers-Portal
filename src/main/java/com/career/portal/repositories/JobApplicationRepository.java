package com.career.portal.repositories;

import com.career.portal.models.ApplicationStatus;
import com.career.portal.models.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUserId(Long UsedId);

    List<JobApplication> findByJobVacancyId(Long JobVacancyId);

    List<JobApplication> findByStatus(ApplicationStatus status);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.jobVacancy.id =  :jobVacancyId ORDER BY ja.user.profileScore DESC")
    List<JobApplication> findByJobVacancyIdOrderByProfileScoreDesc(@Param("jobVacancyId") Long jobVacancyId);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.jobVacancy.id = :jobVacancyId ORDER BY ja.appliedAt DESC")
    List<JobApplication> findByJobVacancyIdOrderByAppliedAtDesc(@Param("jobVacancyId") Long jobVacancyId);

    Optional<JobApplication> findByUserIdAndJobVacancyId(Long userId, Long jobVacancyId);

    boolean existsByUserIdAndJobVacancyId(Long userId, Long jobVacancyId);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.reviewedBy = :recruiterId")
    List<JobApplication> findApplicationsReviewedByRecruiter(@Param("recruiterId") Long recruiterId);

    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.jobVacancy.id = :jobVacancyId")
    Long countApplicationsByJobVacancy(@Param("jobVacancyId") Long jobVacancyId);



}
