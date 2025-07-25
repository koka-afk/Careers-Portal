package com.career.portal.repositories;

import com.career.portal.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question,Long> {
    @Query(value = "SELECT * FROM questions ORDER BY RAND() LIMIT 1", nativeQuery = true)
    List<Question> findRandomQuestions();
}
