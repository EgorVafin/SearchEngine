package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;
import searchengine.model.SiteStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository <Site, Integer> {

    public Optional<Site> findFirstByStatus(SiteStatus status);

    List<Site> findByName(String name);
}
