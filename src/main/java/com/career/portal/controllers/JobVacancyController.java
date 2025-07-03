package com.career.portal.controllers;

import com.career.portal.models.ExperienceLevel;
import com.career.portal.models.JobType;
import com.career.portal.models.JobVacancy;
import com.career.portal.services.JobVacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobVacancyController {

    private final JobVacancyService jobVacancyService;

    @GetMapping
    public ResponseEntity<List<JobVacancy>> getAllActiveJobs(){
        List<JobVacancy> jobs = jobVacancyService.getAllActiveVacancies();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobVacancy> getJobById(@PathVariable Long id){
        Optional<JobVacancy> job = jobVacancyService.findById(id);
        return job.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobVacancy>> searchJobs(@RequestParam String keyword){
        List<JobVacancy> jobs = jobVacancyService.searchVacancies(keyword);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<JobVacancy>> filterJobs(
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) ExperienceLevel experienceLevel){

        List<JobVacancy> jobs;
        if(experienceLevel != null && jobType != null){
            jobs = jobVacancyService.findActiveVacanciesByExperienceLevelAndJobType(experienceLevel, jobType);
        }else if(jobType != null){
            jobs = jobVacancyService.findByJobType(jobType);
        }else if(experienceLevel != null){
            jobs = jobVacancyService.findByExperienceLevel(experienceLevel);
        }else{
            jobs = jobVacancyService.getAllActiveVacancies();
        }

        return ResponseEntity.ok(jobs);
    }

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobVacancy> createJob(@RequestBody JobVacancy jobVacancy){
        JobVacancy createdJob = jobVacancyService.createJobVacancy(jobVacancy);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobVacancy> updateJob(@PathVariable Long id, @RequestBody JobVacancy jobVacancy){
        jobVacancy.setId(id);
        JobVacancy updatedJob = jobVacancyService.updateJobVacancy(jobVacancy);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Void> deactivateJob(@PathVariable Long id){
        jobVacancyService.deactivateJobVacancy(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recruiter/{recruiterId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<JobVacancy>> getJobsByRecruiter(@PathVariable Long recruiterId){
        List<JobVacancy> jobs = jobVacancyService.findVacanciesByRecruiter(recruiterId);
        return ResponseEntity.ok(jobs);
    }
}
