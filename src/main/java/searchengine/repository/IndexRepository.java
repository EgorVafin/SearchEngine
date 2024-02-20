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

    //native query delete index by lemma_id

    @Query(value = "DELETE FROM lemma_index WHERE lemma_id = :lemmaId", nativeQuery = true)
    void deleteByLemmaId(Integer lemmaId);


    @Query(value = "select distinct page_id from lemma_index where lemma_id IN (:lemmaId))
    List<Integer> findIndexByLemmaId(List<Integer> lemmaId);


//    @Query(value = "SELECT max(id) FROM order_line where order_id = :orderId", nativeQuery = true)
//    public Long findMaxOrderLineIdInOrder(Long orderId);
}
