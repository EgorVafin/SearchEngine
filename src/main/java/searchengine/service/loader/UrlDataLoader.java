package searchengine.service.loader;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlDataLoader {

    public String parse(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000).get();

            return doc.body().text();
        } catch (Throwable e) {
            throw new UrlDataLoadException("Error load data for url: " + url, e);
        }
    }



}
