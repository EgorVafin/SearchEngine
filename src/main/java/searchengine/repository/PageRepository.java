package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Integer> {

    List<Page> findAllBySiteAndPath(Site site, String path);

    Integer countBySite(Site site);


}
