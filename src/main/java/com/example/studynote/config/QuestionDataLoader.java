package com.example.studynote.config;

import com.example.studynote.domain.Question;
import com.example.studynote.domain.Subject;
import com.example.studynote.repository.ExamSessionRepository;
import com.example.studynote.repository.QuestionRepository;
import com.example.studynote.repository.SolveRecordRepository;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 앱 시작 시 questions.json의 문제 수와 DB 문제 수를 비교한다.
 * 수가 다르면 기존 데이터를 전체 초기화하고 JSON을 재적재한다.
 */
@Component
public class QuestionDataLoader implements CommandLineRunner {

    private final QuestionRepository questionRepository;
    private final SolveRecordRepository solveRecordRepository;
    private final ExamSessionRepository examSessionRepository;
    private final ObjectMapper objectMapper;

    public QuestionDataLoader(QuestionRepository questionRepository,
                               SolveRecordRepository solveRecordRepository,
                               ExamSessionRepository examSessionRepository,
                               ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.solveRecordRepository = solveRecordRepository;
        this.examSessionRepository = examSessionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(String... args) throws IOException {
        try (InputStream in = new ClassPathResource("data/questions.json").getInputStream()) {
            List<QuestionRecord> records = objectMapper.readValue(in, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, QuestionRecord.class));

            if (questionRepository.count() == records.size()) {
                return;
            }

            solveRecordRepository.deleteAllInBatch();
            examSessionRepository.deleteAllInBatch();
            questionRepository.deleteAllInBatch();

            List<Question> questions = records.stream()
                    .map(QuestionDataLoader::toEntity)
                    .toList();
            questionRepository.saveAll(questions);
        }
    }

    private static Question toEntity(QuestionRecord r) {
        return Question.builder()
                .subject(r.subject() == null ? null : Subject.valueOf(r.subject()))
                .examYear(r.examYear())
                .examRound(r.examRound())
                .content(r.content())
                .choice1(r.choice1())
                .choice2(r.choice2())
                .choice3(r.choice3())
                .choice4(r.choice4())
                .answerNo(r.answerNo())
                .explanation(r.explanation())
                .multiAnswer(r.multiAnswer())
                .theme(r.theme())
                .build();
    }

    private record QuestionRecord(
            String subject,
            Integer examYear,
            Integer examRound,
            String content,
            String choice1,
            String choice2,
            String choice3,
            String choice4,
            Integer answerNo,
            String multiAnswer,
            String explanation,
            String theme
    ) {
    }
}
