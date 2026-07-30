package com.example.studynote.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 문제 풀이 시도 1건. 오답노트는 correct=false 인 최신 기록을 모아서 보여준다. */
@Entity
@Table(name = "solve_record")
@Getter
@Setter
@NoArgsConstructor
public class SolveRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    private Integer selectedAnswer;

    private boolean correct;

    @Column(columnDefinition = "TEXT")
    private String memo;

    private LocalDateTime solvedAt;

    public SolveRecord(Question question, Integer selectedAnswer, boolean correct) {
        this.question = question;
        this.selectedAnswer = selectedAnswer;
        this.correct = correct;
    }

    @PrePersist
    void onCreate() {
        this.solvedAt = LocalDateTime.now();
    }
}
