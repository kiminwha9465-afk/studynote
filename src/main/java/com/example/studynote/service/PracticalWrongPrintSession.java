package com.example.studynote.service;

import java.time.LocalDateTime;
import java.util.List;

/** 인쇄용 오답 모음의 한 회차(풀이 기록). */
public record PracticalWrongPrintSession(
        String sessionKey,
        String title,
        LocalDateTime solvedAt,
        int totalCount,
        int correctCount,
        List<PracticalWrongItem> items
) {
}
