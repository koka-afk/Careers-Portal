package com.career.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDetails {

    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("language_id")
    private Integer languageId;

    private String stdin;

    @JsonProperty("cpu_time_limit")
    private Double cpuTimeLimit;

    @JsonProperty("memory_limit")
    private Integer memoryLimit;
}
