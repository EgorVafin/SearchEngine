package searchengine.service.parser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import searchengine.LemmaParser;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IndexSaver {
    private final LemmaParser lemmaParser;
    private final IndexRepository indexRepository;
    private final LemmaRepository lemmaRepository;

    public void processPage(Page page) {
        String plainText = lemmaParser.clearHtmlTags(page.getContent());
        Map<String, Integer> lemmas = lemmaParser.parseLemmas(plainText);

        for (Map.Entry<String, Integer> lemmaStat : lemmas.entrySet()) {

            Lemma lemma = processLemma(lemmaStat.getKey(), page.getSite());

            Index index = new Index();
            index.setPage(page);
            index.setLemma(lemma);
            index.setRank(lemmaStat.getValue());

            indexRepository.save(index);
        }

    }

    private Lemma processLemma(String lemma, searchengine.model.Site site) {

        Optional<Lemma> lemmaOpt = lemmaRepository.findFirstByLemmaAndSite(lemma, site);
        if (lemmaOpt.isPresent()) {
            Lemma lemmaEntity = lemmaOpt.get();
            lemmaEntity.setFrequency(lemmaEntity.getFrequency() + 1);

            lemmaRepository.save(lemmaEntity);
            return lemmaEntity;
        }

        Lemma lemmaEntity = new Lemma();
        lemmaEntity.setSite(site);
        lemmaEntity.setLemma(lemma);
        lemmaEntity.setFrequency(1);

        lemmaRepository.save(lemmaEntity);
        return lemmaEntity;
    }


}
