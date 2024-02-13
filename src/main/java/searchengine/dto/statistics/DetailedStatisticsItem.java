package searchengine.dto.statistics;

import lombok.Data;

public class DetailedStatisticsItem {
    private String url;
    private String name;
    private String status;
    private long statusTime;
    private String error;
    private int pages;
    private int lemmas;

    public String getUrl() {
        return url;
    }

    public DetailedStatisticsItem setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getName() {
        return name;
    }

    public DetailedStatisticsItem setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public DetailedStatisticsItem setStatus(String status) {
        this.status = status;
        return this;
    }

    public long getStatusTime() {
        return statusTime;
    }

    public DetailedStatisticsItem setStatusTime(long statusTime) {
        this.statusTime = statusTime;
        return this;
    }

    public String getError() {
        return error;
    }

    public DetailedStatisticsItem setError(String error) {
        this.error = error;
        return this;
    }

    public int getPages() {
        return pages;
    }

    public DetailedStatisticsItem setPages(int pages) {
        this.pages = pages;
        return this;
    }

    public int getLemmas() {
        return lemmas;
    }

    public DetailedStatisticsItem setLemmas(int lemmas) {
        this.lemmas = lemmas;
        return this;
    }
}
