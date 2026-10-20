package com.example.studynote.repository;

import com.example.studynote.domain.PracticalSolveRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PracticalSolveRecordRepository extends JpaRepository<PracticalSolveRecord, Long> {

    List<PracticalSolveRecord> findByCorrectFalseOrderBySolvedAtDesc();

    @Query("SELECT DISTINCT r.sessionKey, r.sessionTitle, r.solvedAt FROM PracticalSolveRecord r ORDER BY r.solvedAt DESC")
    List<Object[]> findDistinctSessions();

    List<PracticalSolveRecord> findBySessionKeyOrderByIdAsc(String sessionKey);
}
