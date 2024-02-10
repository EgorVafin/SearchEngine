package searchengine.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.index.IndexingResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.service.SitesIndexingService;
import searchengine.service.StatisticsService;
import searchengine.service.UrlIndexService;

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

        StatisticsResponse statisticsResponse = statisticsService.getStatistics();
        return ResponseEntity.ok(statisticsResponse);
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
