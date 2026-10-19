package com.example.studynote.web;

import com.example.studynote.domain.PracticalQuestion;
import java.util.List;

public record PracticalResultView(String title, List<Item> items) {

    public int score() {
        return (int) items.stream().filter(Item::correct).count();
    }

    public record Item(PracticalQuestion question, String submitted, boolean correct) {}
}
