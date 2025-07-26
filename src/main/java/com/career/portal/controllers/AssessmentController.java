package com.career.portal.controllers;

import com.career.portal.dto.SubmissionDetails;
import com.career.portal.models.Assessment;
import com.career.portal.models.TestCase;
import com.career.portal.services.AssessmentService;
import com.career.portal.services.Judge0Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final Judge0Service judge0Service;

    @GetMapping("/{token}")
    public ResponseEntity<Assessment> getAssessment(@PathVariable String token) {
        try {
            Assessment assessment = assessmentService.getAssessmentByToken(token);
            return ResponseEntity.ok(assessment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{token}/submit")
    public ResponseEntity<Assessment> submitAssessment(@PathVariable String token, @RequestBody Map<String, Object> payload) {
        String code = (String) payload.get("code");
        int languageId = Integer.parseInt(payload.get("languageId").toString());
        Assessment result = assessmentService.submitAssessment(token, code, languageId);
        return ResponseEntity.ok(result);
    }

//    @PostMapping("/{token}/run")
//    public ResponseEntity<Map<String, Object>> runCodeAgainstTestCase(@PathVariable String token, @RequestBody Map<String, Object> payload) {
//        Assessment assessment = assessmentService.getAssessmentByToken(token);
//
//        String code = (String) payload.get("code");
//        int languageId = Integer.parseInt(payload.get("languageId").toString());
//        Long testCaseId = Long.valueOf(payload.get("testCaseId").toString());
//
//        TestCase testCase = assessment.getQuestions().stream()
//                .flatMap(q -> q.getTestCases().stream())
//                .filter(tc -> tc.getId().equals(testCaseId) && tc.isPublic())
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("Public test case not found."));
//
//        Double timeLimit = 2.0; // 2 seconds
//        Integer memoryLimit = 128000; // 128 MB
//
//        Map<String, Object> result = judge0Service.createSubmission(code, testCase.getInput(), languageId, timeLimit, memoryLimit);
//
//        return ResponseEntity.ok(result);
//    }

    @PostMapping("/{token}/run")
    public ResponseEntity<Map<String, Object>> runCodeAgainstTestCase(
            @PathVariable String token,
            @RequestBody Map<String, Object> payload) throws JsonProcessingException {

        Assessment assessment = assessmentService.getAssessmentByToken(token);

        String code = (String) payload.get("code");
        int languageId = Integer.parseInt(payload.get("languageId").toString());
        Long testCaseId = Long.valueOf(payload.get("testCaseId").toString());

        TestCase testCase = assessment.getQuestions().stream()
                .flatMap(q -> q.getTestCases().stream())
                .filter(tc -> tc.getId().equals(testCaseId) && tc.isPublic())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Public test case not found."));

        SubmissionDetails submissionDetails = SubmissionDetails.builder()
                .sourceCode(code)
                .languageId(languageId)
                .stdin(testCase.getInput())
                .cpuTimeLimit(2.0)
                .memoryLimit(128000)
                .build();

        String responseJson = judge0Service.createSubmission(submissionDetails);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result;

        try {
            result = mapper.readValue(responseJson, new TypeReference<>() {});

            decodeJudge0Response(result);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Judge0 response", e);
        }

        return ResponseEntity.ok(result);
    }

    private void decodeJudge0Response(Map<String, Object> result) {
        String[] encodedFields = {"stdout", "stderr", "compile_output", "message"};

        for (String field : encodedFields) {
            if (result.containsKey(field)) {
                String encodedValue = (String) result.get(field);
                if (encodedValue != null && !encodedValue.isEmpty()) {
                    try {
                        String decodedValue = new String(
                                Base64.getDecoder().decode(encodedValue),
                                StandardCharsets.UTF_8
                        );
                        result.put(field, decodedValue);
                    } catch (IllegalArgumentException e) {
                        log.warn("Failed to decode {} field: {}", field, e.getMessage());
                    }
                }
            }
        }

        if (result.containsKey("token")) {
            String encodedToken = (String) result.get("token");
            if (encodedToken != null) {
                result.put("token", new String(
                        Base64.getDecoder().decode(encodedToken),
                        StandardCharsets.UTF_8
                ));
            }
        }
    }

}