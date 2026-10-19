package com.example.studynote.service;

import com.example.studynote.domain.PracticalQuestion;
import com.example.studynote.repository.PracticalQuestionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PracticalService {

    private final PracticalQuestionRepository practicalQuestionRepository;

    public List<ExamSitting> listSittings() {
        return practicalQuestionRepository.findDistinctExamSittings().stream()
                .map(row -> new ExamSitting((Integer) row[0], (Integer) row[1]))
                .toList();
    }

    public List<PracticalQuestion> buildExam(int examYear, int examRound) {
        List<PracticalQuestion> questions =
                practicalQuestionRepository.findByExamYearAndExamRoundOrderByQuestionNoAsc(examYear, examRound);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException(
                    "해당 회차 문제를 찾을 수 없습니다. %d년 %d회".formatted(examYear, examRound));
        }
        return questions;
    }

    public PracticalQuestion findById(Long id) {
        return practicalQuestionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다. id=" + id));
    }
}
