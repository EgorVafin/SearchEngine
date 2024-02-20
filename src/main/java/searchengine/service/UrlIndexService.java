package searchengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.IndexSettings;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.SiteStatus;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.service.parser.IndexSaver;
import searchengine.service.parser.PageSaver;
import searchengine.utils.Tuple;
import searchengine.utils.UrlUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlIndexService {

    private final SiteRepository siteRepository;
    private final IndexSettings indexSettings;
    private final PageSaver pageSaver;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final PageRepository pageRepository;
    private final IndexSaver indexSaver;

    public void index(String url) throws IOException {
        Optional<Site> siteOpt = indexSettings.getSites().stream().filter(s -> url.startsWith(s.getUrl())).findFirst();
        if (siteOpt.isEmpty()) {
            throw new RuntimeException("Данная страница находится за пределами сайтов,\n" + "указанных в конфигурационном файле");
        }

        searchengine.model.Site siteEntity = findOrCreateSite(siteOpt.get());

        String path = UrlUtils.removeDomainFromUrl(url, siteOpt.get().getUrl());

        Optional<Page> pageOpt = pageRepository.findFirstByPathAndSite(path, siteEntity);
        pageOpt.ifPresent(this::removePage);

        Tuple<Page, String> pageAndError = pageSaver.savePage(url, siteEntity);

        if (pageAndError.second() != null) {
            throw new RuntimeException(pageAndError.second());
        }

        indexSaver.processPage(pageAndError.first());
    }

    private void removePage(Page page) {
        List<Index> indexList = indexRepository.findAllByPage(page);

        for (Index index : indexList) {
            Lemma lemma = index.getLemma();
            if(lemma.getFrequency() <= 1){
                indexRepository.delete(index);
                lemmaRepository.delete(lemma);
            } else {
                lemma.setFrequency(lemma.getFrequency() - 1);
                lemmaRepository.save(lemma);
                indexRepository.delete(index);
            }
        }

        pageRepository.delete(page);
    }

    private searchengine.model.Site findOrCreateSite(Site site) {

        List<searchengine.model.Site> siteList = siteRepository.findByName(site.getName());
        if (!siteList.isEmpty()) {
            return siteList.getFirst();
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
