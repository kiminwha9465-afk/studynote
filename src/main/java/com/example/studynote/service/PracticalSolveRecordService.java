package com.example.studynote.service;

import com.example.studynote.domain.PracticalSolveRecord;
import com.example.studynote.repository.PracticalSolveRecordRepository;
import com.example.studynote.web.PracticalResultView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        for (Map.Entry<String, List<PracticalSolveRecord>> e : grouped.entrySet()) {
            List<PracticalSolveRecord> wrongRecords = e.getValue().stream()
                    .filter(r -> !r.isCorrect()).toList();
            if (!wrongRecords.isEmpty()) {
                PracticalSolveRecord first = e.getValue().get(0);
                int totalCount = e.getValue().size();
                int correctCount = totalCount - wrongRecords.size();
                sessions.add(new PracticalWrongSession(
                        e.getKey(), first.getSessionTitle(), first.getSolvedAt(), wrongRecords,
                        totalCount, correctCount));
            }
        }
        return sessions;
    }

    @Transactional
    public void deleteRecord(Long id) {
        repo.deleteById(id);
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
