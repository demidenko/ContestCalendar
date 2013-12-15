import java.util.ArrayList;


public interface SiteParser{
    public String contestsPage();
    
    public String mainPage();
    
    public ArrayList<Contest> parse();
}
