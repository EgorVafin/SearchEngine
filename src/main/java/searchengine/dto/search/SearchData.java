package searchengine.dto.search;

public class SearchData {
    private String site;
    private String siteName;
    private String uri;
    private String title;
    private String snippet;
    private double relevance;

    public String getSite() {
        return site;
    }

    public SearchData setSite(String site) {
        this.site = site;
        return this;
    }

    public String getSiteName() {
        return siteName;
    }

    public SearchData setSiteName(String siteName) {
        this.siteName = siteName;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public SearchData setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public SearchData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSnippet() {
        return snippet;
    }

    public SearchData setSnippet(String snippet) {
        this.snippet = snippet;
        return this;
    }

    public double getRelevance() {
        return relevance;
    }

    public SearchData setRelevance(Double relevance) {
        this.relevance = relevance;
        return this;
    }
}
