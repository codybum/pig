import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.PrintWriter;

public class QueryRunner implements Runnable {

    private int id;
    private String BaseURL = "https://www.genealogy.math.ndsu.nodak.edu/id.php?id=";

    public QueryRunner(int id)
	{
        this.id = id;
	}


    public Document getDocument(int id) throws Exception {
        Document doc = null;
        try {
            String url = BaseURL + String.valueOf(id);
            String fileName = "data/" + String.valueOf(id) + ".html";

            File f = new File(fileName);
            if (!f.exists()) {
                doc = Jsoup.connect(url).get();
                FileUtils.writeStringToFile(f, doc.outerHtml(), "UTF-8");
            } else {
                System.out.println("**CACHED ID: " + id + " **");
                //doc = Jsoup.connect(f.toURL().toString()).get();
                doc = Jsoup.parse(f,"UTF-8");
            }

        } catch(Exception ex) {
            System.out.println("getDocument Error : " + ex.toString());
            ex.printStackTrace();
            System.exit(0);
        }

        return doc;

    }

	public void run() {


        try {

            boolean isProcessed = false;

            synchronized(boom.pm) {
                if (boom.pm.contains(id)) {
                    if (boom.pm.get(id).isProcessed) {
                        isProcessed = true;
                    }
                }
            }
            if (!isProcessed) {
                boom.jobsize++;

           //     String url = BaseURL + String.valueOf(id);

           // Document doc = Jsoup.connect(url).get();

                Document doc = getDocument(id);

            String name = null;

            Elements h2 = doc.select("h2");
            for (Element t : h2) {
                //System.out.println("Person:[" + t.html() + "]");
                name = t.html();
            }

            if (name == null) {
                System.out.println("Name = null ");
                System.exit(0);
            }

                System.out.println("Launching Query for Student: " + name);

                Elements div = doc.select("div");

                for (Element t : div) {

                    boolean hasSpan = false;
                    boolean hasImg = false;

                    if(t.id().length() == 0) {
                        //processDiv(t);
                        for (Element el : t.getAllElements()) {
                            if (el.tag().toString().equals("span")) {
                                hasSpan = true;
                            }
                            if (el.tag().toString().equals("img")) {
                                hasImg = true;
                            }
                        }
                        if(hasImg && hasSpan) {
                            String[] degreeArray = processDiv(t);
                            if(degreeArray != null) {

                                synchronized(boom.pm) {

                                    if (!boom.pm.containsKey(id)) {
                                        PersonRecord pr = new PersonRecord(id, name);
                                        pr.degreeLists.add(degreeArray);
                                        boom.pm.put(id, pr);
                                        System.out.println("New student added " + id);
                                        //System.out.println(pr.name);
                                    } else {
                                        if(!boom.pm.get(id).degreeLists.contains(degreeArray)) {
                                            boom.pm.get(id).degreeLists.add(degreeArray);
                                        }
                                    }
                                }
                                System.out.println("Degree: " + degreeArray[0] + " School: " + degreeArray[1]);
                            }
                        }
                    }
                }

                Elements pp = doc.select("p");
                processPP(pp, name);
                synchronized(boom.pm) {
                    boom.pm.get(id).isProcessed = true;
                }
                boom.jobsize--;
            }

        }
        catch(Exception ex) {
	        ex.printStackTrace();
	        System.exit(0);
        }

    }

    private String[] processDiv(Element div) {
        String[] degreeArray = null;
        String school = null;
        String country = null;

        //System.out.println(div.html());

        try {
            degreeArray = new String[2];
            int state = 0;
            String degree = null;

            //Elements sp = div.get(8).getAllElements();
            Elements sp = div.getAllElements();
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
                        //System.out.println("school: " + school);
                        state = 2;
                    }
                }

                if(state == 2) {
                    //System.out.println(el.tag().toString() + " " + el.text() + " " + el.html());
                    if (el.tag().toString().equals("img")) {
                        country = el.attr("src");
                        //System.out.println("country: " + country);
                        state = 0;
                    }
                }

            }

            //System.out.println("school: " + school + " country:" + country);

        } catch(Exception ex) {
            System.out.println("processDiv() Error : " + ex.toString());
            ex.printStackTrace();
            System.exit(0);
        }

        if((school != null) && (country != null)) {
            degreeArray[0] = school;
            degreeArray[1] = country;
        }
        return degreeArray;
    }


    private void processPP(Elements pp, String name) {

        //Elements pp = doc.select("p");
        //Elements pp = null;
        //String name = null;
        //System.out.println("count " + pp.text());
        //System.out.println("Elements: " + pp.size());

        for (Element t : pp) {

            //System.out.println(t.ownText());
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

                        //System.out.println("aId:" + linkH[1]);

                        synchronized(boom.pm) {

                            if (!boom.pm.containsKey(id)) {
                                PersonRecord pr = new PersonRecord(id, name);
                                boom.pm.put(id, pr);
                                System.out.println("New student added " + id + " name:" + name);
                                //System.out.println(pr.name);
                            }

                            if (!boom.pm.containsKey(aId)) {
                                PersonRecord pr = new PersonRecord(aId, aName);
                                boom.pm.put(aId, pr);
                                System.out.println("New advisor added " + aId + " name:" + name);
                                //System.out.println(pr.name);
                            }

                            if (!boom.pm.get(id).advisors.contains(aId)) {
                                boom.pm.get(id).advisors.add(aId);
                                System.out.println("Student: " + boom.pm.get(id).name + " added advisor:" + boom.pm.get(aId).name);
                            }
                            if (!boom.pm.get(aId).students.contains(id)) {
                                boom.pm.get(aId).students.add(id);
                                System.out.println("Advisor: " + boom.pm.get(aId).name + " added Student:" + boom.pm.get(id).name);
                            }
                            try {
                                //Thread.sleep((long) (Math.random() * 500));
                            } catch (Exception ex) {

                            }

                        }
                        synchronized(boom.qm) {
                            if (!boom.qm.contains(aId)) {
                                boom.qm.put(aId, System.currentTimeMillis());
                                boom.executor.submit(new QueryRunner(aId));
                            }
                        }

                    }
                }
            }
        }

    }
}