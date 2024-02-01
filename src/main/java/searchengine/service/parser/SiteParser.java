package searchengine.service.parser;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import searchengine.model.Site;
import searchengine.model.SiteStatus;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class SiteParser implements Runnable {
    private final String siteName;
    private final String siteUrl;
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final IndexStatus indexStatus;
    private final PageSaver pageSaver;


    @SneakyThrows
    public void run() {
        clearDb();
        Site site = createSite();

        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        try {
            forkJoinPool.invoke(
                    new UrlParserTask(
                            site.getUrl(),
                            site,
                            indexStatus,
                            pageRepository,
                            siteRepository,
                            pageSaver
                    )
            );
            while (true) {
                if (forkJoinPool.getQueuedTaskCount() == 0) {
                    break;
                } else {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
            }

            if (!indexStatus.isIndexing()) {
                site.setStatus(SiteStatus.FAILED);
                site.setLastError("Индексация остановлена пользователем");
            } else {
                if (pageRepository.countBySite(site) <= 1) {
                    site.setStatus(SiteStatus.FAILED);
                } else {
                    site.setStatus(SiteStatus.INDEXED);
                }
            }

            site.setStatusTime(new Date());
            siteRepository.save(site);
        } finally {
            forkJoinPool.shutdown();
        }
    }

    private Site createSite() {
        Site site = new Site();
        site.setName(siteName);
        site.setUrl(siteUrl);
        site.setStatusTime(new Date());
        site.setStatus(SiteStatus.INDEXING);
        siteRepository.save(site);

        return site;
    }

    private void clearDb() {
        List<Site> sites = siteRepository.findByName(siteName);
        siteRepository.deleteAll(sites);
    }
}
