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
import searchengine.repository.PageRepository;
import searchengine.service.snippet.Snippet;
import searchengine.utils.Tuple;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SearchService {
    private final IndexSettings indexSettings;
    private final LemmaParser lemmaParser;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final PageRepository pageRepository;
    private final Snippet snippet;

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

        List<String> finalLemmasStings = finalLemmas.stream().map(Tuple::first).toList();
        Set<Integer> pages = lemmaPages(finalLemmasStings, site);
        if (pages.isEmpty()) {
            return new SearchResponse(true, 0, new ArrayList<>());
        }

        List<IndexRepository.RelevanceObj> pageRelevances = indexRepository.relevance(pages, finalLemmasStings);

        double maxRelevance = pageRelevances.stream()
                .max(Comparator.comparing(IndexRepository.RelevanceObj::getPageRelevance))
                .get()
                .getPageRelevance();

        List<Tuple<Integer, Double>> relRelevance = pageRelevances.stream()
                .map(rel -> new Tuple<Integer, Double>(rel.getPageId(), rel.getPageRelevance() / maxRelevance))
                .sorted(Comparator.comparing(Tuple::second))
                .skip(offset)
                .limit(limit)
                .toList();

        List<Tuple<Page, Double>> pageObjRelevances = relRelevance.stream()
                .map(t -> new Tuple<Page, Double>(pageRepository.findById(t.first()).get(), t.second()))
                .toList();

        List<SearchData> pagesResponse = pageObjRelevances.stream()
                .map(t -> new SearchData()
                        .setSite(t.first().getSite().getUrl())
                        .setSiteName(t.first().getSite().getName())
                        .setUri(t.first().getPath())
                        .setTitle(snippet.createTitle(t.first().getContent()))
                        .setRelevance(t.second())
                        .setSnippet(snippet.createSnippet(t.first().getContent(), finalLemmas))
                )
                .toList();

        return new SearchResponse(true, pagesResponse.size(), pagesResponse);
    }

    private Set<Integer> lemmaPages(List<String> lemmas, Site site) {

        if (lemmas.isEmpty()) {
            return Set.of();
        }

        Set<Integer> resultPages = pagesForLemmas(lemmas.getFirst(), site);
        if (lemmas.size() == 1) {
            return resultPages;
        }

        for (String lemma : lemmas.subList(1, lemmas.size())) {
            Set<Integer> pages = pagesForLemmas(lemma, site);
            resultPages.retainAll(pages);

            if (resultPages.isEmpty()) {
                return resultPages;
            }
        }
        return resultPages;
    }

    private Set<Integer> pagesForLemmas(String lemma, Site site) {

        if (site == null) {
            return indexRepository.pagesForLemma(lemma);
        } else {
            return indexRepository.pagesForLemmaAndSite(lemma, site.getId());
        }
    }

}
