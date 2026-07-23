package com.example.studynote.domain;

public enum Subject {
    SOFTWARE_DESIGN("소프트웨어 설계"),
    SOFTWARE_DEVELOPMENT("소프트웨어 개발"),
    DATABASE_CONSTRUCTION("데이터베이스 구축"),
    PROGRAMMING_LANGUAGE("프로그래밍 언어 활용"),
    INFO_SYSTEM_MANAGEMENT("정보시스템 구축 관리");

    private final String label;

    Subject(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
