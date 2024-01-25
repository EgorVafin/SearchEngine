package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.Page;
import searchengine.model.SiteStatus;
import searchengine.repository.SiteRepository;
import searchengine.services.parser.PageSaver;
import searchengine.services.parser.UrlScraper;
import searchengine.utils.Tuple;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlIndexService {

    private final SiteRepository siteRepository;
    private final SitesList sitesList;
    private final PageSaver pageSaver;

    public void index(String url) throws IOException {
        Optional<Site> siteOpt = sitesList.getSites()
                .stream()
                .filter(s -> url.startsWith(s.getUrl()))
                .findFirst();
        if (siteOpt.isEmpty()) {
            throw new RuntimeException("Данная страница находится за пределами сайтов,\n" +
                    "указанных в конфигурационном файле");
        }

        searchengine.model.Site siteEntity = findOrCreateSite(siteOpt.get());

        Tuple<Page, String> pageAndError = pageSaver.savePage(url, siteEntity);

        if (pageAndError.second() != null) {
            throw new RuntimeException(pageAndError.second());
        }



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
