import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class IPSCParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMMM yyyy, HH:mm z", Locale.ENGLISH);
    
    public String contestsPage() {
        return "http://ipsc.ksp.sk/rules";
    }

    public String mainPage() {
        return "http://ipsc.ksp.sk/";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;
        
        try{
            int i,j;
            Contest c = new Contest();
            i = s.indexOf("<title>");
            String sp[] = s.substring(i+7, s.indexOf("</title>", i)).split(" ");
            c.title = sp[0]+" "+sp[1];
            i = s.indexOf("<h3>When is IPSC?</h3>");
            c.mainPage = mainPage();
            i = s.indexOf("\">", i+1);
            j = s.indexOf("</a>", i);
            try{
                c.startDate.setTime(dateFormat.parse(s.substring(i + 2, j)));
                i = s.indexOf("\">", j);
                j = s.indexOf("</a>", i);
                c.endDate.setTime(dateFormat.parse(s.substring(i + 2, j)));
            }catch (ParseException e){
                e.printStackTrace();
            }
            c.deadLine = Utils.timeConsts.YEAR;
            c.icon = getIcon();
            contests.add(c);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return contests;
    }
}
