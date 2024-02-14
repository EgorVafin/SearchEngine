package searchengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.LemmaParser;
import searchengine.dto.search.SearchData;
import searchengine.dto.search.SearchResponse;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repository.LemmaRepository;
import searchengine.utils.Tuple;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SearchService {
    public static final int MAX_FREQUENCY = 500;
    private final LemmaParser lemmaParser;
    private final LemmaRepository lemmaRepository;

    public SearchResponse search(String query) {

        Map<String, Integer> lemmasMap = lemmaParser.parseLemmas(query);
        Set<String> queryLemmas = lemmasMap.keySet();
        List<Lemma> dbLemmas = queryLemmas.stream()
                .map(lemmaRepository::findAllByLemma)
                .flatMap(Collection::stream)
                .toList();

        Map<String, Integer> uniqueLemmasMap = new HashMap<>();
        dbLemmas.forEach(lemma -> {
            if (uniqueLemmasMap.containsKey(lemma.getLemma())) {
                uniqueLemmasMap.put(lemma.getLemma(), uniqueLemmasMap.get(lemma.getLemma()) + lemma.getFrequency());
            } else {
                uniqueLemmasMap.put(lemma.getLemma(), lemma.getFrequency());
            }
        });

        List<Tuple<String, Integer>> uniqueLemmas = uniqueLemmasMap.entrySet().stream()
                .map(entry -> new Tuple<String, Integer>(entry.getKey(), entry.getValue()))
                .toList();

        List<Tuple<String, Integer>> finalLemmas = uniqueLemmas.stream()
                .filter(lemma -> lemma.second() < MAX_FREQUENCY)
                .sorted(Comparator.comparingInt(Tuple::second))
                .toList();

        //List<Page> = findPagesByLemmas();
        List<SearchData> searchDataList = new ArrayList<>();

        return new SearchResponse(true, 10, searchDataList);
    }
}
