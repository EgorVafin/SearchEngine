package searchengine.service.snippet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SentenceWithLemmas {
    private final Sentence original;
    private final Sentence lemmas;
    private final int lemmasInQuery;

}
