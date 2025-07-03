package com.career.portal.services;

import com.career.portal.models.ExperienceLevel;
import com.career.portal.models.JobType;
import com.career.portal.models.JobVacancy;
import com.career.portal.repositories.JobVacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class JobVacancyService {

    private final JobVacancyRepository jobVacancyRepository;

    public JobVacancy createJobVacancy(JobVacancy jobVacancy){
        return jobVacancyRepository.save(jobVacancy);
    }

    public List<JobVacancy> getAllActiveVacancies(){
        return jobVacancyRepository.findByIsActiveTrueOrderByPostedAtDesc();
    }

    public List<JobVacancy> getActiveVacanciesWithinDeadline(){
        return jobVacancyRepository.findActiveVacanciesWithinDeadline(LocalDateTime.now());
    }

    public Optional<JobVacancy> findById(Long id){
        return jobVacancyRepository.findById(id);
    }

    public List<JobVacancy> findByJobType(JobType jobType){
        return jobVacancyRepository.findByJobTypeAndIsActiveTrue(jobType);
    }

    public List<JobVacancy> findByExperienceLevel(ExperienceLevel experienceLevel){
        return jobVacancyRepository.findByExperienceLevelAndIsActiveTrue(experienceLevel);
    }

    public List<JobVacancy> findActiveVacanciesByExperienceLevelAndJobType(ExperienceLevel experienceLevel, JobType jobType){
        return jobVacancyRepository.findActiveVacanciesByExperienceLevelAndJobType(experienceLevel, jobType);
    }

    public List<JobVacancy> searchVacancies(String keyword){
        return jobVacancyRepository.searchByKeyword(keyword);
    }

    public JobVacancy updateJobVacancy(JobVacancy jobVacancy){
        return jobVacancyRepository.save(jobVacancy);
    }

    public void deactivateJobVacancy(Long id){
        JobVacancy vacancy = jobVacancyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job Vacancy Not Found"));
        vacancy.setActive(false);
        jobVacancyRepository.save(vacancy);
    }

    public List<JobVacancy> findVacanciesByRecruiter(Long recruiterId){
        return jobVacancyRepository.findByPostedBy(recruiterId);
    }



}
