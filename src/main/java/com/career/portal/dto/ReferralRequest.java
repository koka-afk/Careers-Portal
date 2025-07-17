package com.career.portal.dto;

import lombok.Data;

@Data
public class ReferralRequest {
    private String candidateEmail;
    private Long jobVacancyId;
    private String message;
}
