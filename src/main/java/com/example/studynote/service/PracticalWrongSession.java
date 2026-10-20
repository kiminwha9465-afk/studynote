package com.example.studynote.service;

import com.example.studynote.domain.PracticalSolveRecord;
import java.time.LocalDateTime;
import java.util.List;

public record PracticalWrongSession(
        String sessionKey,
        String title,
        LocalDateTime solvedAt,
        List<PracticalSolveRecord> wrongRecords
) {}
