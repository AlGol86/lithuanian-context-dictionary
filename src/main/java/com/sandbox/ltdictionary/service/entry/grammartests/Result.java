package com.sandbox.ltdictionary.service.entry.grammartests;

import java.time.LocalDateTime;

public record Result(String name, String page, int score, LocalDateTime time) {

    public String getKey() {
        return String.join(":", name(), page());
    }

}
