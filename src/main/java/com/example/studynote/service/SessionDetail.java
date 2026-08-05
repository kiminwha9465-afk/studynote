package com.example.studynote.service;

import com.example.studynote.domain.ExamSession;
import com.example.studynote.domain.SolveRecord;
import java.util.List;

public record SessionDetail(
        ExamSession session,
        List<WrongAnswerDetail> wrongDetails,
        List<SolveRecord> correctList) {

    public int totalCount() {
        return wrongDetails.size() + correctList.size();
    }

    public int scorePercent() {
        int total = totalCount();
        if (total == 0) return 0;
        return Math.round(100f * correctList.size() / total);
    }
}
