package com.example.studynote.service;

import com.example.studynote.domain.SolveRecord;

/** 시험에서 틀린 문제 + 이후 맞혔는지 여부 */
public record WrongAnswerDetail(SolveRecord record, boolean laterCorrected) {}
