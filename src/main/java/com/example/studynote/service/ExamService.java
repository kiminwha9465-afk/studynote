package com.example.studynote.service;

import com.example.studynote.domain.Question;
import com.example.studynote.domain.Subject;
import com.example.studynote.repository.QuestionRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 모의고사 모드(과목별 랜덤) / 지정년도 모드(특정 회차 그대로)를 위한 문제 세트 구성. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamService {

    private final QuestionRepository questionRepository;

    public List<ExamSitting> listExamSittings() {
        return questionRepository.findDistinctExamSittings().stream()
                .map(row -> new ExamSitting((Integer) row[0], (Integer) row[1]))
                .toList();
    }

    /** 과목마다 questionsPerSubject개를 무작위로 뽑아 과목 순서대로 이어붙인다. subject가 null이면 전체 과목. */
    public List<Question> buildMockExam(Subject subject, int questionsPerSubject) {
        List<Subject> targets = (subject != null)
                ? List.of(subject)
                : List.of(Subject.values());
        List<Question> result = new ArrayList<>();
        for (Subject s : targets) {
            List<Question> pool = new ArrayList<>(questionRepository.findBySubjectOrderByIdAsc(s));
            Collections.shuffle(pool);
            int take = Math.min(questionsPerSubject, pool.size());
            result.addAll(pool.subList(0, take));
        }
        return result;
    }

    public List<Question> buildYearExam(int examYear, int examRound) {
        List<Question> questions = questionRepository.findByExamYearAndExamRoundOrderByIdAsc(examYear, examRound);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("해당 회차 문제를 찾을 수 없습니다. %d년 %d회".formatted(examYear, examRound));
        }
        return questions;
    }
}
