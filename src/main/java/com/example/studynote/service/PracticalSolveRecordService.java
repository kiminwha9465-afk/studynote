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
        List<Object[]> rows = repo.findDistinctSessions();
        // sessionKey 순서로 각 세션의 오답 목록 구성
        Map<String, PracticalWrongSession> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String key = (String) row[0];
            String title = (String) row[1];
            java.time.LocalDateTime at = (java.time.LocalDateTime) row[2];
            List<PracticalSolveRecord> wrong = repo.findBySessionKeyOrderByIdAsc(key)
                    .stream().filter(r -> !r.isCorrect()).toList();
            if (!wrong.isEmpty()) {
                map.put(key, new PracticalWrongSession(key, title, at, wrong));
            }
        }
        return new ArrayList<>(map.values());
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
