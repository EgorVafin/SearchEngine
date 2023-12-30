package searchengine.services.parser;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.model.Page;
import searchengine.model.SiteStatus;
import searchengine.utils.StringUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class UrlParserTask extends RecursiveTask<UrlParserTaskResult> {
    private final String url;
    private final UrlParserContext context;
    private static final int MAX_PAGES_COUNT = 100;

    @SneakyThrows
    @Override
    protected UrlParserTaskResult compute() {
        if (!context.getIndexStatus().isIndexing()) {
            return new UrlParserTaskResult("Индексация остановлена пользователем");
        }

        List<Page> visitedLinks = context.getPageRepository()
                .findAllBySiteAndPath(context.getSite(), removeDomainFromUrl(url, context.getSite().getUrl()));
        if (!visitedLinks.isEmpty()) {
            return null;
        }

        int pagesCount = context.getPageRepository().countBySite(context.getSite());
        if (pagesCount > MAX_PAGES_COUNT) {
            return null;
        }

        try {
            TimeUnit.MILLISECONDS.sleep(200);
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .maxBodySize(0)
                    .get();

            saveDocument(doc);
            processDocument(doc);
            return null;
        } catch (IOException e) {
            if (url.equals(context.getSite().getUrl())) {
                return new UrlParserTaskResult(e.getMessage());
            } else {
                int statusCode = e instanceof HttpStatusException ? ((HttpStatusException) e).getStatusCode() : 0;
                saveErrorPage(statusCode, e.toString());
                return null;
            }
        }
    }

    private void saveErrorPage(int statusCode, String errorMessage) {
        Page page = new Page();
        page.setSite(context.getSite());
        page.setPath(removeDomainFromUrl(url, context.getSite().getUrl()));
        page.setContent(null);
        page.setCode(statusCode);
        context.getPageRepository().save(page);

        context.getSite().setStatusTime(new Date());
        context.getSite().setLastError(errorMessage);
        context.getSiteRepository().save(context.getSite());
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

    private void saveDocument(Document doc) {
        Page page = new Page();
        page.setSite(context.getSite());
        page.setPath(removeDomainFromUrl(url, context.getSite().getUrl()));
        page.setContent(doc.html());
        page.setCode(200);
        context.getPageRepository().save(page);

        context.getSite().setStatusTime(new Date());
        context.getSiteRepository().save(context.getSite());
    }

    private void processDocument(Document doc) {
        HtmlJsoupParser pageParser = new HtmlJsoupParser();

        Set<String> urlList = pageParser.extractLinks(doc, context.getSite().getUrl());
        if (urlList.isEmpty()) {
            return;
        }

        for (String nestedUrl : urlList) {
            UrlParserTask newTask = new UrlParserTask(nestedUrl, context);

            newTask.fork();
        }
    }
}
