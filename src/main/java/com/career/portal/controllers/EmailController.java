package com.career.portal.controllers;

import com.career.portal.models.User;
import com.career.portal.services.EmailService;
import com.career.portal.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final UserService userService;

    @PostMapping("/send")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> sendEmail(@RequestBody Map<String, String> emailRequest){
        try {
            String to = emailRequest.get("to");
            String subject = emailRequest.get("subject");
            String body = emailRequest.get("body");

            emailService.sendEmail(to, subject, body);
            return ResponseEntity.ok("Email sent successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/send-to-candidate/{candidateId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> sendEmailToCandidate(
            @PathVariable Long candidateId,
            @RequestBody Map<String, String> emailRequest){

        try {
            User candidate = userService.findById(candidateId)
                    .orElseThrow(() -> new IllegalArgumentException("Candidate not found."));

            String subject = emailRequest.get("subject");
            String body = emailRequest.get("body");
            String signature = emailRequest.get("signature");

            emailService.sendCandidateOutreachEmail(candidate, subject, body, signature);
            return ResponseEntity.ok("Email sent successfully to candidate");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/bulk-send")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> sendBulkEmail(@RequestBody Map<String, Object> bulkEmailRequest){
        try {
            @SuppressWarnings("unchecked")
            List<Long> candidateIds = (List<Long>) bulkEmailRequest.get("candidateIds");
            String subject = (String)  bulkEmailRequest.get("subject");
            String body = (String)  bulkEmailRequest.get("body");
            String signature = (String)  bulkEmailRequest.get("signature");

            for(Long candidateId : candidateIds){
                User candidate = userService.findById(candidateId)
                        .orElseThrow(() -> new IllegalArgumentException("Candidate not found."));

                emailService.sendCandidateOutreachEmail(candidate, subject, body, signature);
            }

            return ResponseEntity.ok("Bulk emails sent successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Failed to send bulk emails: " + e.getMessage());
        }
    }


}
