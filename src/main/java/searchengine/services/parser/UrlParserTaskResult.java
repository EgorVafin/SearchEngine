package searchengine.services.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import searchengine.model.SiteStatus;

@Getter
@RequiredArgsConstructor
public class UrlParserTaskResult {
    private final String errorMessage;
}
