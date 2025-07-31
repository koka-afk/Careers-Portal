package com.career.portal.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "assessments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "assessment_questions",
            joinColumns = @JoinColumn(name = "assessment_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions;

    @Column(unique = true, nullable = false)
    private String assessmentToken;

    private Integer score;
    private LocalDateTime completedAt;
    private LocalDateTime expiresAt;

    @Column(columnDefinition = "TEXT")
    private String candidateCode;

    @PrePersist
    protected void onCreate() {
        this.assessmentToken = UUID.randomUUID().toString();
        this.expiresAt = LocalDateTime.now().plusDays(7);
    }
}