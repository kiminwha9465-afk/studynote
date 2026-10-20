package com.example.studynote.web;

import com.example.studynote.domain.PracticalQuestion;
import com.example.studynote.service.PracticalService;
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

@Controller
@RequestMapping("/practical")
public class PracticalController {

    private static final String SESSION_IDS    = "practicalQuestionIds";
    private static final String SESSION_TITLE  = "practicalTitle";
    private static final String SESSION_RESULT = "practicalResult";

    private final PracticalService practicalService;

    public PracticalController(PracticalService practicalService) {
        this.practicalService = practicalService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("sittings", practicalService.listSittings());
        return "practical-home";
    }

    @PostMapping("/mock/start")
    public String startMock(@RequestParam(defaultValue = "20") int count, HttpSession session) {
        List<PracticalQuestion> questions = practicalService.buildMockExam(count);
        session.setAttribute(SESSION_IDS, questions.stream().map(PracticalQuestion::getId).toList());
        session.setAttribute(SESSION_TITLE, "실기 모의고사 (%d문항)".formatted(questions.size()));
        return "redirect:/practical/take";
    }

    @PostMapping("/year/start")
    public String startYear(@RequestParam int examYear, @RequestParam int examRound, HttpSession session) {
        List<PracticalQuestion> questions = practicalService.buildExam(examYear, examRound);
        session.setAttribute(SESSION_IDS, questions.stream().map(PracticalQuestion::getId).toList());
        session.setAttribute(SESSION_TITLE,
                "%d년 %d회 실기 기출문제 (%d문항)".formatted(examYear, examRound, questions.size()));
        return "redirect:/practical/take";
    }

    @GetMapping("/take")
    public String take(HttpSession session, Model model) {
        List<Long> ids = questionIds(session);
        if (ids == null || ids.isEmpty()) return "redirect:/practical";
        model.addAttribute("title", session.getAttribute(SESSION_TITLE));
        model.addAttribute("questions", loadByIds(ids));
        return "practical-take";
    }

    @PostMapping("/submit")
    public String submit(HttpServletRequest request, HttpSession session) {
        List<Long> ids = questionIds(session);
        if (ids == null || ids.isEmpty()) return "redirect:/practical";

        String title = (String) session.getAttribute(SESSION_TITLE);
        List<PracticalQuestion> questions = loadByIds(ids);

        List<PracticalResultView.Item> items = new ArrayList<>();
        for (PracticalQuestion q : questions) {
            String raw = request.getParameter("answer_" + q.getId());
            String submitted = (raw == null) ? "" : raw.trim();
            items.add(new PracticalResultView.Item(q, submitted, q.isCorrectAnswer(submitted)));
        }

        session.setAttribute(SESSION_RESULT, new PracticalResultView(title, items));
        session.removeAttribute(SESSION_IDS);
        session.removeAttribute(SESSION_TITLE);
        return "redirect:/practical/result";
    }

    @GetMapping("/result")
    public String result(HttpSession session, Model model) {
        PracticalResultView result = (PracticalResultView) session.getAttribute(SESSION_RESULT);
        if (result == null) return "redirect:/practical";
        model.addAttribute("result", result);
        return "practical-result";
    }

    @SuppressWarnings("unchecked")
    private List<Long> questionIds(HttpSession session) {
        return (List<Long>) session.getAttribute(SESSION_IDS);
    }

    private List<PracticalQuestion> loadByIds(List<Long> ids) {
        return ids.stream().map(practicalService::findById).toList();
    }
}
