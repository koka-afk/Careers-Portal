package com.career.portal.controllers;

import com.career.portal.models.ApplicationStatus;
import com.career.portal.models.JobApplication;
import com.career.portal.services.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<JobApplication> submitApplication(@RequestBody JobApplication application){
        try {
            JobApplication submittedApplication = jobApplicationService.submitApplication(application);
            return ResponseEntity.status(HttpStatus.CREATED).body(submittedApplication);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('RECRUITER')")
    public ResponseEntity<List<JobApplication>> getUserApplications(@PathVariable Long userId){
        List<JobApplication> applications = jobApplicationService.findApplicationsByUser(userId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<JobApplication>> getJobApplications(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "date") String sortBy){

        List<JobApplication> applications;
        if(sortBy.equals("score")){
            applications = jobApplicationService.findApplicationByJobVacancyOrderedByScore(jobId);
        }else{
            applications = jobApplicationService.findApplicationByJobVacancyOrderedByDate(jobId);
        }

        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('USER')")
    public ResponseEntity<JobApplication> getApplicationById(@PathVariable Long id){
        Optional<JobApplication> application = jobApplicationService.findById(id);
        return application.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobApplication> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            @RequestParam Long reviewerId){

        JobApplication application = jobApplicationService.updateApplicationStatus(id, status, reviewerId);
        return ResponseEntity.ok(application);
    }


    @PutMapping("/{id}/notes")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobApplication> addNotes(@PathVariable Long id, @RequestBody String notes){
        JobApplication updatedApplication = jobApplicationService.addNotesToApplication(id, notes);
        return ResponseEntity.ok(updatedApplication);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<JobApplication>> getApplicationsByStatus(@PathVariable ApplicationStatus status){
        List<JobApplication> applications = jobApplicationService.findApplicationByStatus(status);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/job/{jobId}/count")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Long> getApplicationCount(@PathVariable Long jobId){
        Long count = jobApplicationService.countApplicationsForJob(jobId);
        return ResponseEntity.ok(count);
    }

}
