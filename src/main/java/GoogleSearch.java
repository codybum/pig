import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * Created by vcbumg2 on 9/3/17.
 */
public class GoogleSearch {

    public String google = "http://www.google.com/search?q=";
    //public String search = "stackoverflow";
    public String charset = "UTF-8";
    public String userAgent = "ExampleBot 1.0 (+http://example.com/bot)"; // Change this to your company's name and bot homepage!


    public GoogleSearch() {


    }

    public String searchPerson(String search) {
        String returnUrl = null;
        try {


            Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)).userAgent(userAgent).get().select(".g>.r>a");

            for (Element link : links) {
                String title = link.text();
                String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
                url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

                if (!url.startsWith("http")) {
                    continue; // Ads/news/etc.
                }

                //System.out.println("Title: " + title);
                //System.out.println("URL: " + url);
                if(url.contains("wikipedia.org")) {
                    returnUrl = url;
                }
            }

        } catch(Exception ex) {
            System.out.println("searchPerson() " + ex.getMessage());
        }

        return returnUrl;
    }

}
