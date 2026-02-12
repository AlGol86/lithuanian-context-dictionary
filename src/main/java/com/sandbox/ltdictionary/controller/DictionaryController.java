package com.sandbox.ltdictionary.controller;

import com.sandbox.ltdictionary.service.DatasetSearchingService;
import com.sandbox.ltdictionary.service.entry.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DictionaryController {

    private final DatasetSearchingService datasetSearchingService;

    @GetMapping("/learn/{word}")
    public SearchResult learnWord(@PathVariable String word) {
        log.info("Requested word to search: '{}'", word);
        return datasetSearchingService.findExamples(word.toLowerCase().stripTrailing().replaceAll("=", ""), 20, !word.contains("="));
    }

}


