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

    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;

    /** 정답 번호 (1~4) */
    private Integer answerNo;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private LocalDateTime createdAt;

    @Builder
    public Question(Subject subject, Integer examYear, Integer examRound, String content,
                     String choice1, String choice2, String choice3, String choice4,
                     Integer answerNo, String explanation) {
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
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
