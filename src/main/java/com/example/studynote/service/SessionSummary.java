package com.example.studynote.service;

import com.example.studynote.domain.ExamSession;

public record SessionSummary(ExamSession session, int totalCount, int correctCount) {
    public int wrongCount() {
        return totalCount - correctCount;
    }

    public int scorePercent() {
        if (totalCount == 0) return 0;
        return Math.round(100f * correctCount / totalCount);
    }
}
