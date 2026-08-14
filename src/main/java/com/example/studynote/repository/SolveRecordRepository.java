package com.example.studynote.repository;

import com.example.studynote.domain.ExamSession;
import com.example.studynote.domain.SolveMode;
import com.example.studynote.domain.SolveRecord;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolveRecordRepository extends JpaRepository<SolveRecord, Long> {

    List<SolveRecord> findAllByOrderBySolvedAtDesc();

    List<SolveRecord> findByQuestionIdOrderBySolvedAtDesc(Long questionId);

    List<SolveRecord> findByModeOrderBySolvedAtDesc(SolveMode mode);

    List<SolveRecord> findByModeAndCorrectFalseOrderBySolvedAtDesc(SolveMode mode);

    List<SolveRecord> findByExamSessionOrderBySolvedAtAsc(ExamSession session);

    boolean existsByQuestionIdAndCorrectTrueAndSolvedAtAfter(Long questionId, LocalDateTime after);

    void deleteByExamSession(ExamSession session);
}
