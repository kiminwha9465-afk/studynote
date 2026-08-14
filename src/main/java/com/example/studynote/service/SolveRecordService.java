package com.example.studynote.service;

import com.example.studynote.domain.ExamSession;
import com.example.studynote.domain.Question;
import com.example.studynote.domain.SolveMode;
import com.example.studynote.domain.SolveRecord;
import com.example.studynote.repository.ExamSessionRepository;
import com.example.studynote.repository.SolveRecordRepository;
import java.util.ArrayList;
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
    private final ExamSessionRepository examSessionRepository;
    private final QuestionService questionService;

    @Transactional
    public ExamSession createSession(String title, SolveMode mode) {
        return examSessionRepository.save(new ExamSession(title, mode));
    }

    public ExamSession getExamSession(Long id) {
        return examSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 시험 세션: " + id));
    }

    @Transactional
    public SolveResult submitAnswer(Long questionId, int selectedAnswer, SolveMode mode) {
        return submitAnswer(questionId, selectedAnswer, mode, (Long) null);
    }

    @Transactional
    public SolveResult submitAnswer(Long questionId, int selectedAnswer, SolveMode mode, Long examSessionId) {
        Question question = questionService.getQuestion(questionId);
        boolean correct = question.isCorrectAnswer(selectedAnswer);
        SolveRecord record = new SolveRecord(question, selectedAnswer, correct, mode);
        if (examSessionId != null) {
            record.setExamSession(examSessionRepository.getReferenceById(examSessionId));
        }
        solveRecordRepository.save(record);
        return new SolveResult(question, selectedAnswer, correct);
    }

    /**
     * 임의풀이: 문제별 최근 기록이 오답인 것만 표시. 나중에 맞히면 사라짐.
     */
    public List<SolveRecord> findWrongNotesPractice() {
        List<SolveRecord> all = solveRecordRepository.findByModeOrderBySolvedAtDesc(SolveMode.PRACTICE);

        Map<Long, SolveRecord> latestByQuestion = new LinkedHashMap<>();
        for (SolveRecord record : all) {
            latestByQuestion.putIfAbsent(record.getQuestion().getId(), record);
        }

        return latestByQuestion.values().stream()
                .filter(r -> !r.isCorrect())
                .toList();
    }

    /** 세션 목록 (최신순). 풀이기록이 없는 세션(뒤로가기로 나간 경우)은 제외. */
    public List<SessionSummary> findSessionSummaries(SolveMode mode) {
        List<ExamSession> sessions = examSessionRepository.findByModeOrderByCreatedAtDesc(mode);
        return sessions.stream().map(s -> {
            List<SolveRecord> records = solveRecordRepository.findByExamSessionOrderBySolvedAtAsc(s);
            int total = records.size();
            int correct = (int) records.stream().filter(SolveRecord::isCorrect).count();
            return new SessionSummary(s, total, correct);
        }).filter(s -> s.totalCount() > 0).toList();
    }

    /** 세션 상세: 오답(나중에 맞혔는지 여부 포함) + 정답 */
    public SessionDetail getSessionDetail(Long sessionId) {
        ExamSession session = getExamSession(sessionId);
        List<SolveRecord> all = solveRecordRepository.findByExamSessionOrderBySolvedAtAsc(session);

        List<WrongAnswerDetail> wrongDetails = new ArrayList<>();
        List<SolveRecord> correctList = new ArrayList<>();

        for (SolveRecord r : all) {
            if (r.isCorrect()) {
                correctList.add(r);
            } else {
                boolean laterCorrected = solveRecordRepository.existsByQuestionIdAndCorrectTrueAndSolvedAtAfter(
                        r.getQuestion().getId(), r.getSolvedAt());
                wrongDetails.add(new WrongAnswerDetail(r, laterCorrected));
            }
        }

        return new SessionDetail(session, wrongDetails, correctList);
    }

    /** 세션의 오답 question ID 목록 (오답 재시험용) */
    public List<Long> findWrongQuestionIdsForSession(Long sessionId) {
        return getSessionDetail(sessionId).wrongDetails().stream()
                .map(d -> d.record().getQuestion().getId())
                .toList();
    }

    /** 임의풀이 오답 question ID 목록 (오답 재시험용) */
    public List<Long> findWrongQuestionIdsForPractice() {
        return findWrongNotesPractice().stream()
                .map(r -> r.getQuestion().getId())
                .toList();
    }

    @Transactional
    public void saveSessionDuration(Long sessionId, int durationSeconds) {
        ExamSession session = getExamSession(sessionId);
        session.setDurationSeconds(durationSeconds);
        examSessionRepository.save(session);
    }

    @Transactional
    public void saveMemo(Long recordId, String memo) {
        SolveRecord record = solveRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 풀이 기록입니다. id=" + recordId));
        record.setMemo(memo);
    }
}
