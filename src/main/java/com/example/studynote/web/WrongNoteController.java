package com.example.studynote.web;

import com.example.studynote.domain.SolveMode;
import com.example.studynote.service.SessionDetail;
import com.example.studynote.service.SolveRecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/wrong-notes")
public class WrongNoteController {

    private final SolveRecordService solveRecordService;

    public WrongNoteController(SolveRecordService solveRecordService) {
        this.solveRecordService = solveRecordService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "mock") String mode, Model model) {
        model.addAttribute("mode", mode);
        if ("practice".equals(mode)) {
            model.addAttribute("wrongRecords", solveRecordService.findWrongNotesPractice());
        } else {
            SolveMode solveMode = "year".equals(mode) ? SolveMode.YEAR : SolveMode.MOCK;
            model.addAttribute("sessions", solveRecordService.findSessionSummaries(solveMode));
        }
        return "wrong-notes";
    }

    @GetMapping("/session/{sessionId}")
    public String sessionDetail(@PathVariable Long sessionId, Model model) {
        SessionDetail detail = solveRecordService.getSessionDetail(sessionId);
        model.addAttribute("detail", detail);
        model.addAttribute("examSession", detail.session());
        model.addAttribute("wrongDetails", detail.wrongDetails());
        model.addAttribute("correctList", detail.correctList());
        return "wrong-notes-session";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleNotFound(IllegalArgumentException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/wrong-notes";
    }

    @PostMapping("/session/{sessionId}/delete")
    public String deleteSession(@PathVariable Long sessionId) {
        solveRecordService.deleteSession(sessionId);
        return "redirect:/wrong-notes";
    }

    @PostMapping("/{recordId}/memo")
    public String saveMemo(
            @PathVariable Long recordId,
            @RequestParam String memo,
            @RequestParam(defaultValue = "mock") String mode,
            @RequestParam(required = false) Long sessionId) {
        solveRecordService.saveMemo(recordId, memo);
        if (sessionId != null) {
            return "redirect:/wrong-notes/session/" + sessionId;
        }
        return "redirect:/wrong-notes?mode=" + mode;
    }
}
