package com.career.portal.dto;

import lombok.Data;

@Data
public class ReferralRequest {
    private Long referredUserId;
    private Long jobVacancyId;
    private String message;
}
