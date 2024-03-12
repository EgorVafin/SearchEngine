package searchengine.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.index.IndexPageRequestDto;
import searchengine.dto.index.IndexingResponse;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.exception.NotFoundHttpException;
import searchengine.model.Site;
import searchengine.repository.SiteRepository;
import searchengine.service.SearchService;
import searchengine.service.SitesIndexingService;
import searchengine.service.StatisticsService;
import searchengine.service.UrlIndexService;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")

public class ApiController {
    private final StatisticsService statisticsService;
    private final SitesIndexingService sitesIndexingService;
    private final UrlIndexService urlIndexService;
    private final SearchService searchService;
    private final SiteRepository siteRepository;

    @GetMapping("/search")
    public SearchResponse search(
            @RequestParam(name = "query", required = true) String query,
            @RequestParam(name = "site", required = false) String siteUrl,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "20") int limit) {

        if (siteUrl != null) {
            Optional<Site> siteOpt = siteRepository.findByUrl(siteUrl);
            if (siteOpt.isEmpty()) {
                throw new NotFoundHttpException("Site " + siteUrl + " not found");
            }
            return searchService.search(query, siteOpt.get(), offset, limit);
        } else {
            return searchService.search(query, null, offset, limit);
        }
    }

    @GetMapping("/statistics")
    public StatisticsResponse statistics() {
        return statisticsService.getStatistics();
    }

    @GetMapping("/startIndexing")
    public IndexingResponse startIndexing() {
        if (sitesIndexingService.isIndexStarted()) {
            return IndexingResponse.error("Индексация уже запущена");
        }
        sitesIndexingService.index();

        return IndexingResponse.success();
    }

    @GetMapping("/stopIndexing")
    public IndexingResponse stopIndexing() {
        if (!sitesIndexingService.isIndexStarted()) {
            return IndexingResponse.error("Индексация не запущена");
        }
        sitesIndexingService.stopIndex();

        return IndexingResponse.success();
    }

    @PostMapping("/indexPage")
    public ResponseEntity<IndexingResponse> indexPage(@RequestBody IndexPageRequestDto dto) throws IOException {

        try {
            urlIndexService.index(dto.getUrl());
            return ResponseEntity.ok()
                    .body(IndexingResponse.success());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(IndexingResponse.error(e.getMessage()));
        }
    }
}
