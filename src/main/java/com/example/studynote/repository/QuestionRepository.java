package com.example.studynote.repository;

import com.example.studynote.domain.Question;
import com.example.studynote.domain.Subject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findBySubjectOrderByIdAsc(Subject subject);

    List<Question> findAllByOrderByIdAsc();

    List<Question> findByExamYearAndExamRoundOrderByIdAsc(Integer examYear, Integer examRound);

    List<Question> findByExamYearAndExamRoundAndContent(Integer examYear, Integer examRound, String content);

    @Query("select distinct q.examYear, q.examRound from Question q "
            + "where q.examYear is not null and q.examRound is not null "
            + "order by q.examYear desc, q.examRound desc")
    List<Object[]> findDistinctExamSittings();
}
