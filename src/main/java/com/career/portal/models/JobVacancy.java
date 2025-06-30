package com.career.portal.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job_vacancy")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobVacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    private String location;

    @Column(name = "job_type")
    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Column(name = "experience_level")
    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    @Column(name = "min_salary")
    private Double minSalary;

    @Column(name = "max_salary")
    private Double maxSalary;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "posted_by")
    private Long postedBy;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @Column(name = "application_deadline")
    private LocalDateTime applicationDeadline;

    @OneToMany(mappedBy = "jobVacancy", cascade = CascadeType.ALL)
    private List<JobApplication> applications;

    @PrePersist
    protected void onCreate(){
        postedAt = LocalDateTime.now();
    }

}
