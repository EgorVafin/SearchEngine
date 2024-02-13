package searchengine.dto.statistics;

import lombok.Data;

public class TotalStatistics {
    private int sites;
    private int pages;
    private int lemmas;
    private boolean indexing;

    public int getSites() {
        return sites;
    }

    public TotalStatistics setSites(int sites) {
        this.sites = sites;
        return this;
    }

    public int getPages() {
        return pages;
    }

    public TotalStatistics setPages(int pages) {
        this.pages = pages;
        return this;
    }

    public int getLemmas() {
        return lemmas;
    }

    public TotalStatistics setLemmas(int lemmas) {
        this.lemmas = lemmas;
        return this;
    }

    public boolean isIndexing() {
        return indexing;
    }

    public TotalStatistics setIndexing(boolean indexing) {
        this.indexing = indexing;
        return this;
    }
}
