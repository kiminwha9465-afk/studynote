package com.example.studynote.web;

import com.example.studynote.domain.Question;
import com.example.studynote.domain.Subject;
import com.example.studynote.service.ExamService;
import com.example.studynote.service.ExamSitting;
import com.example.studynote.service.QuestionService;
import com.example.studynote.service.SolveRecordService;
import com.example.studynote.service.SolveResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** 모의고사 모드(과목별 랜덤) / 지정년도 모드(특정 회차 그대로 풀기). */
@Controller
@RequestMapping("/exam")
public class ExamController {

    private static final String SESSION_QUESTION_IDS = "examQuestionIds";
    private static final String SESSION_TITLE = "examTitle";
    private static final String SESSION_RESULT = "examResult";

    private final ExamService examService;
    private final QuestionService questionService;
    private final SolveRecordService solveRecordService;

    public ExamController(ExamService examService, QuestionService questionService,
                           SolveRecordService solveRecordService) {
        this.examService = examService;
        this.questionService = questionService;
        this.solveRecordService = solveRecordService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("sittings", examService.listExamSittings());
        model.addAttribute("subjects", Subject.values());
        return "exam-home";
    }

    @PostMapping("/mock/start")
    public String startMock(
            @RequestParam(defaultValue = "20") int questionsPerSubject,
            @RequestParam(required = false) String subject,
            HttpSession session) {
        Subject subjectEnum = (subject == null || subject.isBlank()) ? null : Subject.valueOf(subject);
        List<Question> questions = examService.buildMockExam(subjectEnum, questionsPerSubject);
        String title = (subjectEnum != null)
                ? "모의고사 - %s (%d문항)".formatted(subjectEnum.getLabel(), questions.size())
                : "모의고사 (과목별 %d문항, 총 %d문항)".formatted(questionsPerSubject, questions.size());
        startAttempt(session, questions, title);
        return "redirect:/exam/take";
    }

    @PostMapping("/year/start")
    public String startYear(@RequestParam int examYear, @RequestParam int examRound, HttpSession session) {
        List<Question> questions = examService.buildYearExam(examYear, examRound);
        startAttempt(session, questions, "%d년 %d회 기출문제 (%d문항)".formatted(examYear, examRound, questions.size()));
        return "redirect:/exam/take";
    }

    @GetMapping("/take")
    public String take(HttpSession session, Model model) {
        List<Long> ids = questionIdsInSession(session);
        if (ids == null || ids.isEmpty()) {
            return "redirect:/exam";
        }
        List<Question> questions = ids.stream().map(questionService::getQuestion).toList();
        model.addAttribute("title", session.getAttribute(SESSION_TITLE));
        model.addAttribute("questions", questions);
        return "exam-take";
    }

    @PostMapping("/submit")
    public String submit(HttpServletRequest request, HttpSession session) {
        List<Long> ids = questionIdsInSession(session);
        if (ids == null || ids.isEmpty()) {
            return "redirect:/exam";
        }

        List<SolveResult> results = new ArrayList<>();
        for (Long id : ids) {
            String param = request.getParameter("answer_" + id);
            int selected = (param == null || param.isBlank()) ? 0 : Integer.parseInt(param);
            results.add(solveRecordService.submitAnswer(id, selected));
        }

        String title = (String) session.getAttribute(SESSION_TITLE);
        session.setAttribute(SESSION_RESULT, new ExamResultView(title, results));
        session.removeAttribute(SESSION_QUESTION_IDS);
        session.removeAttribute(SESSION_TITLE);
        return "redirect:/exam/result";
    }

    @GetMapping("/result")
    public String result(HttpSession session, Model model) {
        ExamResultView result = (ExamResultView) session.getAttribute(SESSION_RESULT);
        if (result == null) {
            return "redirect:/exam";
        }
        model.addAttribute("result", result);
        return "exam-result";
    }

    private void startAttempt(HttpSession session, List<Question> questions, String title) {
        session.setAttribute(SESSION_QUESTION_IDS, questions.stream().map(Question::getId).toList());
        session.setAttribute(SESSION_TITLE, title);
    }

    @SuppressWarnings("unchecked")
    private List<Long> questionIdsInSession(HttpSession session) {
        return (List<Long>) session.getAttribute(SESSION_QUESTION_IDS);
    }
}
