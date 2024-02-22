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

        //List<Integer> pagesIdList = new ArrayList<>();
        List<Page> pages = lemmaPages(finalLemmas.stream().map(Tuple::first).toList(), site);


        List<SearchData> searchDataList = new ArrayList<>();

        return new SearchResponse(true, 10, searchDataList);
    }


    private List<Page> lemmaPages(List<String> lemmas, Site site) {

        List<Page> allPagesLemmaList = new ArrayList<>();
        List<Integer> intersectionPageIdList = new ArrayList<>();

        for (String lemma : lemmas) {


            List<Page> pageForOneLemmaList = pagesForLemmas(lemma, site);

            intersectionPageIdList = pageForOneLemmaList.stream()
                    .map(p -> p.getId()).toList();


            List<Page> previousPageList = pageForOneLemmaList.stream().toList();


        }


        return null;
    }

    private List<Page> pagesForLemmas(String lemma, Site site) {
        List<Lemma> lemmas = lemmasForName(lemma, site); // из слова нашли список лемм

        List<Integer> lemmaIdList = lemmas.stream().map(Lemma::getId).toList(); // из списка лемм сделали список lemmaId


        List<Integer> pageIdList = indexRepository.findIndexByLemmaIdList(lemmaIdList); // по списку lemmaId находим список pageId

        List<Page> pagesForOneLemma = new ArrayList<>();

        for (Integer pageId : pageIdList) {
            Optional<Page> optionalPage = pageRepository.findPageById(pageId);
            optionalPage.ifPresent(pagesForOneLemma::add);
        }


        return pagesForOneLemma;
    }

    private List<Lemma> lemmasForName(String lemma, Site site) {
        if (site == null) {
            return lemmaRepository.findAllByLemma(lemma);
        }
        return lemmaRepository.findAllByLemmaAndSite(lemma, site);
    }
}
