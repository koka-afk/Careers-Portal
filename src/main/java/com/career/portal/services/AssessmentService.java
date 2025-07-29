package com.career.portal.services;

import com.career.portal.dto.SubmissionDetails;
import com.career.portal.dto.TestCaseDto;
import com.career.portal.models.Assessment;
import com.career.portal.models.JobApplication;
import com.career.portal.models.Question;
import com.career.portal.models.TestCase;
import com.career.portal.repositories.AssessmentRepository;
import com.career.portal.repositories.QuestionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final Judge0Service judge0Service;
    private final EmailService emailService;
    public Assessment createAssessment(JobApplication jobApplication) {
        Assessment assessment = new Assessment();
        assessment.setJobApplication(jobApplication);

        List<Question> randomQuestions = questionRepository.findRandomQuestions();
        assessment.setQuestions(randomQuestions);

        return assessmentRepository.save(assessment);
    }

    public Assessment getAssessmentByToken(String token) {
        return assessmentRepository.findByAssessmentToken(token)
                .filter(assessment -> assessment.getExpiresAt().isAfter(LocalDateTime.now()) && assessment.getCompletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired assessment token."));
    }

    public Assessment submitAssessment(String token, String candidateCode, int languageId) {
        Assessment assessment = getAssessmentByToken(token);
        assessment.setCandidateCode(candidateCode);

        int passedTestCases = 0;
        Question question = assessment.getQuestions().get(0);

        List<TestCase> privateTestCases = question.getTestCases().stream()
                .filter(tc -> !tc.getIsPublic())
                .toList();

        ObjectMapper mapper = new ObjectMapper();

        for (TestCase testCase : privateTestCases) {
            SubmissionDetails submissionDetails = SubmissionDetails.builder()
                    .sourceCode(candidateCode)
                    .languageId(languageId)
                    .stdin(testCase.getInput())
                    .cpuTimeLimit(question.getCpuTimeLimit())
                    .memoryLimit(question.getMemoryLimit())
                    .build();

            try {
                String responseJson = judge0Service.createSubmission(submissionDetails);
                Map<String, Object> result = mapper.readValue(responseJson, new TypeReference<>() {});

                Map<String, Object> status = (Map<String, Object>) result.get("status");
                if (status != null && "Accepted".equals(status.get("description"))) {
                    String encodedOutput = (String) result.get("stdout");
                    String output = encodedOutput != null ?
                            new String(Base64.getDecoder().decode(encodedOutput), StandardCharsets.UTF_8) :
                            null;

                    if (output != null && output.trim().equals(testCase.getExpectedOutput().trim())) {
                        passedTestCases++;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error during code submission or result parsing", e);
            }
        }

        int score = (int) (((double) passedTestCases / privateTestCases.size()) * 100);
        assessment.setScore(score);
        assessment.setCompletedAt(LocalDateTime.now());

        JobApplication jobApplication = assessment.getJobApplication();
        jobApplication.setAssessmentScore(score);
        emailService.sendAssessmentConfirmationEmail(jobApplication.getUser(), jobApplication.getJobVacancy());

        return assessmentRepository.save(assessment);
    }

    public List<TestCaseDto> getQuestionTestCases(String assessmentToken, Long questionId) {
        Assessment assessment = getAssessmentByToken(assessmentToken);

        Question question = assessment.getQuestions().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Question not found in assessment"));

        return question.getTestCases().stream()
                .map(tc -> new TestCaseDto(
                        tc.getId(),
                        tc.getInput(),
                        tc.getExpectedOutput(),
                        tc.getIsPublic()
                ))
                .toList();
    }
}