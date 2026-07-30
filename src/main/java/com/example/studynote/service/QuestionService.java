package com.example.studynote.service;

import com.example.studynote.domain.Question;
import com.example.studynote.domain.Subject;
import com.example.studynote.repository.QuestionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<Question> findQuestions(Subject subject) {
        if (subject == null) {
            return questionRepository.findAllByOrderByIdAsc();
        }
        return questionRepository.findBySubjectOrderByIdAsc(subject);
    }

    public Question getQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문제입니다. id=" + id));
    }
}
