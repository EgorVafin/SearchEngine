package searchengine.service.snippet;


import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.store.SingleInstanceLockFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import searchengine.LemmaParser;
import searchengine.utils.Tuple;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@Data
@RequiredArgsConstructor
public class Snippet {
    private final LemmaParser lemmaParser;

    public String createSnippet(String pageContent, Set<String> lemmas) {

        Document doc = Jsoup.parse(pageContent);
        String text = doc.body().text();

        String[] sentences = text.split("[.!?]");
        final List<Sentence> sentenceWords = Arrays.stream(sentences).map(s -> new Sentence(List.of(s.split(" "))))
                .toList();

        List<Sentence> sentenceLemmas = sentenceWords.stream()
                .map(sentence -> new Sentence(
                                sentence.getWords().stream()
                                        .map(w -> w.toLowerCase(Locale.ROOT).replaceAll("[^а-я]", ""))
                                        .map(lemmaParser::getNormalLemma)
                                        .toList()
                        )
                )
                .toList();

        Map<Integer, Boolean> sentenceHasSearchLemmas = new TreeMap<>();
        for (int i = 0; i < sentenceLemmas.size(); i++) {
            boolean sentenceHasSearchLemma = sentenceLemmas.get(i).getWords().stream().anyMatch(lemma -> lemmas.contains(lemma));
            sentenceHasSearchLemmas.put(i, sentenceHasSearchLemma);
        }

        List<SentenceWithLemmas> sentencesHasLemmas = IntStream.range(0, sentenceWords.size())
                .mapToObj(i -> sentenceHasSearchLemmas.get(i)
                        ? new SentenceWithLemmas(
                        sentenceWords.get(i),
                        sentenceLemmas.get(i),
                        lemmasInQuery(sentenceLemmas.get(i), lemmas))
                        : null)
                .filter(s -> s != null)
                .toList();


        boolean hasGoodSentence = sentencesHasLemmas.stream().anyMatch(sl -> this.isGoodSentence(sl.getOriginal()));
        if (hasGoodSentence) {
            // Только подходящие предложения, если они есть. Если нет, то оставляем все предложения
            sentencesHasLemmas = sentencesHasLemmas.stream().filter(sl -> this.isGoodSentence(sl.getOriginal())).toList();
        }

        sentencesHasLemmas = sentencesHasLemmas.stream().sorted(Comparator.comparingInt(SentenceWithLemmas::getLemmasInQuery))
                .toList();

        String snippet = buildSnippet(sentencesHasLemmas, lemmas);
        return snippet + "...";
    }

    private String buildSnippet( List<SentenceWithLemmas> sentencesHasLemmas, Set<String> lemmas) {
        StringBuilder snippet = new StringBuilder();
        for( SentenceWithLemmas sentence: sentencesHasLemmas) {

            for(int i=0; i< sentence.getLemmas().getWords().size(); i++) {
                String lemma = sentence.getLemmas().getWords().get(i);
                String word = sentence.getOriginal().getWords().get(i);
                if(lemmas.contains(lemma)) {
                    snippet.append("<b>").append(word).append("</b>");
                } else {
                    snippet.append(word);
                }
                snippet.append(" ");

                if(snippet.length() > 600){
                    return snippet.toString();
                }
            }
        }

        return snippet.toString();
    }

    private int lemmasInQuery(Sentence sentence, Set<String> lemmas) {
        return (int) sentence.getWords().stream().filter(l -> lemmas.contains(l)).count();
    }

    private boolean isGoodSentence(Sentence sentence) {
        return sentence.getWords().size() > 4 && sentence.getWords().size() < 30;
    }

    public String createTitle(String pageContent) {
        return Jsoup.parse(pageContent).title();
    }
}
