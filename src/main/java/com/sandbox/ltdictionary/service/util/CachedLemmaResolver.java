package com.sandbox.ltdictionary.service.util;

import com.github.demidko.aot.WordformMeaning;
import com.github.demidko.aot.morphology.MorphologyTag;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CachedLemmaResolver {

    private static final Set<MorphologyTag> IGNORED_MORPHOLOGY = Set.of(
        MorphologyTag.Pretext, MorphologyTag.Union, MorphologyTag.Name, MorphologyTag.MiddleName, MorphologyTag.Surname,
        MorphologyTag.Pronoun, MorphologyTag.Particle, MorphologyTag.Interjection, MorphologyTag.PronounAdjective, MorphologyTag.PronounPredicative
    );

    private static final Set<String> IGNORED_LEMMAS = Set.of(
        "что", "мочь", "да", "нет", "как", "так", "все", "быть", "есть", "вот", "где", "там", "тут", "тута", "раз", "тоже", "том", "под", "ага"
    );

    private static final Map<String, Set<String>> LEMMA_CACHE = new ConcurrentHashMap<>();

    public static Set<String> resolveLemmas(String word) {
        return LEMMA_CACHE.computeIfAbsent(word, CachedLemmaResolver::resolve);
    }

    private static Set<String> resolve(String w) {
        return WordformMeaning.lookupForMeanings(w).stream()
            .map(WordformMeaning::getLemma)
            .filter(lemma -> lemma.getMorphology().stream().noneMatch(IGNORED_MORPHOLOGY::contains))
            .map(Objects::toString)
            .filter(Predicate.not(IGNORED_LEMMAS::contains))
        .collect(Collectors.toSet());
    }

}
