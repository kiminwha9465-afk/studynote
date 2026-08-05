package com.example.studynote.web;

import com.example.studynote.domain.ExamSession;
import com.example.studynote.domain.Question;
import com.example.studynote.domain.SolveMode;
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
import org.springframework.web.bind.annotation.PathVariable;
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
    private static final String SESSION_MODE = "examMode";
    private static final String SESSION_EXAM_SESSION_ID = "examSessionId";
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
        startAttempt(session, questions, title, SolveMode.MOCK);
        return "redirect:/exam/take";
    }

    @PostMapping("/year/start")
    public String startYear(@RequestParam int examYear, @RequestParam int examRound, HttpSession session) {
        List<Question> questions = examService.buildYearExam(examYear, examRound);
        startAttempt(session, questions, "%d년 %d회 기출문제 (%d문항)".formatted(examYear, examRound, questions.size()), SolveMode.YEAR);
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

        SolveMode mode = (SolveMode) session.getAttribute(SESSION_MODE);
        if (mode == null) mode = SolveMode.MOCK;

        Long examSessionId = (Long) session.getAttribute(SESSION_EXAM_SESSION_ID);
        ExamSession examSession = examSessionId != null ? solveRecordService.getExamSession(examSessionId) : null;

        List<SolveResult> results = new ArrayList<>();
        for (Long id : ids) {
            String param = request.getParameter("answer_" + id);
            int selected = (param == null || param.isBlank()) ? 0 : Integer.parseInt(param);
            results.add(solveRecordService.submitAnswer(id, selected, mode, examSession));
        }

        String title = (String) session.getAttribute(SESSION_TITLE);
        session.setAttribute(SESSION_RESULT, new ExamResultView(title, results));
        session.removeAttribute(SESSION_QUESTION_IDS);
        session.removeAttribute(SESSION_TITLE);
        session.removeAttribute(SESSION_MODE);
        session.removeAttribute(SESSION_EXAM_SESSION_ID);
        return "redirect:/exam/result";
    }

    @GetMapping("/retry/session/{sessionId}")
    public String retrySession(@PathVariable Long sessionId, HttpSession session) {
        List<Long> wrongIds = solveRecordService.findWrongQuestionIdsForSession(sessionId);
        if (wrongIds.isEmpty()) {
            return "redirect:/wrong-notes/session/" + sessionId;
        }
        ExamSession orig = solveRecordService.getExamSession(sessionId);
        List<Question> questions = wrongIds.stream().map(questionService::getQuestion).toList();
        startAttempt(session, questions, "오답 재시험 - " + orig.getTitle(), SolveMode.PRACTICE);
        return "redirect:/exam/take";
    }

    @GetMapping("/retry/practice")
    public String retryPractice(HttpSession session) {
        List<Long> wrongIds = solveRecordService.findWrongQuestionIdsForPractice();
        if (wrongIds.isEmpty()) {
            return "redirect:/wrong-notes?mode=practice";
        }
        List<Question> questions = wrongIds.stream().map(questionService::getQuestion).toList();
        startAttempt(session, questions, "임의풀이 오답 재시험", SolveMode.PRACTICE);
        return "redirect:/exam/take";
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

    private void startAttempt(HttpSession session, List<Question> questions, String title, SolveMode mode) {
        ExamSession examSession = solveRecordService.createSession(title, mode);
        session.setAttribute(SESSION_QUESTION_IDS, questions.stream().map(Question::getId).toList());
        session.setAttribute(SESSION_TITLE, title);
        session.setAttribute(SESSION_MODE, mode);
        session.setAttribute(SESSION_EXAM_SESSION_ID, examSession.getId());
    }

    @SuppressWarnings("unchecked")
    private List<Long> questionIdsInSession(HttpSession session) {
        return (List<Long>) session.getAttribute(SESSION_QUESTION_IDS);
    }
}
