package com.career.portal.services;

import com.career.portal.dto.Judge0SubmissionRequest;
import com.career.portal.dto.SubmissionDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class Judge0Service {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Value("${JUDGE0_API_URL}")
    private String judge0ApiUrl;

    @Value("${RAPID_API_KEY}")
    private String rapidApiKey;

    @Value("${RAPID_API_HOST}")
    private String rapidApiHost;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
//    public Map<String, Object> createSubmission(String sourceCode, String stdin, int languageId) {
//        return createSubmission(sourceCode, stdin, languageId, null, null);
//    }
//

    public String createSubmission(SubmissionDetails submissionDetails) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("X-RapidAPI-Key", rapidApiKey);
            headers.set("X-RapidAPI-Host", rapidApiHost);

            String encodedSource = Base64.getEncoder().encodeToString(
                    submissionDetails.getSourceCode().getBytes(StandardCharsets.UTF_8)
            );
            String encodedStdin = submissionDetails.getStdin() != null ?
                    Base64.getEncoder().encodeToString(
                            submissionDetails.getStdin().getBytes(StandardCharsets.UTF_8)
                    ) : "";

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("language_id", submissionDetails.getLanguageId());
            body.put("source_code", encodedSource);
            body.put("stdin", encodedStdin);

            if (submissionDetails.getCpuTimeLimit() != null) {
                body.put("cpu_time_limit", submissionDetails.getCpuTimeLimit());
            }
            if (submissionDetails.getMemoryLimit() != null) {
                body.put("memory_limit", submissionDetails.getMemoryLimit());
            }

            String url = UriComponentsBuilder.fromHttpUrl(judge0ApiUrl + "/submissions")
                    .queryParam("base64_encoded", "true")
                    .queryParam("wait", "true")
                    .queryParam("fields", "*")
                    .toUriString();

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

//            log.debug("Request to Judge0:\nURL: {}\nHeaders: {}\nBody: {}",
//                    url, headers, new ObjectMapper().writeValueAsString(body));

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Judge0 API call failed", e);
            throw new RuntimeException("Failed to submit code to Judge0", e);
        }
    }

}