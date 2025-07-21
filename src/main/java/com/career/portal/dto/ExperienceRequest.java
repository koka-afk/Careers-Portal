package com.career.portal.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExperienceRequest {
    private String title;
    private String company;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
