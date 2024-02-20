package searchengine.service;

import org.springframework.stereotype.Service;
import searchengine.config.IndexSettings;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.service.parser.IndexSaver;
import searchengine.service.parser.IndexStatus;
import searchengine.service.parser.PageSaver;
import searchengine.service.parser.SiteParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class SitesIndexingServiceImpl implements SitesIndexingService {
    private final IndexSettings indexSettings;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final ExecutorService pool;
    private final List<Future> tasks = new ArrayList<>();
    private final IndexStatus indexStatus = new IndexStatus();
    private final PageSaver pageSaver;
    private final IndexSaver indexSaver;


    public SitesIndexingServiceImpl(
            IndexSettings indexSettings,
            SiteRepository siteRepository,
            PageRepository pageRepository,
            PageSaver pageSaver,
            IndexSaver indexSaver) {
        this.indexSettings = indexSettings;
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.pool = Executors.newFixedThreadPool(indexSettings.getSites().size());
        this.pageSaver = pageSaver;
        this.indexSaver = indexSaver;
    }

    @Override
    public boolean isIndexStarted() {
        return tasks.stream().anyMatch(t -> !t.isDone());
    }

    @Override
    public void index() {
        indexStatus.setIndexing(true);
        tasks.clear();
        for (searchengine.config.Site site : indexSettings.getSites()) {
            SiteParser siteParser = new SiteParser(
                    site.getName(),
                    site.getUrl(),
                    pageRepository,
                    siteRepository,
                    indexStatus,
                    pageSaver,
                    indexSaver);
            Future<?> feature = pool.submit(siteParser);
            tasks.add(feature);
        }
    }

    @Override
    public void stopIndex() {
        indexStatus.setIndexing(false);
    }

}
