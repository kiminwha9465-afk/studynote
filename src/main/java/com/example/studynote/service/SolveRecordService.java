package com.example.studynote.service;

import com.example.studynote.domain.Question;
import com.example.studynote.domain.SolveRecord;
import com.example.studynote.repository.SolveRecordRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolveRecordService {

    private final SolveRecordRepository solveRecordRepository;
    private final QuestionService questionService;

    @Transactional
    public SolveResult submitAnswer(Long questionId, int selectedAnswer) {
        Question question = questionService.getQuestion(questionId);
        boolean correct = question.getAnswerNo() != null && question.getAnswerNo() == selectedAnswer;
        solveRecordRepository.save(new SolveRecord(question, selectedAnswer, correct));
        return new SolveResult(question, selectedAnswer, correct);
    }

    /** 문제별 가장 최근 풀이 기록 중, 최근 결과가 오답인 것만 오답노트로 노출한다. */
    public List<SolveRecord> findWrongNotes() {
        List<SolveRecord> all = solveRecordRepository.findAllByOrderBySolvedAtDesc();

        Map<Long, SolveRecord> latestByQuestion = new LinkedHashMap<>();
        for (SolveRecord record : all) {
            latestByQuestion.putIfAbsent(record.getQuestion().getId(), record);
        }

        return latestByQuestion.values().stream()
                .filter(record -> !record.isCorrect())
                .toList();
    }

    @Transactional
    public void saveMemo(Long recordId, String memo) {
        SolveRecord record = solveRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 풀이 기록입니다. id=" + recordId));
        record.setMemo(memo);
    }
}
