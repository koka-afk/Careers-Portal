package com.career.portal.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkEmailRequest {
    private List<Long> candidateIds;
    private String subject;
    private String body;
    private String signature;
}
