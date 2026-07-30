package com.example.studynote.web;

import com.example.studynote.service.SolveResult;
import java.util.List;

/** 모의고사/지정년도 시험 제출 결과 화면에 쓰이는 요약 뷰. */
public record ExamResultView(String title, List<SolveResult> results) {

    public int totalCount() {
        return results.size();
    }

    public long correctCount() {
        return results.stream().filter(SolveResult::correct).count();
    }

    public int scorePercent() {
        if (results.isEmpty()) {
            return 0;
        }
        return Math.round(100f * correctCount() / totalCount());
    }
}
