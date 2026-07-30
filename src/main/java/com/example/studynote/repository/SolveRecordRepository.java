package com.example.studynote.repository;

import com.example.studynote.domain.SolveRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolveRecordRepository extends JpaRepository<SolveRecord, Long> {

    List<SolveRecord> findAllByOrderBySolvedAtDesc();

    List<SolveRecord> findByQuestionIdOrderBySolvedAtDesc(Long questionId);
}
