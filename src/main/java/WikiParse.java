import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiParse {

    String userAgent = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";


    public WikiParse()
	{

	}

	public String parse(String url) {

        String imageUrl = null;
        try {


            String userAgent = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
            Document doc = Jsoup.connect(url).userAgent(userAgent).get();

            //Document doc = Jsoup.parse(url);
            String ogImage = null;
            Elements metaOgImage = doc.select("meta[property=og:image]");

            if (metaOgImage!=null) {
                String desc = metaOgImage.first().attr("content");
                imageUrl = desc;

            }

        }
        catch(Exception ex) {
	        //ex.printStackTrace();
            System.out.println("Error for " + url);
        }
        return imageUrl;
    }
}