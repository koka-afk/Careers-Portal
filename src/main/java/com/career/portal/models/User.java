package com.career.portal.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "profile_score")
    private double profileScore;

    @Column(name = "resume_path")
    private String resumePath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<JobApplication> applications;

    @OneToMany(mappedBy = "referredUser", cascade = CascadeType.ALL)
    private List<Referral> receivedReferrals;

    @OneToMany(mappedBy = "referrer", cascade = CascadeType.ALL)
    private List<Referral> sentReferrals;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }


}
