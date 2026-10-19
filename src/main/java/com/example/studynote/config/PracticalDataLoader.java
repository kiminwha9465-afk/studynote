package com.example.studynote.config;

import com.example.studynote.domain.PracticalQuestion;
import com.example.studynote.repository.PracticalQuestionRepository;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 앱 시작 시 practical_questions.json을 DB에 동기화한다.
 * (examYear, examRound, questionNo) 조합을 자연키로 삼아 기존 레코드를 갱신하고 새 레코드만 insert.
 */
@Component
@Order(2)
public class PracticalDataLoader implements CommandLineRunner {

    private final PracticalQuestionRepository repository;
    private final ObjectMapper objectMapper;

    public PracticalDataLoader(PracticalQuestionRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(String... args) throws IOException {
        ClassPathResource resource = new ClassPathResource("data/practical_questions.json");
        if (!resource.exists()) return;

        try (InputStream in = resource.getInputStream()) {
            List<PracticalRecord> records = objectMapper.readValue(in,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PracticalRecord.class));

            for (PracticalRecord r : records) {
                List<PracticalQuestion> matches = repository.findByExamYearAndExamRoundAndContent(
                        r.examYear(), r.examRound(), r.content() == null ? "" : r.content().trim());

                if (!matches.isEmpty()) {
                    for (PracticalQuestion existing : matches) {
                        existing.setQuestionNo(r.questionNo());
                        existing.setAnswer(r.answer());
                        existing.setExplanation(r.explanation());
                        repository.save(existing);
                    }
                } else {
                    repository.save(PracticalQuestion.builder()
                            .examYear(r.examYear())
                            .examRound(r.examRound())
                            .questionNo(r.questionNo())
                            .content(r.content())
                            .answer(r.answer())
                            .explanation(r.explanation())
                            .build());
                }
            }
        }
    }

    private record PracticalRecord(
            Integer examYear,
            Integer examRound,
            Integer questionNo,
            String content,
            String answer,
            String explanation
    ) {}
}
