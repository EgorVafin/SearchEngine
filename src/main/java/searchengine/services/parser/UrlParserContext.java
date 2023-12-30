package searchengine.services.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import searchengine.model.Site;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

@Getter
@RequiredArgsConstructor
public class UrlParserContext {
    private final Site site;
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final IndexStatus indexStatus;
}
