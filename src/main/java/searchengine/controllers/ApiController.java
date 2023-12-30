package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.index.IndexingResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.IndexingService;
import searchengine.services.IndexingServiceImpl;
import searchengine.services.StatisticsService;
import searchengine.services.loader.UrlDataLoadException;
import searchengine.services.loader.UrlDataLoader;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {
    private final StatisticsService statisticsService;
    private final IndexingService indexingService;


    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexingResponse> startIndexing() {
        if (indexingService.isIndexStarted()) {
            return ResponseEntity.ok(IndexingResponse.error("Индексация уже запущена"));
        }
        indexingService.index();

        return ResponseEntity.ok(IndexingResponse.success());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexingResponse> stopIndexing() {
        if (!indexingService.isIndexStarted()) {
            return ResponseEntity.ok(IndexingResponse.error("Индексация не запущена"));
        }
        indexingService.stopIndex();

        return ResponseEntity.ok(IndexingResponse.success());
    }
}
