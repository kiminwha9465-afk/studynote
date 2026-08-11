package com.example.studynote.config;

import com.example.studynote.domain.Question;
import com.example.studynote.domain.Subject;
import com.example.studynote.repository.QuestionRepository;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 앱 시작 시 questions.json을 DB에 동기화한다.
 * content를 자연키로 삼아 기존 문제는 ID를 유지한 채 필드만 갱신하고,
 * 새 문제만 insert한다. 풀이기록(FK)을 보존하기 위해 삭제는 하지 않는다.
 */
@Component
public class QuestionDataLoader implements CommandLineRunner {

    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper;

    public QuestionDataLoader(QuestionRepository questionRepository,
                               ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(String... args) throws IOException {
        try (InputStream in = new ClassPathResource("data/questions.json").getInputStream()) {
            List<QuestionRecord> records = objectMapper.readValue(in, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, QuestionRecord.class));

            Map<String, Question> byContent = questionRepository.findAll().stream()
                    .collect(Collectors.toMap(
                            q -> q.getContent().trim(),
                            q -> q,
                            (a, b) -> a));

            for (QuestionRecord r : records) {
                String key = r.content() == null ? "" : r.content().trim();
                Question existing = byContent.get(key);
                if (existing != null) {
                    existing.setSubject(r.subject() == null ? null : Subject.valueOf(r.subject()));
                    existing.setExamYear(r.examYear());
                    existing.setExamRound(r.examRound());
                    existing.setChoice1(r.choice1());
                    existing.setChoice2(r.choice2());
                    existing.setChoice3(r.choice3());
                    existing.setChoice4(r.choice4());
                    existing.setAnswerNo(r.answerNo());
                    existing.setExplanation(r.explanation());
                    existing.setMultiAnswer(r.multiAnswer());
                    existing.setTheme(r.theme());
                } else {
                    questionRepository.save(toEntity(r));
                }
            }
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
