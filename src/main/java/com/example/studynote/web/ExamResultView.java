package com.example.studynote.web;

import com.example.studynote.domain.Subject;
import com.example.studynote.service.SolveResult;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 모의고사/지정년도 시험 제출 결과 화면에 쓰이는 요약 뷰. */
public record ExamResultView(String title, List<SolveResult> results) {

    public record SubjectStat(Subject subject, int total, int correct) {
        public int scorePercent() {
            return total == 0 ? 0 : Math.round(100f * correct / total);
        }
    }

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

    public List<SubjectStat> subjectStats() {
        Map<Subject, List<SolveResult>> bySubject = results.stream()
                .collect(Collectors.groupingBy(r -> r.question().getSubject()));
        return Arrays.stream(Subject.values())
                .filter(bySubject::containsKey)
                .map(s -> {
                    List<SolveResult> list = bySubject.get(s);
                    int total = list.size();
                    int correct = (int) list.stream().filter(SolveResult::correct).count();
                    return new SubjectStat(s, total, correct);
                })
                .toList();
    }
}
