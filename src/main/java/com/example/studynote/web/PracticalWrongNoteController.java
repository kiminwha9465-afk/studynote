package com.example.studynote.web;

import com.example.studynote.service.PracticalSolveRecordService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/practical/wrong-notes")
public class PracticalWrongNoteController {

    private final PracticalSolveRecordService solveRecordService;

    public PracticalWrongNoteController(PracticalSolveRecordService solveRecordService) {
        this.solveRecordService = solveRecordService;
    }

    @GetMapping
    public String list(Model model) {
        try {
            model.addAttribute("sessions", solveRecordService.findWrongSessions());
        } catch (Exception e) {
            log.error("실기 오답노트 로드 실패", e);
            model.addAttribute("sessions", List.of());
            model.addAttribute("loadError", e.getMessage());
        }
        return "practical-wrong-notes";
    }

    @GetMapping("/session/{sessionKey}")
    public String sessionDetail(@PathVariable String sessionKey, Model model) {
        model.addAttribute("wrongSess", solveRecordService.findWrongSessionDetail(sessionKey));
        return "practical-wrong-notes-session";
    }

    @GetMapping("/print")
    public String print(Model model) {
        model.addAttribute("items", solveRecordService.findWrongItemsForPrint());
        model.addAttribute("printedAt", LocalDateTime.now());
        return "practical-wrong-notes-print";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleNotFound(IllegalArgumentException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/practical/wrong-notes";
    }

    @PostMapping("/session/{sessionKey}/delete")
    public String deleteSession(@PathVariable String sessionKey) {
        solveRecordService.deleteSession(sessionKey);
        return "redirect:/practical/wrong-notes";
    }

    @PostMapping("/{id}/delete")
    public String deleteRecord(@PathVariable Long id, @RequestParam(required = false) String sessionKey) {
        solveRecordService.deleteRecord(id);
        if (sessionKey != null) {
            return "redirect:/practical/wrong-notes/session/" + sessionKey;
        }
        return "redirect:/practical/wrong-notes";
    }

    @PostMapping("/{id}/mark-correct")
    public String markCorrect(@PathVariable Long id, @RequestParam(required = false) String sessionKey) {
        solveRecordService.markCorrect(id);
        if (sessionKey != null) {
            return "redirect:/practical/wrong-notes/session/" + sessionKey;
        }
        return "redirect:/practical/wrong-notes";
    }

    @PostMapping("/{id}/memo")
    public String saveMemo(@PathVariable Long id, @RequestParam String memo,
                            @RequestParam(required = false) String sessionKey) {
        solveRecordService.saveMemo(id, memo);
        if (sessionKey != null) {
            return "redirect:/practical/wrong-notes/session/" + sessionKey;
        }
        return "redirect:/practical/wrong-notes";
    }

    @PostMapping("/re-evaluate")
    public String reEvaluate(RedirectAttributes ra) {
        int fixed = solveRecordService.reEvaluateWrongRecords();
        ra.addFlashAttribute("reEvaluateResult", fixed + "개의 오답이 정답으로 정정되었습니다.");
        return "redirect:/practical/wrong-notes";
    }
}
