package searchengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.model.SiteStatus;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;

    @Override
    public StatisticsResponse getStatistics() {
        List<Site> sites = siteRepository.findAll();

        TotalStatistics total = new TotalStatistics()
                .setSites(sites.size())
                .setPages((int) pageRepository.count())
                .setLemmas((int) lemmaRepository.count())
                .setIndexing(sites.stream().anyMatch(s -> s.getStatus() == SiteStatus.INDEXING));

        List<DetailedStatisticsItem> detailed = sites.stream()
                .map(site -> new DetailedStatisticsItem()
                        .setUrl(site.getUrl())
                        .setName(site.getName())
                        .setStatus(site.getStatus().toString())
                        .setStatusTime(site.getStatusTime().getTime())
                        .setError(site.getLastError())
                        .setPages(pageRepository.countBySite(site))
                        .setLemmas(lemmaRepository.countBySite(site))
                )
                .toList();

        return new StatisticsResponse()
                .setResult(true)
                .setStatistics(
                        new StatisticsData()
                                .setTotal(total)
                                .setDetailed(detailed)
                );
    }
}
