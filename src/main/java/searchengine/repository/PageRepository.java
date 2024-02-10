package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {

    List<Page> findAllBySiteAndPath(Site site, String path);

    List<Page> findAllBySite(Site site);

    //Optional<Page> findFirstByPath(String path);
    Optional<Page> findFirstByPathAndSite(String path, Site site);

    Integer countBySite(Site site);


}
