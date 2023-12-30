package searchengine.services;

public interface IndexingService {
    boolean isIndexStarted();
    void index();
    void stopIndex();
}
