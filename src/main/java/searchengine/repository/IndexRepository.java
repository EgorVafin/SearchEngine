package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {

    List<Index> findAllByPage(Page page);

    @Query(nativeQuery = true, value = "DELETE FROM lemma_index WHERE lemma_id = :lemmaId")
    void deleteByLemmaId(Integer lemmaId);

    @Query(nativeQuery = true, value = "select distinct page_id from lemma_index where lemma_id in :lemmaIdList")
    List<Integer> findIndexByLemmaIdList(List<Integer> lemmaIdList);

    Optional<Index> findById(Integer id);

}
