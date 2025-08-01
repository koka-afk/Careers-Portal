package com.career.portal.controllers;

import com.career.portal.models.Question;
import com.career.portal.models.User;
import com.career.portal.models.UserRole;
import com.career.portal.repositories.QuestionRepository;
import com.career.portal.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final QuestionRepository questionRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> roleUpdate) {
        try {
            UserRole newRole = UserRole.valueOf(roleUpdate.get("role").toUpperCase());
            User updatedUser = userService.changeUserRole(userId, newRole);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/questions")
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        if (question.getTestCases() != null) {
            question.getTestCases().forEach(testCase -> testCase.setQuestion(question));
        }
        Question newQuestion = questionRepository.save(question);
        return ResponseEntity.status(HttpStatus.CREATED).body(newQuestion);
    }

}
