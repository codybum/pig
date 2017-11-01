import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QueryRunner2 implements Runnable {

    private int id;
    private String BaseURL = "https://www.genealogy.math.ndsu.nodak.edu/id.php?id=";

    public QueryRunner2(int id)
	{
        this.id = id;
	}

	public void run() {

        boom.jobsize++;

        try {

            if (!boom.pm.contains(id)) {

                String url = BaseURL + String.valueOf(id);

            Document doc = Jsoup.connect(url).get();


            String name = null;

            Elements h2 = doc.select("h2");
            for (Element t : h2) {
                //System.out.println("Person:[" + t.html() + "]");
                name = t.html();
            }

            if (name == null) {
                System.out.println("Name = null " + url);
                System.exit(0);
            }


                Elements div = doc.select("div");

                //System.out.println("8 " + div.get(8).html());

                int state = 0;
                String degree = null;
                String school = null;
                String country = null;

                Elements sp = div.get(8).getAllElements();
                for(Element el : sp) {

                    if(state == 0) {

                        if (el.tag().toString().equals("span")) {
                            degree = el.ownText();
                            state = 1;
                        }
                    }

                    if(state == 1) {
                        //System.out.println(el.text());
                        if (el.tag().toString().equals("span")) {
                            school = el.text();
                            System.out.println("school: " + school);
                            state = 2;
                        }
                    }

                    if(state == 2) {
                        //System.out.println(el.tag().toString() + " " + el.text() + " " + el.html());
                        if (el.tag().toString().equals("img")) {
                            country = el.attr("src");
                            System.out.println("country: " + country);
                            state = 0;
                        }
                    }

                    }

                    System.out.println("school: " + school + " country:" + country);

                //}

                    /*
                for (Element t : div) {
                    if(t.html().contains("Uniwersytet")) {
                        System.out.println("Person:[" + t.html() + "]");
                    }
*/


                    //name = t.html();
               // }

            /*
            <div style="line-height: 30px; text-align: center; margin-bottom: 1ex">
  <span style="margin-right: 0.5em">Ph.D. <span style="color:
  #006633; margin-left: 0.5em">Uniwersytet Warszawski</span> 1968</span>

<img src="img/flags/Poland.gif" alt="Poland" width="48" height="30" style="border: 0; vertical-align: middle" title="Poland" />
</div>
             */

            Elements pp = doc.select("p");

            //System.out.println("count " + pp.text());
            //System.out.println("Elements: " + pp.size());

            for (Element t : pp) {

                System.out.println(t.ownText());
                //System.out.println("Person:[" + pp.html() + "]");
                //if(t.ownText().equals("Advisor:")) {
                if ((t.ownText().contains("Advisor:")) || (t.ownText().contains("Advisor 1:")) || (t.ownText().contains("Advisor 2:")) || (t.ownText().contains("Advisor 3:")) || (t.ownText().contains("Advisor 4:"))) {

                    //System.out.println("text " + t.ownText());


                    String html = t.html();
                    Document doc2 = Jsoup.parse(html);
                    //Element link = doc2.select("a").first();
                    Elements links = doc2.select("a");

                    if (links.size() > 3) {
                        System.out.println("**** OVER LIMIT **** " + links.size());
                    }

                    for (Element link : links) {

                        String linkText = link.text(); // "example""
                        //System.out.println(linkText);
                        String aName = linkText;

                        if (!aName.startsWith("Unknown")) {

                            String linkHref = link.attr("href"); // "http://example.com/"
                            String[] linkH = linkHref.split("=");

                            //advisorod
                            int aId = Integer.parseInt(linkH[1]);

                            System.out.println("aId:" + linkH[1]);

                            if (!boom.pm.containsKey(id)) {
                                PersonRecord pr = new PersonRecord(id, name);
                                boom.pm.put(id, pr);
                                System.out.println("New student added " + id);
                                //System.out.println(pr.name);
                            }

                            if (!boom.pm.containsKey(aId)) {
                                PersonRecord pr = new PersonRecord(aId, aName);
                                boom.pm.put(aId, pr);
                                System.out.println("New advisor added " + aId);
                                //System.out.println(pr.name);
                            }

                            if (!boom.pm.get(id).advisors.contains(aId)) {
                                boom.pm.get(id).advisors.add(aId);
                                System.out.println("Student id: " + id + " added advisor:" + aId);
                            }
                            if (!boom.pm.get(aId).students.contains(id)) {
                                boom.pm.get(aId).students.add(id);
                                System.out.println("Advisor id: " + aId + " added Student:" + id);
                            }

                            if(!boom.qm.contains(aId)) {
                                boom.qm.put(aId,System.currentTimeMillis());
                                //boom.executor.submit(new QueryRunner2(aId));
                            }
                            Thread.sleep((long) (Math.random() * 1000));

                        }
                    }
                }
            }
        }
        }
        catch(Exception ex) {
	        ex.printStackTrace();
        }

        boom.jobsize--;
    }
}