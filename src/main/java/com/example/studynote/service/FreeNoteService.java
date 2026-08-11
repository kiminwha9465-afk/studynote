package com.example.studynote.service;

import com.example.studynote.domain.FreeNote;
import com.example.studynote.repository.FreeNoteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FreeNoteService {

    private final FreeNoteRepository freeNoteRepository;

    public List<FreeNote> findAll() {
        return freeNoteRepository.findAllByOrderByUpdatedAtDesc();
    }

    public FreeNote findById(Long id) {
        return freeNoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노트: " + id));
    }

    @Transactional
    public FreeNote create(String title, String content) {
        FreeNote note = new FreeNote();
        note.setTitle(title == null || title.isBlank() ? "제목 없음" : title);
        note.setContent(content);
        return freeNoteRepository.save(note);
    }

    @Transactional
    public void update(Long id, String title, String content) {
        FreeNote note = findById(id);
        note.setTitle(title == null || title.isBlank() ? "제목 없음" : title);
        note.setContent(content);
    }

    @Transactional
    public void delete(Long id) {
        freeNoteRepository.deleteById(id);
    }
}
