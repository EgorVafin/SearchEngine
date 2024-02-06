package searchengine.utils;

public class UrlUtils {

    public static String removeDomainFromUrl(String url, String domain) {
        if (domain.endsWith("/")) {
            domain = StringUtils.removeLastChar(domain);
        }
        if (url.startsWith(domain)) {
            String substr = url.substring(domain.length());
            return substr.isEmpty() ? "/" : substr;
        }
        return url;
    }
}
