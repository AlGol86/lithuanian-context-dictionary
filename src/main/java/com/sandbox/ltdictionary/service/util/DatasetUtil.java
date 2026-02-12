package com.sandbox.ltdictionary.service.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class DatasetUtil {

    @SneakyThrows
    public static List<Map.Entry<String, String>> loadDataset(String ltAbsPath, String ruAbsPath) {
        LocalDateTime start = LocalDateTime.now();
        List<String> ltLines = Files.readAllLines(Path.of(ltAbsPath));
        log.info("LT lines loaded. Time of loading: {}", Duration.between(start, LocalDateTime.now()).withNanos(0));
        List<String> ruLines = Files.readAllLines(Path.of(ruAbsPath));
        log.info("RU lines loaded. Time of loading: {}", Duration.between(start, LocalDateTime.now()).withNanos(0));
        Set<String> rareWords = getStatistics(ltLines).sequencedEntrySet().reversed().stream().takeWhile(entry -> entry.getValue() < 2).map(Map.Entry::getKey).map(String::toLowerCase).collect(Collectors.toCollection(TreeSet::new));
        log.info("Statistics calculated. Time of calculation: {}", Duration.between(start, LocalDateTime.now()).withNanos(0));
        List<Map.Entry<String, String>> result = new ArrayList<>(ltLines.size()/3);
        int sizeOfPairs = Math.min(ltLines.size(), ruLines.size());
        return IntStream.range(0, sizeOfPairs).mapToObj(i -> Map.entry(ltLines.get(i), ruLines.get(i))).parallel().filter(pair -> {
            Set<String> ltWords = getWordsStream(pair.getKey()).map(String::toLowerCase).collect(Collectors.toSet());
            return (ltWords.size() >= 3) && (getWordsStream(pair.getValue()).count() >= 3) && ltWords.stream().noneMatch(rareWords::contains);
        }).toList();
    }

    public static Stream<String> getWordsStream(String line) {
        return Stream.of(line)
            .flatMap(s -> Arrays.stream(s.toLowerCase().split("[,/\\\\.!?() –-]")))
            .flatMap(s -> Arrays.stream(s.split("\"")))
            .map(String::trim)
            .filter(Predicate.not(String::isBlank));
    }

    private static LinkedHashMap<String, Long> getStatistics(List<String> lines) {
        return lines.stream()
            .filter(Predicate.not(String::isBlank))
            .flatMap(DatasetUtil::getWordsStream)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

}
