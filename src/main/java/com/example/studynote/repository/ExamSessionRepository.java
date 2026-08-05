package com.example.studynote.repository;

import com.example.studynote.domain.ExamSession;
import com.example.studynote.domain.SolveMode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
    List<ExamSession> findByModeOrderByCreatedAtDesc(SolveMode mode);
}
