package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;
import searchengine.model.Site;

import java.util.List;
import java.util.Optional;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {

    Optional<Lemma> findFirstByLemmaAndSite(String lemma, Site site);

    Optional<Lemma> findById(Integer integer);

    List<Lemma> findAllBySite(Site site);

    @Override
    void deleteById(Integer integer);
}
