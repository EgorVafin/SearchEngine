package searchengine.service.parser;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.utils.StringUtils;
import searchengine.utils.Tuple;
import searchengine.utils.UrlUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class UrlParserTask extends RecursiveAction {
    //@Value("${max.page.count}")
    private static final int MAX_PAGES_COUNT = 300;

    private final String url;
    private final Site site;
    private final IndexStatus indexStatus;
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final PageSaver pageSaver;
    private final IndexSaver indexSaver;

    @SneakyThrows
    @Override
    protected void compute() {
        if (!indexStatus.isIndexing()) {
            return;
        }

        int pagesCount = pageRepository.countBySite(site);
        if (pagesCount > MAX_PAGES_COUNT) {
            return;
        }

        List<Page> visitedLinks = pageRepository
                .findAllBySiteAndPath(site, UrlUtils.removeDomainFromUrl(url, site.getUrl()));
        if (!visitedLinks.isEmpty()) {
            return;
        }

        TimeUnit.MILLISECONDS.sleep(175);

        Tuple<Page, String> pageAndError = pageSaver.savePage(url, site);

        site.setStatusTime(new Date());
        if(pageAndError.second() != null) {
            site.setLastError(pageAndError.second());
        }
        siteRepository.save(site);

        if(pageAndError.second() == null) {
            if (pageAndError.second() != null) {
                throw new RuntimeException(pageAndError.second());
            }

            indexSaver.processPage(pageAndError.first());

            processDocument(pageAndError.first().getContent());
        }
    }

    private void processDocument(String html) {
        Document doc = Jsoup.parse(html);
        HtmlJsoupParser pageParser = new HtmlJsoupParser();

        Set<String> urlList = pageParser.extractLinks(doc, site.getUrl());
        if (urlList.isEmpty()) {
            return;
        }

        for (String nestedUrl : urlList) {
            UrlParserTask newTask = new UrlParserTask(nestedUrl,
                    site,
                    indexStatus,
                    pageRepository,
                    siteRepository,
                    pageSaver,
                    indexSaver);

            newTask.fork();
        }
    }
}
