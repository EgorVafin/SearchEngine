package searchengine.service.parser;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.jsoup.HttpStatusException;
import org.springframework.stereotype.Service;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.PageRepository;
import searchengine.utils.StringUtils;
import searchengine.utils.Tuple;
import searchengine.utils.UrlUtils;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PageSaver {
    public static final int STATUS_CODE_SUCCESS = 200;
    private final UrlScraper urlScraper;
    private final PageRepository pageRepository;

    public Tuple<Page, String> savePage(String url, Site site) {

        try {
            String html = urlScraper.getContent(url);
            return savePage(html, url, site);
        } catch (IOException e) {
            int statusCode = e instanceof HttpStatusException ? ((HttpStatusException) e).getStatusCode() : 0;
            return saveErrorPage(statusCode, e.toString(), site, url);
        }
    }

    private Tuple<Page, String> saveErrorPage(int statusCode, String errorMessage, Site site, String url) {
        Page page = new Page();
        page.setSite(site);
        page.setPath(UrlUtils.removeDomainFromUrl(url, site.getUrl()));
        page.setContent(null);
        page.setCode(statusCode);
        pageRepository.save(page);

        return new Tuple<>(page, errorMessage);
    }

    private Tuple<Page, String> savePage(String html, String url, Site site) {
        Page page = new Page();
        page.setSite(site);
        page.setPath(UrlUtils.removeDomainFromUrl(url, site.getUrl()));
        page.setContent(html);
        page.setCode(STATUS_CODE_SUCCESS);
        pageRepository.save(page);

        return new Tuple<>(page, null);
    }

}
