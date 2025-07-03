package com.career.portal.dto;

import lombok.Data;

@Data
public class JobApplicationRequest {
    private Long jobVacancyId;
    private String coverLetter;
}
