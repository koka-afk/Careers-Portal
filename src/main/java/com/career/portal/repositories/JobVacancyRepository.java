package com.career.portal.repositories;

import com.career.portal.models.ExperienceLevel;
import com.career.portal.models.JobType;
import com.career.portal.models.JobVacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobVacancyRepository extends JpaRepository<JobVacancy, Long> {
    List<JobVacancy> findByIsActiveTrue();

    List<JobVacancy> findByIsActiveTrueOrderByPostedAtDesc();

    List<JobVacancy> findByJobTypeAndIsActiveTrue(JobType jobType);

    List<JobVacancy> findByExperienceLevelAndIsActiveTrue(ExperienceLevel experienceLevel);

    @Query("SELECT jv FROM JobVacancy jv WHERE jv.isActive = true AND jv.experienceLevel = :experienceLevel AND jv.jobType = :jobType")
    List<JobVacancy> findActiveVacanciesByExperienceLevelAndJobType(@Param("experienceLevel")  ExperienceLevel experienceLevel, @Param("jobType") JobType jobType);

    @Query("SELECT jv FROM JobVacancy jv WHERE jv.isActive = true AND jv.applicationDeadline > :currentDate")
    List<JobVacancy> findActiveVacanciesWithinDeadline(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT jv FROM JobVacancy jv WHERE jv.isActive = true AND " +
            "LOWER(jv.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(jv.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<JobVacancy> searchByKeyword(@Param("keyword") String keyword);

    List<JobVacancy> findByPostedBy(Long recruiterId);

}
