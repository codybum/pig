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



    public PersonRecord(int id, String name)
    {
        this.id = id;
	    this.name = name;
	    students = new ArrayList<Integer>();
	    advisors = new ArrayList<Integer>();
    }

}