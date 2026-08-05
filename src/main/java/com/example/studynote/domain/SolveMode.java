package com.example.studynote.domain;

public enum SolveMode {
    PRACTICE,  // 임의풀이 - /questions/{id} 개별 풀이
    MOCK,      // 모의고사 - 과목별 랜덤 시험
    YEAR       // 지정년도 - 특정 회차 기출 시험
}
