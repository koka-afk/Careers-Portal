package com.career.portal.services;

import com.career.portal.models.EmailTemplate;
import com.career.portal.models.EmailTemplateType;
import com.career.portal.models.JobVacancy;
import com.career.portal.models.User;
import com.career.portal.repositories.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateRepository emailTemplateRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public void sendEmail(String to, String subject, String body){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        }catch (Exception e){
            log.error("Failed to Send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendTemplatedEmail(String to, EmailTemplateType templateType, Map<String, String> variables, String signature){
        EmailTemplate template = emailTemplateRepository.findByTemplateTypeAndIsActiveTrue(templateType)
                .orElseThrow(() -> new IllegalArgumentException("Email template not found: " + templateType));

        String subject = replaceVariables(template.getSubject(), variables);
        String body = replaceVariables(template.getBody(), variables);

        if(signature != null && !signature.isEmpty()){
            body += "\n\n"  + signature;
        }

        sendEmail(to, subject, body);
    }

    public void sendCandidateOutreachEmail(User candidate, String subject, String body, String signature){
        String fullBody = body;
        if(signature != null && !signature.isEmpty()){
            fullBody += "\n\n"  + signature;
        }

        sendEmail(candidate.getEmail(), subject, fullBody);
    }

    private String replaceVariables(String template, Map<String, String> variables){
        String result = template;
        for(Map.Entry<String, String> entry : variables.entrySet()){
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }

    public EmailTemplate createEmailTemplate(EmailTemplate template){
        return emailTemplateRepository.save(template);
    }

    public EmailTemplate updateEmailTemplate(EmailTemplate template){
        return emailTemplateRepository.save(template);
    }

    public void deleteEmailTemplate(Long id){
        emailTemplateRepository.deleteById(id);
    }

    public void sendVerificationEmail(String toEmail, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Verify Your Email Address");
            message.setText(buildVerificationEmailContent(verificationToken));

            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private String buildVerificationEmailContent(String verificationToken) {
        String verificationUrl = frontendUrl + "/account-verified?token=" + verificationToken;

        return """
                Welcome to Noon Careers!
                
                Thank you for registering with us. To complete your registration and activate your account, 
                please click the link below to verify your email address:
                
                %s
                
                This link will expire in 24 hours for security reasons.
                
                If you didn't create an account with us, please ignore this email.
                
                Best regards,
                Noon Careers
                """.formatted(verificationUrl);
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Reset Your Password");
            message.setText(buildPasswordResetEmailContent(resetToken));

            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String buildPasswordResetEmailContent(String resetToken) {
        String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;

        return """
                Password Reset Request
                
                You have requested to reset your password. Click the link below to reset your password:
                
                %s
                
                This link will expire in 1 hour for security reasons.
                
                If you didn't request a password reset, please ignore this email.
                
                Best regards,
                Noon Careers
                """.formatted(resetUrl);
    }

    public void sendShortlistEmail(User candidate, JobVacancy jobVacancy, String assessmentToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(candidate.getEmail());
            message.setSubject("Congratulations! You've been shortlisted for the " + jobVacancy.getTitle() + " position");
            String assessmentLink = "http://localhost:5173/assessment/" + assessmentToken;
            String emailBody = String.format(
                    "Dear %s,\n\nCongratulations! You have been shortlisted for the %s position. Please complete the coding assessment at the following link:\n%s\n\nBest regards,\nThe Careers Portal Team",
                    candidate.getFirstName(),
                    jobVacancy.getTitle(),
                    assessmentLink
            );

            message.setText(emailBody);
            mailSender.send(message);
            log.info("Shortlist email sent successfully to: {}", candidate.getEmail());
        } catch (Exception e) {
            log.error("Failed to send shortlist email to: {}", candidate.getEmail(), e);
            throw new RuntimeException("Failed to send shortlist email", e);
        }
    }

    private String buildShortlistEmailContent(User candidate, JobVacancy jobVacancy, String assessmentLink) {
        if (assessmentLink == null || assessmentLink.isEmpty()) {
            assessmentLink = "https://www.hackerearth.com/challenge/test/your-test-id/";
        }

        return String.format(
                """
                Dear %s,
    
                Congratulations! We are pleased to inform you that you have been shortlisted for the position of %s.
                We were very impressed with your application and would like to invite you to the next stage of our recruitment process, which is a coding assessment.
    
                Please use the following link to access the assessment:
                %s
    
                The assessment should take approximately 60-90 minutes to complete. Please ensure you have a stable internet connection and a quiet environment.
    
                We wish you the best of luck!
    
                Best regards,
                The Noon Careers Team
                """,
                candidate.getFirstName(),
                jobVacancy.getTitle(),
                assessmentLink
        );
    }

}
