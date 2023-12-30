package searchengine.services.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class HtmlJsoupParser {


    public Set<String> extractLinks(Document doc, String domain) {

        Set<String> childrenList = new HashSet<>();
        Elements elements = doc.select("a");

        for (Element element : elements) {
            String url = element.attr("href");

            if (isInternalLink(url, domain)) {
                if (url.startsWith("/")) {
                    url = domain + url;
                }
                childrenList.add(url);
            }
        }
        return childrenList;
    }

    public boolean isInternalLink(String url, String domain) {

        String urlCheck = url.trim();

        return urlCheck.startsWith("/") || urlCheck.startsWith(domain);
    }
}
