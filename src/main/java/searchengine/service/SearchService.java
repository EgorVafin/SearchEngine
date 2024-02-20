package searchengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.LemmaParser;
import searchengine.config.IndexSettings;
import searchengine.dto.search.SearchData;
import searchengine.dto.search.SearchResponse;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.SiteRepository;
import searchengine.utils.Tuple;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SearchService {
    private final IndexSettings indexSettings;
    private final LemmaParser lemmaParser;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;

    public SearchResponse search(String query, Site site, int offset, int limit) {

        Map<String, Integer> lemmasMap = lemmaParser.parseLemmas(query);
        Set<String> queryLemmas = lemmasMap.keySet();
        List<Lemma> dbLemmas = queryLemmas.stream()
                .map(lemmaRepository::findAllByLemma)
                .flatMap(Collection::stream)
                .toList();
        if (site != null) {
            dbLemmas = dbLemmas.stream()
                    .filter(lemma -> lemma.getSite() == site)
                    .toList();
        }

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
                .filter(lemma -> lemma.second() < indexSettings.getMaxFrequency())
                .sorted(Comparator.comparingInt(Tuple::second))
                .toList();

        List<Page> pages = lemmaPages(finalLemmas.stream().map(Tuple::first).toList());

        List<SearchData> searchDataList = new ArrayList<>();

        return new SearchResponse(true, 10, searchDataList);
    }


    private List<Page> lemmaPages(List<String> lemmas) {



        return null;
    }

    private List<Page> pagesForLemmas(String lemma, Site site){
        List<Lemma> lemmas = lemmasForName(lemma, site);


        indexRepository.findAllByLemma(le)
    }

    private List<Lemma> lemmasForName(String lemma, Site site){
        if(site == null){
            return lemmaRepository.findAllByLemma(lemma);
        }
        return lemmaRepository.findAllByLemmaAndSite(lemma, site);
    }
}
