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
        List<Page> pages = pageRepository.findAll();
        List<Lemma> lemmas = lemmaRepository.findAll();


        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.size());
        total.setPages(pages.size());
        total.setLemmas(lemmas.size());
        total.setIndexing(true); //todo что означает INDEXING ?

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        for (int i = 0; i < sites.size(); i++) {
            DetailedStatisticsItem detailedStatisticsItem = new DetailedStatisticsItem();
            Site site = sites.get(i);
            detailedStatisticsItem.setUrl(site.getUrl());
            detailedStatisticsItem.setName(site.getName());
            detailedStatisticsItem.setStatus(site.getStatus().toString());
            detailedStatisticsItem.setStatusTime(site.getStatusTime().getTime());

            if (site.getLastError() == null) {
                detailedStatisticsItem.setError("");
            } else {
                detailedStatisticsItem.setError(site.getLastError());
            }
            detailedStatisticsItem.setPages(pageRepository.findAllBySite(site).size());
            detailedStatisticsItem.setLemmas(lemmaRepository.findAllBySite(site).size());
            detailed.add(detailedStatisticsItem);
        }
        StatisticsResponse response = new StatisticsResponse(true, new StatisticsData(total, detailed));

        //todo ERROR 406 HttpMediaTypeNotAcceptableException

//        List<DetailedStatisticsItem> detailedStat = sites.stream().map(a->a).toList();
//
//        StatisticsResponse response = new StatisticsResponse()
//                .result(true)
//                .statistics(
//                        new StatisticsData()
//                                .total(new TotalStatistics())
//                                .detailed(detailedStat)
//                );
//        return response;


//        StatisticsResponse response = new StatisticsResponse()
//                .result(true)
//                .statistics(
//                  new StatisticsData()
//                          .total(total)
//                          .detailed(detailed)
//                );

        return response;
    }
//    @Override
//    public StatisticsResponse getStatistics() {
//        String[] statuses = { "INDEXED", "FAILED", "INDEXING" };
//        String[] errors = {
//                "Ошибка индексации: главная страница сайта не доступна",
//                "Ошибка индексации: сайт не доступен",
//                ""
//        };
//        TotalStatistics total = new TotalStatistics();
//        total.setSites(sites.getSites().size());
//        total.setIndexing(true);
//        List<DetailedStatisticsItem> detailed = new ArrayList<>();
//        List<Site> sitesList = sites.getSites();
//        for(int i = 0; i < sitesList.size(); i++) {
//            Site site = sitesList.get(i);
//            DetailedStatisticsItem item = new DetailedStatisticsItem();
//            item.setName(site.getName());
//            item.setUrl(site.getUrl());
//            int pages = random.nextInt(1_000);
//            int lemmas = pages * random.nextInt(1_000);
//            item.setPages(pages);
//            item.setLemmas(lemmas);
//            item.setStatus(statuses[i % 3]);
//            item.setError(errors[i % 3]);
//            item.setStatusTime(System.currentTimeMillis() -
//                    (random.nextInt(10_000)));
//            total.setPages(total.getPages() + pages);
//            total.setLemmas(total.getLemmas() + lemmas);
//            detailed.add(item);
//        }
//
//        StatisticsResponse response = new StatisticsResponse();
//        StatisticsData data = new StatisticsData();
//        data.setTotal(total);
//        data.setDetailed(detailed);
//        response.setStatistics(data);
//        response.setResult(true);
//        return response;
//    }
}
