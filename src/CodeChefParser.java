import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 14.01.13 18:30
 */
public class CodeChefParser implements SiteParser {
    static final SimpleDateFormat frm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    
    public String contestsPage() {
        return "http://www.codechef.com/contests/";
    }

    public String mainPage() {
        return "www.codechef.com";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        try{
            int i, j, k, end;
            k = s.indexOf("<h3>");
            end = s.indexOf("Past",k);
            j = k;
            for(;;){
                j = s.indexOf("<tr >", j+1);
                if(j<0 || j>end) break;
                Contest c = new Contest();
                c.source = mainPage();
                i = s.indexOf("<td", j+1);
                i = s.indexOf("<td", i+1);
                c.tittle = s.substring(s.indexOf("\">",i)+2, s.indexOf("</a",i));
                i = s.indexOf("<td", i+1);
                c.startDate.setTime(frm.parse(Utils.trim(s.substring(s.indexOf(">", i) + 1, s.indexOf("</", i))) + " India Standard Time"));
                i = s.indexOf("<td", i+1);
                c.endDate.setTime(frm.parse(Utils.trim(s.substring(s.indexOf(">",i)+1, s.indexOf("</",i)))+" India Standard Time"));
                contests.add(c);
                j = i;
            }
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return contests;
    }
}
