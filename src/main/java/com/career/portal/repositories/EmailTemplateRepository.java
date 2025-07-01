package com.career.portal.repositories;

import com.career.portal.models.EmailTemplate;
import com.career.portal.models.EmailTemplateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    List<EmailTemplate> findByIsActiveTrue();

    List<EmailTemplate> findByTemplateType(EmailTemplateType templateType);

    Optional<EmailTemplate> findByTemplateTypeAndIsActiveTrue(EmailTemplateType templateType);

    List<EmailTemplate> findByCreatedBy(Long createdBy);

}
