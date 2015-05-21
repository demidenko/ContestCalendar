import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by demich on 6/19/14.
 */
public class HackerRankParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    @Override
    public String contestsPage() {
        return "https://www.hackerrank.com/calendar/feed.rss";
    }

    @Override
    public String mainPage() {
        return "https://www.hackerrank.com/";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;
        s = s.replace("<url/>", "<url></url>");
        try{
            int i = 0, j;
            for(;;){
                i = s.indexOf("<item>", i+1);
                if(i==-1) break;
                i = s.indexOf("<title>", i);
                j = s.indexOf("</title>", i+1);
                Contest c = new Contest();
                c.icon = getIcon();
                c.mainPage = mainPage();
                c.title = Utils.replaceHTMLSymbols(Utils.trim(s.substring(i+7,j)));
                i = s.indexOf("<url>", j+1);
                j = s.indexOf("</url>", i+1);
                c.contestPage = Utils.trim(s.substring(i+5,j));
                if(!c.contestPage.contains(mainPage())) continue;
                i = s.indexOf("<startTime>", j+1);
                j = s.indexOf("</startTime>", i+1);
                c.startDate.setTime(dateFormat.parse(Utils.trim(s.substring(i+11,j))));
                i = s.indexOf("<endTime>", j+1);
                j = s.indexOf("</endTime>", i+1);
                c.endDate.setTime(dateFormat.parse(Utils.trim(s.substring(i+9,j))));
                boolean ok = c.endDate.getTimeInMillis()-c.startDate.getTimeInMillis()<Utils.timeConsts.DAY;
                ok|=c.title.contains("Week");
                ok|=c.title.contains("Infinitum");
                if(!ok) continue;
                contests.add(c);
            }
        }catch (ParseException e){
            e.printStackTrace();
        }
        return contests;
    }
}
