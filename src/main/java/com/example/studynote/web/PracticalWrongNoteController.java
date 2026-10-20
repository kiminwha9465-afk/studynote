package com.example.studynote.web;

import com.example.studynote.service.PracticalSolveRecordService;
import com.example.studynote.service.PracticalWrongSession;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/practical/wrong-notes")
public class PracticalWrongNoteController {

    private final PracticalSolveRecordService solveRecordService;

    public PracticalWrongNoteController(PracticalSolveRecordService solveRecordService) {
        this.solveRecordService = solveRecordService;
    }

    /** 디버그용: Thymeleaf 없이 컨트롤러/서비스 동작 확인 */
    @GetMapping("/debug")
    @ResponseBody
    public ResponseEntity<String> debug() {
        StringBuilder sb = new StringBuilder("<html><body style='font-family:monospace;padding:20px'>");
        sb.append("<h2>DEBUG: PracticalWrongNoteController</h2>");
        try {
            List<PracticalWrongSession> sessions = solveRecordService.findWrongSessions();
            sb.append("<p style='color:green'>✅ 서비스 호출 성공 - sessions=").append(sessions.size()).append("</p>");
        } catch (Throwable t) {
            sb.append("<p style='color:red'>❌ 서비스 오류: ").append(t.getClass().getName())
              .append(": ").append(t.getMessage()).append("</p>");
            Throwable cause = t.getCause();
            while (cause != null) {
                sb.append("<p style='color:red; margin-left:20px'>원인: ")
                  .append(cause.getClass().getName()).append(": ").append(cause.getMessage()).append("</p>");
                cause = cause.getCause();
            }
        }
        sb.append("</body></html>");
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(sb.toString());
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

    @PostMapping("/{id}/delete")
    public String deleteRecord(@PathVariable Long id) {
        solveRecordService.deleteRecord(id);
        return "redirect:/practical/wrong-notes";
    }

    @PostMapping("/{id}/memo")
    public String saveMemo(@PathVariable Long id, @RequestParam String memo) {
        solveRecordService.saveMemo(id, memo);
        return "redirect:/practical/wrong-notes";
    }

    @PostMapping("/re-evaluate")
    public String reEvaluate(org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        int fixed = solveRecordService.reEvaluateWrongRecords();
        ra.addFlashAttribute("reEvaluateResult", fixed + "개의 오답이 정답으로 정정되었습니다.");
        return "redirect:/practical/wrong-notes";
    }
}
