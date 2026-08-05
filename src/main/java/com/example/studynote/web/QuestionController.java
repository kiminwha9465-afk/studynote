package com.example.studynote.web;

import com.example.studynote.domain.Question;
import com.example.studynote.domain.SolveMode;
import com.example.studynote.domain.Subject;
import com.example.studynote.service.QuestionService;
import com.example.studynote.service.SolveRecordService;
import com.example.studynote.service.SolveResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final SolveRecordService solveRecordService;

    public QuestionController(QuestionService questionService, SolveRecordService solveRecordService) {
        this.questionService = questionService;
        this.solveRecordService = solveRecordService;
    }

    @ModelAttribute("subjects")
    public Subject[] subjects() {
        return Subject.values();
    }

    @GetMapping
    public String list(@RequestParam(required = false) Subject subject, Model model) {
        model.addAttribute("questions", questionService.findQuestions(subject));
        model.addAttribute("selectedSubject", subject);
        return "question-list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question-detail";
    }

    @PostMapping("/{id}/answer")
    public String answer(@PathVariable Long id, @RequestParam int selectedAnswer, Model model) {
        SolveResult result = solveRecordService.submitAnswer(id, selectedAnswer, SolveMode.PRACTICE);
        model.addAttribute("question", result.question());
        model.addAttribute("result", result);
        return "question-detail";
    }
}
