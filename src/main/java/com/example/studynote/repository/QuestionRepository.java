package com.example.studynote.repository;

import com.example.studynote.domain.Question;
import com.example.studynote.domain.Subject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findBySubjectOrderByIdAsc(Subject subject);

    List<Question> findAllByOrderByIdAsc();
}
