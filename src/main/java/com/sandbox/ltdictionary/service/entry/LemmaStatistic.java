package com.sandbox.ltdictionary.service.entry;

import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
public class LemmaStatistic {

    private long count = 1;
    private final Set<String> words;

    public LemmaStatistic(String word) {
        this.words = new HashSet<>(Set.of(word));
    }

    public LemmaStatistic merge(LemmaStatistic other) {
        this.words.addAll(other.words);
        this.count += other.count;
        return this;
    }

    public void add(String word) {
        this.words.add(word);
        this.count++;
    }

}
