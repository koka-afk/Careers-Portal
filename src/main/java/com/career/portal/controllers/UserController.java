package com.career.portal.controllers;

import com.career.portal.models.User;
import com.career.portal.models.UserRole;
import com.career.portal.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final String UPLOAD_DIR = "uploads/resumes/";

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user){
        try{
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/profile/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('RECRUITER') or hasRole('EMPLOYEE')")
    public ResponseEntity<User> getUserProfile(@PathVariable Long id){
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('RECRUITER') or hasRole('EMPLOYEE')")
    public ResponseEntity<User> updateUserProfile(@PathVariable Long id, @RequestBody User user){
        user.setId(id);
        User updatedUser = userService.updateUser(user);
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> uploadResume(@PathVariable Long id, @RequestParam("file") MultipartFile file){
        try{
            if(file.isEmpty()){
                return ResponseEntity.badRequest().body("Please select a file to upload.");
            }

            Path uploadPath = Paths.get(UPLOAD_DIR);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            String fileName = id + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            userService.uploadResume(id, filePath.toString());

            return ResponseEntity.ok("Resume uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload resume.");
        }
    }

    @PutMapping("/{id}/score")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<User> updateProfileScore(@PathVariable Long id, @RequestParam Double score){
        User updatedUser = userService.updateProfileScore(id, score);
        return ResponseEntity.ok(updatedUser);
    }


}
