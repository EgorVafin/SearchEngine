package searchengine.service.snippet;

import java.util.List;

public class Sentence {
    private final List<String> words;

    public Sentence(List<String> words) {
        this.words = words;
    }

    public List<String> getWords() {
        return words;
    }
}
