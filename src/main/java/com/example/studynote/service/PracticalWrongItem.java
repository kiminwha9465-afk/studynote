package com.example.studynote.service;

import com.example.studynote.domain.PracticalQuestion;

/** 인쇄용 오답 모음의 한 문항. wrongCount 는 전체 회차를 통틀어 현재 오답으로 남은 횟수다. */
public record PracticalWrongItem(
        PracticalQuestion question,
        String submitted,
        String memo,
        int wrongCount
) {
}
