package searchengine.service.parser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexStatus {
    private volatile boolean isIndexing = false;
}
