package com.career.portal.repositories;

import com.career.portal.models.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<Assessment,Long> {
    Optional<Assessment> findByAssessmentToken(String assessmentToken);
}
