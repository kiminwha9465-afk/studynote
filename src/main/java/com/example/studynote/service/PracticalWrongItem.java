package com.example.studynote.service;

import com.example.studynote.domain.PracticalQuestion;
import java.util.List;

/** 인쇄용 오답 모음의 한 문항. 같은 문제를 여러 번 틀린 경우 하나로 묶는다. */
public record PracticalWrongItem(
        PracticalQuestion question,
        int wrongCount,
        List<String> submittedAnswers,
        String memo
) {
}
