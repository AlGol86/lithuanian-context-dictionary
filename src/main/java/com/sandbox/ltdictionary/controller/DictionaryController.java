package com.sandbox.ltdictionary.controller;

import com.sandbox.ltdictionary.service.DatasetSearchingService;
import com.sandbox.ltdictionary.service.entry.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DictionaryController {

    private final DatasetSearchingService datasetSearchingService;

    @GetMapping("/learn/{word}")
    public SearchResult learnWord(@PathVariable String word, @RequestParam String direction) {
        log.info("Requested word to search: '{}' direction '{}'", word, direction);
        return datasetSearchingService.findExamples(prepareWord(word), 20, !word.contains("="), direction.equals("ru-lt"));
    }

    private static String prepareWord(String word) {
        if (Objects.isNull(word) || word.isBlank() || word.length() > 30) throw new IllegalArgumentException("wrong argument: " + word);
        return word.toLowerCase().stripTrailing().replaceAll("=", "");
    }

}


