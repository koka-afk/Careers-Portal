package com.career.portal.dto;

import com.career.portal.models.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {
    private String jwt;
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private String phone;
    private String resumePath;
    private LocalDateTime createdAt;


}
