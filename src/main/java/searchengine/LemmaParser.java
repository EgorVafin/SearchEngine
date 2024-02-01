package searchengine;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
//@RequiredArgsConstructor
public class LemmaParser {
    private final LuceneMorphology luceneMorph;
    private static final String RUSSIAN_WORD_REGEX = "[а-яА-Я]+";
    private static final List<String> PARTICLES_NAMES = List.of("МЕЖД", "ПРЕДЛ", "СОЮЗ");

    public LemmaParser() throws IOException {
        luceneMorph = new RussianLuceneMorphology();
    }

    public String clearHtmlTags(String html) {
        return Jsoup.parse(html).text();
    }

    public Map<String, Integer> parseLemmas(String text) {
        Map<String, Integer> result = new HashMap<>();

        String[] russianWords = arrayContainsRussianWords(text);
        Arrays.stream(russianWords)
                .filter(word -> !word.isEmpty())
                .filter(this::isFullWord)
                .map(word -> {
                    var normalForms = luceneMorph.getNormalForms(word);
                    return !normalForms.isEmpty() ? normalForms.get(0) : null;
                })
                .filter(Objects::nonNull)
                .forEach(word -> {
                    if (result.containsKey(word)) {
                        result.put(word, result.get(word) + 1);
                    } else {
                        result.put(word, 1);
                    }
                });

        return result;
    }

    private boolean isFullWord(String word) {
        return luceneMorph.getMorphInfo(word)
                .stream()
                .noneMatch(this::isParticle);
    }

    private boolean isParticle(String morphInfo) {
        return PARTICLES_NAMES.stream()
                .anyMatch(pName -> morphInfo.toUpperCase().contains(pName));
    }

    private String[] arrayContainsRussianWords(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("([^а-я\\s])", " ")
                .trim()
                .split("\\s+");
    }


}
