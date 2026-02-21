package com.sandbox.ltdictionary.controller;

import com.sandbox.ltdictionary.service.GrammarTestDatasetService;
import com.sandbox.ltdictionary.service.entry.grammartests.Result;
import com.sandbox.ltdictionary.service.entry.grammartests.WordTranslation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/grammar-tests")
@RequiredArgsConstructor
public class GrammarTestsController {

    private final GrammarTestDatasetService grammarTestDatasetService;
    private final Map<String, List<WordTranslation>> translationsByChapter = new HashMap<>();
    private List<ResultDto> results = new ArrayList<>();

    @GetMapping("{translationPageId}")
    public List<WordDto> getWords(@PathVariable String translationPageId) {
        return translationsByChapter.computeIfAbsent(translationPageId, grammarTestDatasetService::loadWords).stream().map(WordDto::of).toList();
    }

    @PostMapping("/results")
    public void submit(@RequestBody ResultSubmitDto dto) {

        int total = translationsByChapter.get(dto.page).size();
        int correct = dto.solvedIds().size();
        int percent = (int)Math.round((correct * 100.0) / total);

        Map<String, Result> resultMap = grammarTestDatasetService.loadResultLeaderboard();
        Optional.ofNullable(resultMap.get(dto.getKey())).filter(e -> e.score() > percent).ifPresentOrElse(
             e -> log.warn("User {} can not worsen score for {}", dto.name, dto.page),
                () -> resultMap.put(dto.getKey(), new Result(dto.name(), dto.page(), percent, LocalDateTime.now()))
        );
        grammarTestDatasetService.persist(resultMap.values());
        this.results = resultMap.values().stream().map(ResultDto::of).toList();
    }

    @GetMapping("/results")
    public List<ResultDto> getResults() {
        return results.stream().sorted(Comparator.comparing(ResultDto::score).reversed()).toList();
    }

    public record WordDto(int id, String word, String translation, String hint) {

        static WordDto of(WordTranslation wt) {
            return new WordDto(wt.id(), wt.word(), wt.translation(), wt.hint());
        }

    }
    public record ResultDto(String name, String page, int score, LocalDateTime time) {

        static ResultDto of(Result r) {
            return new ResultDto(r.name(), r.page(), r.score(), r.time());
        }

    }

    public record ResultSubmitDto(String name, String page, List<Integer> solvedIds) {

        public String getKey() {
            return String.join(":", name(), page());
        }

    }

}


