package com.example.studynote.service;

import com.example.studynote.domain.Question;

/** 답안 제출 결과 */
public record SolveResult(Question question, int selectedAnswer, boolean correct) {
}
