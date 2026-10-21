package com.example.studynote.repository;

import com.example.studynote.domain.PracticalSolveRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PracticalSolveRecordRepository extends JpaRepository<PracticalSolveRecord, Long> {

    List<PracticalSolveRecord> findByCorrectFalseOrderBySolvedAtDesc();

    List<PracticalSolveRecord> findAllByOrderBySolvedAtDesc();

    List<PracticalSolveRecord> findBySessionKeyOrderBySolvedAtDesc(String sessionKey);

    void deleteBySessionKey(String sessionKey);
}
