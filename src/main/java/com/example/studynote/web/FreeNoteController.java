package com.example.studynote.web;

import com.example.studynote.domain.FreeNote;
import com.example.studynote.service.FreeNoteService;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/free-notes")
public class FreeNoteController {

    private final FreeNoteService freeNoteService;
    private final Path uploadDir = Paths.get("./data/uploads");

    public FreeNoteController(FreeNoteService freeNoteService) {
        this.freeNoteService = freeNoteService;
    }

    @PostConstruct
    void init() throws IOException {
        Files.createDirectories(uploadDir);
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("notes", freeNoteService.findAll());
        return "free-notes";
    }

    @GetMapping("/new")
    public String newNote() {
        return "free-note-editor";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("note", freeNoteService.findById(id));
        return "free-note-view";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("note", freeNoteService.findById(id));
        return "free-note-editor";
    }

    @PostMapping
    public String create(@RequestParam String title, @RequestParam String content) {
        FreeNote note = freeNoteService.create(title, content);
        return "redirect:/free-notes/" + note.getId();
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String content) {
        freeNoteService.update(id, title, content);
        return "redirect:/free-notes/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        freeNoteService.delete(id);
        return "redirect:/free-notes";
    }

    @PostMapping("/upload-image")
    @ResponseBody
    public Map<String, String> uploadImage(@RequestParam MultipartFile file) throws IOException {
        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf("."))
                : "";
        String filename = UUID.randomUUID() + ext;
        Files.copy(file.getInputStream(), uploadDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return Map.of("url", "/uploads/" + filename);
    }
}
