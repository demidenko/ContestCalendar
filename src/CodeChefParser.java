import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class CodeChefParser extends SiteParser {
    //static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy  HH:mm:ss Z");

    public String contestsPage() {
        return "http://www.codechef.com/contests/";
    }

    public String mainPage() {
        return "http://www.codechef.com";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;

        try{
            int i, j, k, end;
            k = s.indexOf("<h3>");
            end = s.indexOf("<h3>Past",k);
            end = s.indexOf("</div>", end);
            j = k;
            for(;;){
                j = s.indexOf("<tr", j+1);
                if(j<0 || j>end) break;
                Contest c = new Contest();
                c.mainPage = mainPage();
                i = s.indexOf("<td", j+1);
                i = s.indexOf("<td", i+1);
                c.contestPage = "http://www.codechef.com" + s.substring(s.indexOf("href=\"",i)+6, s.indexOf("\">",i));
                c.title = s.substring(s.indexOf("\">",i)+2, s.indexOf("</a",i));
                i = s.indexOf("<td", i+1);
                try{
                    String t = s.substring(s.indexOf(">", i) + 1, s.indexOf("</", i));
                    c.startDate.setTime(dateFormat.parse(Utils.trim(Utils.trimTags(Utils.replaceMonth(t.toLowerCase()))) + " India Standard Time"));
                    i = s.indexOf("<td", i+1);
                    t = s.substring(s.indexOf(">", i) + 1, s.indexOf("</", i));
                    c.endDate.setTime(dateFormat.parse(Utils.trim(Utils.trimTags(Utils.replaceMonth(t.toLowerCase()))) + " India Standard Time"));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                c.icon = getIcon();
                contests.add(c);
                j = i;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contests;
    }
}
