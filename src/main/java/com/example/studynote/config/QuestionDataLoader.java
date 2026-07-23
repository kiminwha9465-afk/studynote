package com.example.studynote.config;

import com.example.studynote.domain.Question;
import com.example.studynote.domain.Subject;
import com.example.studynote.repository.QuestionRepository;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * 앱 최초 실행 시 정보처리기사 실제 기출문제/문제집 데이터(data/questions.json)를 DB에 적재한다.
 * 이미 데이터가 있으면 아무 것도 하지 않는다.
 */
@Component
public class QuestionDataLoader implements CommandLineRunner {

    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper;

    public QuestionDataLoader(QuestionRepository questionRepository, ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws IOException {
        if (questionRepository.count() > 0) {
            return;
        }

        try (InputStream in = new ClassPathResource("data/questions.json").getInputStream()) {
            List<QuestionRecord> records = objectMapper.readValue(in, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, QuestionRecord.class));

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
