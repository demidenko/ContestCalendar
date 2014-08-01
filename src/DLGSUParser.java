import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by demich on 8/1/14.
 */
public class DLGSUParser implements SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss Z");

    @Override
    public String contestsPage() {
        return "http://dl.gsu.by/LC.jsp?Type=-1&lng=ru";
    }

    @Override
    public String mainPage() {
        return "dl.gsu.by";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "windows-1251"); if(s==null) return contests;
        try{
            int i, j, k = s.indexOf("class=\"news\""), end = s.indexOf("colspan",k);

            j = s.indexOf("</tr>", k);
            for(;;){
                Contest c = new Contest();
                c.mainPage = mainPage();
                c.contestPage = "http://dl.gsu.by/desk.asp";
                i = s.indexOf("<tr>", j);
                j = s.indexOf("</tr>", i);
                if(j>end) break;
                k = s.indexOf("<td",i);
                c.title = Utils.trim(s.substring(k + 4, s.indexOf("</td>", k)));
                k = s.indexOf("<td", k + 1);
                k = s.indexOf("<td",k+1);
                c.startDate.setTime(dateFormat.parse(Utils.trim(s.substring(s.indexOf(">", k) + 1, s.indexOf("</td>", k)) + " GMT+03:00")));
                k = s.indexOf("<td", k + 1);
                c.endDate.setTime(dateFormat.parse(Utils.trim(s.substring(s.indexOf(">", k) + 1, s.indexOf("</td>", k)) + " GMT+03:00")));
                contests.add(c);
            }
        }catch (ParseException e){
            e.printStackTrace();
        }
        return contests;
    }
}
