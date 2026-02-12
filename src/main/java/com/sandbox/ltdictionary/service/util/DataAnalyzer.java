package com.sandbox.ltdictionary.service.util;

import com.sandbox.ltdictionary.service.entry.LemmaStatistic;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DataAnalyzer {

    public static Set<String> findMostFrequentLemmaWords(List<String> lines) {
        LinkedHashMap<String, LemmaStatistic> analysed = analyseRuDataset(lines);
        if (analysed.isEmpty()) return Set.of();
        if (analysed.size() == 1) return analysed.pollFirstEntry().getValue().getWords();
        List<LemmaStatistic> firstAndSecond = analysed.sequencedValues().stream().limit(2).toList();
        if ((firstAndSecond.getFirst().getCount() - firstAndSecond.getLast().getCount()) < 2) return Set.of();
        return firstAndSecond.getFirst().getWords();
    }

    public static LinkedHashMap<String, LemmaStatistic> analyseRuDataset(List<String> lines) {
        return lines.stream().flatMap(line -> analyseRuSentence(line).entrySet().stream()).collect(Collectors.toMap(
            Map.Entry::getKey, Map.Entry::getValue, LemmaStatistic::merge
        )).entrySet().stream().sorted(
            Comparator.comparing((Map.Entry<String, LemmaStatistic> e) -> e.getValue().getCount()).reversed()
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public static Map<String, LemmaStatistic> analyseRuSentence(String line) {
        return DatasetUtil.getWordsStream(line)
            .flatMap(word -> CachedLemmaResolver.resolveLemmas(word).stream().map(lemma -> Map.entry(lemma, word)))
        .collect(Collectors.toMap(Map.Entry::getKey, pair -> new LemmaStatistic(pair.getValue()), (a,b) -> a));
    }

}
