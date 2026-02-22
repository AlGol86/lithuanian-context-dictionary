package com.sandbox.ltdictionary.service;

import com.sandbox.ltdictionary.service.entry.grammartests.Result;
import com.sandbox.ltdictionary.service.entry.grammartests.WordTranslation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class GrammarTestDatasetService {

    private final ObjectMapper objectMapper;
    private final String absPathToResults;

    public GrammarTestDatasetService(@Value("${grammar-tests-results.absolute-path}") String absPathToResults, ObjectMapper objectMapper) {
        this.absPathToResults = absPathToResults;
        this.objectMapper = objectMapper;
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
        return getLineStream(chapterName).map(line -> {
            String[] elements = line.split(",");
            if (elements.length < 2) return null;
            if ((elements.length > 2)) return new WordTranslation(cntr.getAndIncrement(), elements[0].trim(), elements[1].trim(), elements[2].trim());
            return new WordTranslation(cntr.getAndIncrement(), elements[0].trim(), elements[1].trim(), null);
        }).filter(Objects::nonNull).toList();
    }

    @SneakyThrows
    public List<WordTranslation> loadPhrases(String chapterName) {
        AtomicInteger cntr = new AtomicInteger(0);
        return getLineStream(chapterName).map(line -> {
            String[] elements = line.split("\\|");
            if (elements.length != 2) return null;
            return new WordTranslation(cntr.getAndIncrement(), elements[0].trim(), elements[1].trim(), null);
        }).filter(Objects::nonNull).toList();
    }

    private static @NonNull Stream<String> getLineStream(String chapterName) throws IOException, URISyntaxException {
        return Files.readAllLines(Path.of(GrammarTestDatasetService.class.getResource("/text-materials-for-tasks/" + chapterName + ".txt").toURI())).stream().filter(Predicate.not(String::isBlank));
    }

}
