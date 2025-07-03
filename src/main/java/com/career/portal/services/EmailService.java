package com.career.portal.services;

import com.career.portal.models.EmailTemplate;
import com.career.portal.models.EmailTemplateType;
import com.career.portal.models.User;
import com.career.portal.repositories.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
