package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {

    List<Index> findAllByPage(Page page);

    @Query(nativeQuery = true, value = "select distinct page_id from lemma_index where lemma_id in :lemmaIdList")
    List<Integer> findIndexByLemmaIdList(List<Integer> lemmaIdList);

    @Query(nativeQuery = true, value = "select distinct li.page_id from lemma_index li " +
            "inner join lemma l on l.id = li.lemma_id " +
            "where l.lemma = :lemma")
    Set<Integer> pagesForLemma(String lemma);

    @Query(nativeQuery = true, value = "select distinct li.page_id from lemma_index li " +
            "inner join lemma l on l.id = li.lemma_id " +
            "where l.lemma = :lemma AND l.site_id = :siteId")
    Set<Integer> pagesForLemmaAndSite(String lemma, Integer siteId);

    @Query(nativeQuery = true, value = "select li.page_id as pageId, sum(li.lemma_rank) as pageRelevance from lemma_index li " +
            "join lemma l on li.lemma_id = l.id " +
            "where li.page_id IN (:pages) " +
            "and l.lemma IN (:lemmas) " +
            "group by li.page_id")
    List<RelevanceObj> relevance(Set<Integer> pages, List<String> lemmas);

    static interface RelevanceObj {
        int getPageId();
        double getPageRelevance();
    }
}
