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

@Entity
@Table(name = "practical_solve_record")
@Getter
@Setter
@NoArgsConstructor
public class PracticalSolveRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id")
    private PracticalQuestion question;

    private String submitted;

    private boolean correct;

    private String sessionTitle;

    /** 같은 세션에서 제출된 레코드를 묶는 식별자 (UUID) */
    private String sessionKey;

    @Column(columnDefinition = "TEXT")
    private String memo;

    private LocalDateTime solvedAt;

    public PracticalSolveRecord(PracticalQuestion question, String submitted, boolean correct,
                                 String sessionTitle, String sessionKey) {
        this.question = question;
        this.submitted = submitted;
        this.correct = correct;
        this.sessionTitle = sessionTitle;
        this.sessionKey = sessionKey;
    }

    @PrePersist
    void onCreate() {
        this.solvedAt = LocalDateTime.now();
    }
}
