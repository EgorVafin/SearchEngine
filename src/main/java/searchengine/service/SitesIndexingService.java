package searchengine.service;

public interface SitesIndexingService {
    boolean isIndexStarted();
    void index();
    void stopIndex();
}
