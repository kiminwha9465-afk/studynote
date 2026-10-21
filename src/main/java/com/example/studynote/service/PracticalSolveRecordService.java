package com.example.studynote.service;

import com.example.studynote.domain.PracticalSolveRecord;
import com.example.studynote.repository.PracticalSolveRecordRepository;
import com.example.studynote.web.PracticalResultView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PracticalSolveRecordService {

    private final PracticalSolveRecordRepository repo;

    @Transactional
    public void saveResults(String sessionTitle, List<PracticalResultView.Item> items) {
        String sessionKey = UUID.randomUUID().toString();
        for (PracticalResultView.Item item : items) {
            repo.save(new PracticalSolveRecord(
                    item.question(), item.submitted(), item.correct(), sessionTitle, sessionKey));
        }
    }

    @Transactional(readOnly = true)
    public List<PracticalWrongSession> findWrongSessions() {
        List<PracticalSolveRecord> all = repo.findAllByOrderBySolvedAtDesc();

        // sessionKey 기준으로 그룹핑 (최신 순 유지)
        Map<String, List<PracticalSolveRecord>> grouped = new LinkedHashMap<>();
        for (PracticalSolveRecord r : all) {
            grouped.computeIfAbsent(r.getSessionKey(), k -> new ArrayList<>()).add(r);
        }

        List<PracticalWrongSession> sessions = new ArrayList<>();
        for (List<PracticalSolveRecord> records : grouped.values()) {
            toWrongSession(records).ifPresent(sessions::add);
        }
        return sessions;
    }

    @Transactional(readOnly = true)
    public PracticalWrongSession findWrongSessionDetail(String sessionKey) {
        List<PracticalSolveRecord> records = repo.findBySessionKeyOrderBySolvedAtDesc(sessionKey);
        return toWrongSession(records)
                .orElseThrow(() -> new IllegalArgumentException("시험 기록을 찾을 수 없습니다."));
    }

    private Optional<PracticalWrongSession> toWrongSession(List<PracticalSolveRecord> records) {
        if (records.isEmpty()) {
            return Optional.empty();
        }
        List<PracticalSolveRecord> wrongRecords = records.stream()
                .filter(r -> !r.isCorrect()).toList();
        if (wrongRecords.isEmpty()) {
            return Optional.empty();
        }
        List<PracticalSolveRecord> correctRecords = records.stream()
                .filter(PracticalSolveRecord::isCorrect).toList();
        PracticalSolveRecord first = records.get(0);
        int totalCount = records.size();
        int correctCount = correctRecords.size();
        return Optional.of(new PracticalWrongSession(
                first.getSessionKey(), first.getSessionTitle(), first.getSolvedAt(), wrongRecords,
                correctRecords, totalCount, correctCount));
    }

    @Transactional
    public void deleteRecord(Long id) {
        repo.deleteById(id);
    }

    /** 오답 기록을 수동으로 정답 처리한다. */
    @Transactional
    public void markCorrect(Long id) {
        repo.findById(id).ifPresent(r -> r.setCorrect(true));
    }

    @Transactional(readOnly = true)
    public boolean hasWrongRecords(String sessionKey) {
        return repo.existsBySessionKeyAndCorrectFalse(sessionKey);
    }

    @Transactional
    public void deleteSession(String sessionKey) {
        repo.deleteBySessionKey(sessionKey);
    }

    @Transactional
    public void saveMemo(Long id, String memo) {
        repo.findById(id).ifPresent(r -> r.setMemo(memo));
    }

    /** 저장된 오답 기록을 현재 채점 로직으로 재채점하여 이제 정답인 항목을 correct=true 로 갱신한다. */
    @Transactional
    public int reEvaluateWrongRecords() {
        List<PracticalSolveRecord> wrongRecords = repo.findByCorrectFalseOrderBySolvedAtDesc();
        int fixed = 0;
        for (PracticalSolveRecord r : wrongRecords) {
            if (r.getQuestion().isCorrectAnswer(r.getSubmitted())) {
                r.setCorrect(true);
                fixed++;
            }
        }
        return fixed;
    }
}
