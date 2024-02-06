package searchengine.service.parser;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    private static final int MAX_PAGES_COUNT = 100;

    private final String url;
    private final Site site;
    private final IndexStatus indexStatus;
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final PageSaver pageSaver;

    @SneakyThrows
    @Override
    protected void compute() {
        if (!indexStatus.isIndexing()) {
            return;
        }

        List<Page> visitedLinks = pageRepository
                .findAllBySiteAndPath(site, UrlUtils.removeDomainFromUrl(url, site.getUrl()));
        if (!visitedLinks.isEmpty()) {
            return;
        }

        int pagesCount = pageRepository.countBySite(site);
        if (pagesCount > MAX_PAGES_COUNT) {
            return;
        }

        TimeUnit.MILLISECONDS.sleep(200);

        Tuple<Page, String> pageAndError = pageSaver.savePage(url, site);


        site.setStatusTime(new Date());
        if(pageAndError.second() != null) {
            site.setLastError(pageAndError.second());
        }
        siteRepository.save(site);

        if(pageAndError.second() == null) {
            // todo save lemma and index
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
                    pageSaver);

            newTask.fork();
        }
    }
}