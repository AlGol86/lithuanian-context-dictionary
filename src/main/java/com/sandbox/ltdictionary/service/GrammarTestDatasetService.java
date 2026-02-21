package com.sandbox.ltdictionary.service;

import com.sandbox.ltdictionary.service.entry.grammartests.Result;
import com.sandbox.ltdictionary.service.entry.grammartests.WordTranslation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GrammarTestDatasetService {

    private final ObjectMapper objectMapper;
    private final String absPathToResults;
    private final String absPathToWordTranslationsPrefix;

    public GrammarTestDatasetService(
        @Value("${grammar-tests-results.absolute-path}") String absPathToResults,
        @Value("${word-translations-prefix.absolute-path}") String absPathToWordTranslationsPrefix,
        ObjectMapper objectMapper
    ) {
        this.absPathToResults = absPathToResults;
        this.objectMapper = objectMapper;
        this.absPathToWordTranslationsPrefix = absPathToWordTranslationsPrefix;
    }

    @SneakyThrows
    public void persist(Collection<Result> results) {
        Files.writeString(Path.of(absPathToResults), objectMapper.writeValueAsString(results));
    }

    @SneakyThrows
    public Map<String,Result> loadResultLeaderboard() {
        try {
            return objectMapper.readValue(Path.of(absPathToResults), new TypeReference<List<Result>>() {}).stream().collect(Collectors.toMap(e -> String.join(":", e.name(), e.page()), Function.identity()));
        } catch (Exception e) {
            log.error("Unable to load: {}", absPathToResults, e);
            return new HashMap<>();
        }
    }

    @SneakyThrows
    public List<WordTranslation> loadWords(String chapterName) {
        AtomicInteger cntr = new AtomicInteger(0);
        return Files.readAllLines(Path.of(absPathToWordTranslationsPrefix + chapterName + ".txt")).stream().filter(Predicate.not(String::isBlank)).map(line -> {
                    String[] elements = line.split(",");
                    if (elements.length < 2) return null;
                    if ((elements.length > 2)) return new WordTranslation(cntr.getAndIncrement(), elements[0].trim(), elements[1].trim(), elements[2].trim());
                    return new WordTranslation(cntr.getAndIncrement(), elements[0], elements[1], null);
        }).toList();
    }

}
