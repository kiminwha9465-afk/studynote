package com.example.studynote.service;

import com.example.studynote.domain.ExamSession;
import com.example.studynote.domain.SolveRecord;
import com.example.studynote.domain.Subject;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record SessionDetail(
        ExamSession session,
        List<WrongAnswerDetail> wrongDetails,
        List<SolveRecord> correctList) {

    public record SubjectStat(Subject subject, int total, int correct) {
        public int scorePercent() {
            return total == 0 ? 0 : Math.round(100f * correct / total);
        }
    }

    public int totalCount() {
        return wrongDetails.size() + correctList.size();
    }

    public int scorePercent() {
        int total = totalCount();
        if (total == 0) return 0;
        return Math.round(100f * correctList.size() / total);
    }

    public boolean passed() {
        if (scorePercent() < 60) return false;
        return subjectStats().stream().noneMatch(s -> s.scorePercent() < 40);
    }

    public List<SubjectStat> subjectStats() {
        Map<Subject, int[]> stats = new LinkedHashMap<>();
        for (Subject s : Subject.values()) {
            stats.put(s, new int[]{0, 0}); // [total, correct]
        }
        wrongDetails.stream().map(WrongAnswerDetail::record).forEach(r -> {
            stats.get(r.getQuestion().getSubject())[0]++;
        });
        correctList.forEach(r -> {
            stats.get(r.getQuestion().getSubject())[0]++;
            stats.get(r.getQuestion().getSubject())[1]++;
        });
        return stats.entrySet().stream()
                .filter(e -> e.getValue()[0] > 0)
                .map(e -> new SubjectStat(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .toList();
    }
}
