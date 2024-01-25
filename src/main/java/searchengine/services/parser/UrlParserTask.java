package searchengine.services.parser;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.utils.StringUtils;
import searchengine.utils.Tuple;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
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
                .findAllBySiteAndPath(site, removeDomainFromUrl(url, site.getUrl()));
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
            processDocument(pageAndError.first().getContent());
        }
    }


    private String removeDomainFromUrl(String url, String domain) {
        if (domain.endsWith("/")) {
            domain = StringUtils.removeLastChar(domain);
        }

        if (url.startsWith(domain)) {
            String substr = url.substring(domain.length());
            return substr.isEmpty() ? "/" : substr;
        }
        return url;
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
