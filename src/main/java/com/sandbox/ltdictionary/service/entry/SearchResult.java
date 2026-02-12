package com.sandbox.ltdictionary.service.entry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@ToString
@RequiredArgsConstructor
public class SearchResult {

    private final List<String> ltPhrases;
    private final List<String> ruPhrases;
    private final Set<String> ruAssumedTranslations;

    public static SearchResult of(SearchSlice searchSlice) {
        List<String> lt = searchSlice.getCombinedLimited().stream().map(Map.Entry::getKey).toList();
        List<String> ru = searchSlice.getCombinedLimited().stream().map(Map.Entry::getValue).toList();
        return new SearchResult(lt, ru, searchSlice.getAssumedTranslations());
    }

}
