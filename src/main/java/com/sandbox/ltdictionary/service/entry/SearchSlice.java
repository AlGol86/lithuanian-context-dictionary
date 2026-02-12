package com.sandbox.ltdictionary.service.entry;

import com.sandbox.ltdictionary.service.util.DataAnalyzer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Getter
@ToString
@RequiredArgsConstructor
public class SearchSlice {

    private final List<Map.Entry<String, String>> strictMatched;
    private final List<Map.Entry<String, String>> nonStrictMatched;
    private final int nextLineNumber;
    private final int pageSize;

    public List<Map.Entry<String, String>> getCombinedLimited() {
        return Stream.concat(strictMatched.stream(), nonStrictMatched.stream()).limit(pageSize).toList();
    }

    public Set<String> getAssumedTranslations() {
        return DataAnalyzer.findMostFrequentLemmaWords(getCombinedLimited().stream().map(Map.Entry::getValue).toList());
    }

}
