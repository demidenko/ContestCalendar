import java.util.ArrayList;

/**
 * 12.01.13 20:42
 */
public interface SiteParser {
    
    public String url();
    
    public ArrayList<Contest> parse();
}
