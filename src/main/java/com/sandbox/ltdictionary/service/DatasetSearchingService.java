package com.sandbox.ltdictionary.service;

import com.sandbox.ltdictionary.service.entry.SearchResult;
import com.sandbox.ltdictionary.service.entry.SearchSlice;
import com.sandbox.ltdictionary.service.util.DatasetUtil;
import io.micrometer.observation.Observation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Service
public class DatasetSearchingService {

    private final List<Map.Entry<String, String>> dataset;

    private final Map<String, Integer> autoscrollCounter = new ConcurrentHashMap<>();

    public DatasetSearchingService(@Value("${dataset-lt.absolute-path}") String ltAbsPath, @Value("${dataset-ru.absolute-path}") String ruAbsPath) {
        LocalDateTime start = LocalDateTime.now();
        this.dataset = DatasetUtil.loadDataset(ltAbsPath, ruAbsPath);
        log.info("Dataset has been loaded and initialized. Total time: {}", Duration.between(start, LocalDateTime.now()).withNanos(0));
    }

    public SearchResult findExamples(String word, int limit, boolean fast, boolean ruLtDirection) {
        SearchSlice matchesSlice = findMatchesSlice(word, autoscrollCounter.getOrDefault(word, 0), limit, fast, ruLtDirection ? Map.Entry::getValue : Map.Entry::getKey);
        autoscrollCounter.put(word, matchesSlice.getNextLineNumber());
        return SearchResult.of(matchesSlice);
    }

    public SearchSlice findMatchesSlice(String word, int startFrom, int pageSize, boolean fast, Function<Map.Entry<String, String>, String> partToSearchInSupplier) {
        LocalDateTime start = LocalDateTime.now();
        List<Map.Entry<String, String>> strictResult = new ArrayList<>();
        List<Map.Entry<String, String>> notStrictResult = new ArrayList<>();
        int i;
        for (i = startFrom; i < dataset.size() && strictResult.size() < pageSize; i++) {
            String lineToSearchIn = partToSearchInSupplier.apply(dataset.get(i));
            if (isContainsWord(word, lineToSearchIn, true)) {
                strictResult.add(dataset.get(i));
            } else if (isContainsWord(word, lineToSearchIn, false)) {
                notStrictResult.add(dataset.get(i));
            }
            if (fast && LocalDateTime.now().minusSeconds(6).isAfter(start)) break;
        }
        return new SearchSlice(strictResult, notStrictResult, i == dataset.size() ? 0 : i, pageSize);
    }

    private static boolean isContainsWord(String word, String line, boolean strictLithuanian) {
        if (strictLithuanian) {
            return line.toLowerCase().contains(word);
        }
        String simplifiedLIne = simplifyLithuanian(line.toLowerCase());
        String simplifiedWord = simplifyLithuanian(word.toLowerCase());
        return simplifiedLIne.contains(simplifiedWord);
    }

    private static String simplifyLithuanian(String text) {
        return text.replaceAll("ą", "a").replaceAll("[ęė]", "e").replaceAll("[įy]", "i").replaceAll("[ųū]", "u");
    }

}
