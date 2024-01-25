package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.index.IndexingResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.SitesIndexingService;
import searchengine.services.StatisticsService;
import searchengine.services.UrlIndexService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {
    private final StatisticsService statisticsService;
    private final SitesIndexingService sitesIndexingService;
    private final UrlIndexService urlIndexService;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexingResponse> startIndexing() {
        if (sitesIndexingService.isIndexStarted()) {
            return ResponseEntity.ok(IndexingResponse.error("Индексация уже запущена"));
        }
        sitesIndexingService.index();

        return ResponseEntity.ok(IndexingResponse.success());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexingResponse> stopIndexing() {
        if (!sitesIndexingService.isIndexStarted()) {
            return ResponseEntity.ok(IndexingResponse.error("Индексация не запущена"));
        }
        sitesIndexingService.stopIndex();

        return ResponseEntity.ok(IndexingResponse.success());
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
