package com.career.portal.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EducationRequest {
    private String school;
    private String degree;
    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
}
