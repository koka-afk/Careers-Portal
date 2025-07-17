package com.career.portal.services;

import com.career.portal.models.ApplicationStatus;
import com.career.portal.models.JobApplication;
import com.career.portal.models.JobVacancy;
import com.career.portal.models.User;
import com.career.portal.repositories.JobApplicationRepository;
import com.career.portal.repositories.JobVacancyRepository;
import com.career.portal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final JobVacancyRepository jobVacancyRepository;

    public JobApplication submitApplication(JobApplication jobApplication){
        User user = userRepository.findById(jobApplication.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        JobVacancy jobVacancy = jobVacancyRepository.findById(jobApplication.getJobVacancy().getId())
                .orElseThrow(() -> new IllegalArgumentException("Job vacancy not found"));

        jobApplication.setUser(user);
        jobApplication.setJobVacancy(jobVacancy);

        if(jobApplicationRepository.existsByUserIdAndJobVacancyId(
                jobApplication.getUser().getId(),
                jobApplication.getJobVacancy().getId())){
            throw new IllegalStateException("User has already applied for this job.");
        }

        return jobApplicationRepository.save(jobApplication);
    }

    public List<JobApplication> findApplicationsByUser(Long userId){
        return jobApplicationRepository.findByUserId(userId);
    }

    public List<JobApplication> findApplicationByJobVacancy(Long jobVacancyId){
        return jobApplicationRepository.findByJobVacancyId(jobVacancyId);
    }

    public List<JobApplication> findApplicationByJobVacancyOrderedByScore(Long jobVacancyId){
        return jobApplicationRepository.findByJobVacancyIdOrderByProfileScoreDesc(jobVacancyId);
    }

    public List<JobApplication> findApplicationByJobVacancyOrderedByDate(Long jobVacancyId){
        return jobApplicationRepository.findByJobVacancyIdOrderByAppliedAtDesc(jobVacancyId);
    }

    public Optional<JobApplication> findById(Long id){
        return jobApplicationRepository.findById(id);
    }

    public JobApplication updateApplicationStatus(Long applicationId, ApplicationStatus status, Long reviewerId){
        JobApplication application = jobApplicationRepository.findById(applicationId).
                orElseThrow(() -> new IllegalArgumentException("Application not found."));

        application.setStatus(status);
        application.setReviewedBy(reviewerId);
        application.setReviewedAt(LocalDateTime.now());

        return jobApplicationRepository.save(application);
    }

    public JobApplication addNotesToApplication(Long applicationId, String notes){
        JobApplication application = jobApplicationRepository.findById(applicationId).
                orElseThrow(() -> new IllegalArgumentException("Application not found."));

        application.setNotes(notes);
        return jobApplicationRepository.save(application);
    }

    public List<JobApplication> findApplicationByStatus(ApplicationStatus status){
        return jobApplicationRepository.findByStatus(status);
    }

    public Long countApplicationsForJob(Long jobVacancyId){
        return jobApplicationRepository.countApplicationsByJobVacancy(jobVacancyId);
    }

    public JobApplication markAsReferred(Long applicationId){
        JobApplication application =  jobApplicationRepository.findById(applicationId).
                orElseThrow(() -> new IllegalArgumentException("Application not found."));

        application.setHasReferral(true);
        return jobApplicationRepository.save(application);
    }

}
