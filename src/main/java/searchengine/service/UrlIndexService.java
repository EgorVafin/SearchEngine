package searchengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.LemmaParser;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.SiteStatus;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.SiteRepository;
import searchengine.service.parser.PageSaver;
import searchengine.utils.Tuple;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlIndexService {

    private final SiteRepository siteRepository;
    private final SitesList sitesList;
    private final PageSaver pageSaver;
    private final LemmaParser lemmaParser;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;

    public void index(String url) throws IOException {
        Optional<Site> siteOpt = sitesList.getSites().stream().filter(s -> url.startsWith(s.getUrl())).findFirst();
        if (siteOpt.isEmpty()) {
            throw new RuntimeException("Данная страница находится за пределами сайтов,\n" + "указанных в конфигурационном файле");
        }

        searchengine.model.Site siteEntity = findOrCreateSite(siteOpt.get());

        Tuple<Page, String> pageAndError = pageSaver.savePage(url, siteEntity);

        if (pageAndError.second() != null) {
            throw new RuntimeException(pageAndError.second());
        }

        String plainText = lemmaParser.clearHtmlTags(pageAndError.first().getContent());
        Map<String, Integer> lemmas = lemmaParser.parseLemmas(plainText);

        for (Map.Entry<String, Integer> lemmaStat : lemmas.entrySet()) {

            Lemma lemma = processLemma(lemmaStat.getKey(), siteEntity);

            Index index = new Index();
            index.setPage(pageAndError.first());
            index.setLemma(lemma);
            index.setRank(lemmaStat.getValue());

            indexRepository.save(index);
        }
    }

    private Lemma processLemma(String lemma, searchengine.model.Site site) {

        Optional<Lemma> lemmaOpt = lemmaRepository.findFirstByLemmaAndSite(lemma, site);
        if (lemmaOpt.isPresent()) {
            Lemma lemmaEntity = lemmaOpt.get();
            lemmaEntity.setFrequency(lemmaEntity.getFrequency() + 1);

            lemmaRepository.save(lemmaEntity);
            return lemmaEntity;
        }

        Lemma lemmaEntity = new Lemma();
        lemmaEntity.setSite(site);
        lemmaEntity.setLemma(lemma);
        lemmaEntity.setFrequency(1);

        lemmaRepository.save(lemmaEntity);
        return lemmaEntity;
    }

    private searchengine.model.Site findOrCreateSite(Site site) {

        List<searchengine.model.Site> siteList = siteRepository.findByName(site.getName());
        if (!siteList.isEmpty()) {
            return siteList.get(0);
        }

        searchengine.model.Site siteEntity = new searchengine.model.Site();
        siteEntity.setUrl(site.getUrl());
        siteEntity.setName(site.getName());
        siteEntity.setStatus(SiteStatus.INDEXING);
        siteEntity.setStatusTime(new Date());
        siteRepository.save(siteEntity);

        return siteEntity;
    }
}
