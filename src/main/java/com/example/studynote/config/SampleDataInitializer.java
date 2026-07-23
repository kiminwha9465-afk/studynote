package com.example.studynote.config;

import com.example.studynote.domain.Question;
import com.example.studynote.domain.Subject;
import com.example.studynote.repository.QuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** 최초 실행 시 화면 확인용 샘플 문제 몇 개를 넣어준다. 실제 기출문제로 교체해서 사용할 것. */
@Component
public class SampleDataInitializer implements CommandLineRunner {

    private final QuestionRepository questionRepository;

    public SampleDataInitializer(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(String... args) {
        if (questionRepository.count() > 0) {
            return;
        }

        questionRepository.save(Question.builder()
                .subject(Subject.SOFTWARE_DESIGN)
                .examYear(2023)
                .examRound(1)
                .content("(샘플) 소프트웨어 설계에서 모듈 간 결합도(Coupling)가 가장 낮은 것은?")
                .choice1("내용 결합도")
                .choice2("공통 결합도")
                .choice3("자료 결합도")
                .choice4("제어 결합도")
                .answerNo(3)
                .explanation("결합도는 자료 결합도가 가장 낮고, 내용 결합도가 가장 높다.")
                .build());

        questionRepository.save(Question.builder()
                .subject(Subject.SOFTWARE_DEVELOPMENT)
                .examYear(2023)
                .examRound(2)
                .content("(샘플) 다음 중 정렬 알고리즘의 평균 시간복잡도가 O(n log n)이 아닌 것은?")
                .choice1("퀵 정렬")
                .choice2("병합 정렬")
                .choice3("힙 정렬")
                .choice4("버블 정렬")
                .answerNo(4)
                .explanation("버블 정렬의 평균/최악 시간복잡도는 O(n^2)이다.")
                .build());

        questionRepository.save(Question.builder()
                .subject(Subject.DATABASE_CONSTRUCTION)
                .examYear(2022)
                .examRound(3)
                .content("(샘플) 제3정규형(3NF)을 만족하기 위해 제거해야 하는 종속성은?")
                .choice1("부분 함수 종속")
                .choice2("이행 함수 종속")
                .choice3("다치 종속")
                .choice4("조인 종속")
                .answerNo(2)
                .explanation("3NF는 2NF를 만족하면서 이행적 함수 종속을 제거한 상태이다.")
                .build());

        questionRepository.save(Question.builder()
                .subject(Subject.PROGRAMMING_LANGUAGE)
                .examYear(2023)
                .examRound(1)
                .content("(샘플) C언어에서 정수를 0으로 나누었을 때 발생하는 문제는?")
                .choice1("컴파일 오류")
                .choice2("런타임 오류(예외 발생)")
                .choice3("항상 0을 반환")
                .choice4("항상 1을 반환")
                .answerNo(2)
                .explanation("정수 나눗셈에서 0으로 나누면 런타임에 예외/오류가 발생한다.")
                .build());

        questionRepository.save(Question.builder()
                .subject(Subject.INFO_SYSTEM_MANAGEMENT)
                .examYear(2022)
                .examRound(2)
                .content("(샘플) 다음 중 정보보안의 3대 요소(CIA)에 해당하지 않는 것은?")
                .choice1("기밀성(Confidentiality)")
                .choice2("무결성(Integrity)")
                .choice3("가용성(Availability)")
                .choice4("편의성(Convenience)")
                .answerNo(4)
                .explanation("정보보안의 3대 요소는 기밀성, 무결성, 가용성이다.")
                .build());
    }
}
