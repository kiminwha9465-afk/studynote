package com.example.studynote.repository;

import com.example.studynote.domain.PracticalSolveRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PracticalSolveRecordRepository extends JpaRepository<PracticalSolveRecord, Long> {

    List<PracticalSolveRecord> findByCorrectFalseOrderBySolvedAtDesc();

    @Query("SELECT r.sessionKey, r.sessionTitle, MIN(r.solvedAt) FROM PracticalSolveRecord r GROUP BY r.sessionKey, r.sessionTitle ORDER BY MIN(r.solvedAt) DESC")
    List<Object[]> findDistinctSessions();

    List<PracticalSolveRecord> findBySessionKeyOrderByIdAsc(String sessionKey);
}
