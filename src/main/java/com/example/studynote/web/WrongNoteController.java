package com.example.studynote.web;

import com.example.studynote.service.SolveRecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wrong-notes")
public class WrongNoteController {

    private final SolveRecordService solveRecordService;

    public WrongNoteController(SolveRecordService solveRecordService) {
        this.solveRecordService = solveRecordService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("wrongRecords", solveRecordService.findWrongNotes());
        return "wrong-notes";
    }

    @PostMapping("/{recordId}/memo")
    public String saveMemo(@PathVariable Long recordId, @RequestParam String memo) {
        solveRecordService.saveMemo(recordId, memo);
        return "redirect:/wrong-notes";
    }
}
