package com.example.studynote.repository;

import com.example.studynote.domain.PracticalQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PracticalQuestionRepository extends JpaRepository<PracticalQuestion, Long> {

    List<PracticalQuestion> findByExamYearAndExamRoundOrderByQuestionNoAsc(Integer examYear, Integer examRound);

    List<PracticalQuestion> findByExamYearAndExamRoundAndContent(Integer examYear, Integer examRound, String content);

    @Query("SELECT DISTINCT p.examYear, p.examRound FROM PracticalQuestion p ORDER BY p.examYear DESC, p.examRound DESC")
    List<Object[]> findDistinctExamSittings();
}
