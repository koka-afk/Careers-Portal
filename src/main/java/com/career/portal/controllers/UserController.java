package com.career.portal.controllers;

import com.career.portal.dto.UserProfileUpdateRequest;
import com.career.portal.models.User;
import com.career.portal.models.UserRole;
import com.career.portal.services.CloudinaryService;
import com.career.portal.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final String UPLOAD_DIR = "uploads/resumes/";
    private final CloudinaryService cloudinaryService;


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user){
        try{
            userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful. Please check your email to verify your account.");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/profile/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('RECRUITER')")
    public ResponseEntity<User> getUserProfile(@PathVariable Long id){
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('RECRUITER') or hasRole('EMPLOYEE')")
    public ResponseEntity<User> updateUserProfile(@PathVariable Long id, @RequestBody UserProfileUpdateRequest updateRequest){
        User updatedUser = userService.updateUserProfile(id, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/candidates")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<User>> getCandidates(@RequestParam(defaultValue = "score") String sortBy){
        List<User> candidates;
        if(sortBy.equals("score")){
            candidates = userService.findCandidatesOrderedByScore();
        }else{
            candidates = userService.findUsersByRole(UserRole.USER);
        }

        return ResponseEntity.ok(candidates);
    }

    @PostMapping("/{id}/resume")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<User> uploadResume(@PathVariable Long id, @RequestParam("file") MultipartFile file){
        try{
            if(file.isEmpty()){
                return ResponseEntity.badRequest().build();
            }

            User user = userService.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (user.getResumePublicId() != null && !user.getResumePublicId().isEmpty()) {
                cloudinaryService.deleteFile(user.getResumePublicId());
            }

            Map<String, String> uploadResult = cloudinaryService.uploadFile(file, id);
            String fileUrl = uploadResult.get("url");
            String publicId = uploadResult.get("public_id");

            User updatedUser = userService.uploadResume(id, fileUrl, publicId);

            return ResponseEntity.ok(updatedUser);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{userId}/resume/view")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Void> viewResume(@PathVariable Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getResumePath() != null && !user.getResumePath().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(user.getResumePath()))
                    .build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private String extractPublicIdFromPath(String cloudinaryUrl) {

        if (cloudinaryUrl.contains("/upload/")) {
            String[] parts = cloudinaryUrl.split("/upload/");
            if (parts.length > 1) {
                String afterUpload = parts[1];
                if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
                    afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
                }
                return afterUpload.replaceAll("\\.[^.]+$", "");
            }
        }

        return cloudinaryUrl;
    }

    @PutMapping("/{id}/score")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<User> updateProfileScore(@PathVariable Long id, @RequestParam Double score){
        User updatedUser = userService.updateProfileScore(id, score);
        return ResponseEntity.ok(updatedUser);
    }


}
