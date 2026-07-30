package com.example.studynote.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Subject subject;

    private Integer examYear;

    private Integer examRound;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String choice1;
    @Column(columnDefinition = "TEXT")
    private String choice2;
    @Column(columnDefinition = "TEXT")
    private String choice3;
    @Column(columnDefinition = "TEXT")
    private String choice4;

    /** 정답 번호 (1~4) */
    private Integer answerNo;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    /** 복수 정답이 인정되는 문항(전항정답 등)의 허용 정답 목록. 예: "1,3". 없으면 answerNo 하나만 정답. */
    private String multiAnswer;

    /** 연도/회차 기출문제가 아닌 주제별 문제집 출처 태그 (예: "필수계산", "키워드찾기"). 기출문제는 null. */
    private String theme;

    private LocalDateTime createdAt;

    @Builder
    public Question(Subject subject, Integer examYear, Integer examRound, String content,
                     String choice1, String choice2, String choice3, String choice4,
                     Integer answerNo, String explanation, String multiAnswer, String theme) {
        this.subject = subject;
        this.examYear = examYear;
        this.examRound = examRound;
        this.content = content;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.answerNo = answerNo;
        this.explanation = explanation;
        this.multiAnswer = multiAnswer;
        this.theme = theme;
    }

    /** selectedAnswer가 정답으로 인정되는지 여부 (복수 정답 문항 포함). */
    public boolean isCorrectAnswer(int selectedAnswer) {
        if (multiAnswer != null && !multiAnswer.isBlank()) {
            for (String s : multiAnswer.split(",")) {
                if (Integer.parseInt(s.trim()) == selectedAnswer) {
                    return true;
                }
            }
            return false;
        }
        return answerNo != null && answerNo == selectedAnswer;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
