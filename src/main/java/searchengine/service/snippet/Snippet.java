package searchengine.service.snippet;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import searchengine.LemmaParser;
import searchengine.utils.Tuple;

import java.util.*;

@Component
@Data
@RequiredArgsConstructor
public class Snippet {
    private String snippet = "";
    private final LemmaParser lemmaParser;

    public String createSnippet(String pageContent, List<Tuple<String, Integer>> finalLemmas) {

        Document doc = Jsoup.parse(pageContent);
        String text = doc.body().text();

        List<String> sentences = Arrays.stream(text.split("[.]")).toList();

        List<Map<String, Integer>> sentencesInLemma = new ArrayList<>();
        Map<String, Integer> lemmas = new HashMap<>();
        for(String sen:sentences) {
            lemmas = lemmaParser.parseLemmas(sen);
            sentencesInLemma.add(lemmas);
        }

        var firstLemma = finalLemmas.get(0).first();


        return snippet;
    }

    public String createTitle(String pageContent) {

        return Jsoup.parse(pageContent).title();
    }

}
