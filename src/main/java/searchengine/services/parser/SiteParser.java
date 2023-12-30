package searchengine.services.parser;

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
    private final String siteUrl;
    private final String siteName;
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final IndexStatus indexStatus;

    @SneakyThrows
    public void run() {
        clearDb();
        Site site = createSite();

        UrlParserContext context = new UrlParserContext(site, pageRepository, siteRepository, indexStatus);
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        try {
            UrlParserTaskResult taskResult = forkJoinPool.invoke(new UrlParserTask(site.getUrl(), context));
            while (true){
                if (forkJoinPool.getQueuedTaskCount() == 0) {
                    break;
                } else {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
            }

            if(taskResult != null){
                site.setStatus(SiteStatus.FAILED);
                site.setLastError(taskResult.getErrorMessage());
            } else {
                if(!indexStatus.isIndexing()){
                    site.setStatus(SiteStatus.FAILED);
                    site.setLastError("Индексация остановлена пользователем");
                } else {
                    site.setStatus(SiteStatus.INDEXED);
                }
            }
            site.setStatusTime(new Date());
            siteRepository.save(site);
        } finally {
            forkJoinPool.shutdown();
            System.out.println("FINALLY ---------------------------------------------------");
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
