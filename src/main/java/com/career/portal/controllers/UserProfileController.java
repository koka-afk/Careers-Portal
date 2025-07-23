package com.career.portal.controllers;

import com.career.portal.dto.EducationRequest;
import com.career.portal.dto.ExperienceRequest;
import com.career.portal.models.Education;
import com.career.portal.models.Experience;
import com.career.portal.repositories.EducationRepository;
import com.career.portal.repositories.ExperienceRepository;
import com.career.portal.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;

    @GetMapping("/{userId}/education")
    @PreAuthorize("#userId == authentication.principal.id OR hasRole('RECRUITER')")
    public ResponseEntity<List<Education>> getEducationList(@PathVariable Long userId){
        try{
            List<Education> userEducations = educationRepository.findEducationsByUserId(userId);
            return ResponseEntity.ok(userEducations);
        }catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/education")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<Education> addEducation(@PathVariable Long userId, @RequestBody EducationRequest educationRequest, Authentication authentication) {
        try{
            Education newEducation = userService.addEducation(userId, educationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(newEducation);
        }catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/education/{educationId}")
    @PreAuthorize("@userService.canModifyEducation(#educationId, authentication.principal.id)")
    public ResponseEntity<Education> updateEducation(@PathVariable Long educationId, @RequestBody EducationRequest educationRequest) {
        try {
            Education updatedEducation = userService.updateEducation(educationId, educationRequest);
            return ResponseEntity.ok(updatedEducation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/education/{educationId}")
    @PreAuthorize("@userService.canModifyEducation(#educationId, authentication.principal.id)")
    public ResponseEntity<Void> deleteEducation(@PathVariable Long educationId) {
        try {
            userService.deleteEducation(educationId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}/experience")
    @PreAuthorize("#userId == authentication.principal.id OR hasRole('RECRUITER')")
    public ResponseEntity<List<Experience>> getExperienceList(@PathVariable Long userId){
        try {
            List<Experience> experienceList = experienceRepository.findByUserId(userId);
            return ResponseEntity.ok(experienceList);
        }catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/experience")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<Experience> addExperience(@PathVariable Long userId, @RequestBody ExperienceRequest experienceRequest) {
        try {
            Experience newExperience = userService.addExperience(userId, experienceRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(newExperience);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/experience/{experienceId}")
    @PreAuthorize("@userService.canModifyExperience(#experienceId, authentication.principal.id)")
    public ResponseEntity<Experience> updateExperience(@PathVariable Long experienceId, @RequestBody ExperienceRequest experienceRequest) {
        try {
            Experience updatedExperience = userService.updateExperience(experienceId, experienceRequest);
            return ResponseEntity.ok(updatedExperience);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/experience/{experienceId}")
    @PreAuthorize("@userService.canModifyExperience(#experienceId, authentication.principal.id)")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long experienceId) {
        try {
            userService.deleteExperience(experienceId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
