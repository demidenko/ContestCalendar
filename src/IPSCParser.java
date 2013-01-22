import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * 19.01.13 23:52
 */
public class IPSCParser implements SiteParser {
    static final SimpleDateFormat frm = new SimpleDateFormat("d MMMMM yyyy, HH:mm z", Locale.ENGLISH);
    
    public String url() {
        return "http://ipsc.ksp.sk/rules";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(url(), "UTF-8"); if(s==null) return contests;
        
        try{
            int i,j;
            Contest c = new Contest();
            i = s.indexOf("<title>");
            String sp[] = s.substring(i+7, s.indexOf("</title>", i)).split(" ");
            c.tittle = sp[0]+" "+sp[1];
            i = s.indexOf("<h3>When is IPSC?</h3>");
            c.source = "ipsc.ksp.sk";
            i = s.indexOf("\">", i+1);
            j = s.indexOf("</a>", i);
            c.startDate.setTime(frm.parse(s.substring(i+2, j)));
            i = s.indexOf("\">", j);
            j = s.indexOf("</a>", i);
            c.endDate.setTime(frm.parse(s.substring(i+2, j)));
            contests.add(c);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return contests;
    }
}