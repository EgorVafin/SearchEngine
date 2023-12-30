package searchengine.services;

import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.model.Site;
import searchengine.model.SiteStatus;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.parser.IndexStatus;
import searchengine.services.parser.SiteParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class IndexingServiceImpl implements IndexingService {
    private final SitesList sitesList;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final ExecutorService pool;
    private final List<Future> tasks = new ArrayList<>();
    private final IndexStatus indexStatus = new IndexStatus();


    public IndexingServiceImpl(SitesList sitesList, SiteRepository siteRepository, PageRepository pageRepository) {
        this.sitesList = sitesList;
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.pool = Executors.newFixedThreadPool(sitesList.getSites().size());
    }

    @Override
    public boolean isIndexStarted() {
        return tasks.stream().anyMatch(t -> !t.isDone());
    }

    @Override
    public void index() {
        indexStatus.setIndexing(true);
        tasks.clear();
        for (searchengine.config.Site site : sitesList.getSites()) {
            SiteParser siteParser = new SiteParser(
                    site.getUrl(),
                    site.getName(),
                    pageRepository,
                    siteRepository,
                    indexStatus);
            Future<?> feature = pool.submit(siteParser);
            tasks.add(feature);
        }
    }

    @Override
    public void stopIndex() {
        indexStatus.setIndexing(false);
    }

}
