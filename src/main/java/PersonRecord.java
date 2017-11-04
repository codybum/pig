import java.util.ArrayList;
import java.util.List;

public class PersonRecord {

	  public int id;
	  public String name;
	  public List<Integer> students;
      public List<Integer> advisors;
      public String wikiUrl;
      public String wikiImage;
      public String birth;
      public String death;
      private List<String[]> degreeLists;
      public boolean isProcessed = false;

    public PersonRecord(int id, String name)
    {
        this.id = id;
	    this.name = name;
	    students = new ArrayList<Integer>();
	    advisors = new ArrayList<Integer>();
	    degreeLists = new ArrayList<String[]>();
    }

    public void addDegree(String[] degree) {

        synchronized(degreeLists) {
            if(!degreeLists.contains(degree)) {
                degreeLists.add(degree);
            }
        }

    }

    public List<String[]> getDegreeLists() {
        return degreeLists;
    }

}