package searchengine.services;

public interface SitesIndexingService {
    boolean isIndexStarted();
    void index();
    void stopIndex();
}
