package com.example.studynote.repository;

import com.example.studynote.domain.FreeNote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreeNoteRepository extends JpaRepository<FreeNote, Long> {
    List<FreeNote> findAllByOrderByUpdatedAtDesc();
}
