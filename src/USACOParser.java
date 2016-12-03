import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by demich on 24.05.15.
 */
public class USACOParser extends SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM HH:mm Z", Locale.ENGLISH);

    @Override
    public String contestsPage() {
        return "http://usaco.org/";
    }

    @Override
    public String mainPage() {
        return "http://usaco.org/";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;

        try{
            int i = s.lastIndexOf("<div class=\"panel\">");
            int y = Integer.parseInt(Utils.trim(s.substring(s.indexOf("<h2>",i)+4,s.indexOf("</h2>",i))).split("-")[0]), m = -1, m2;
            String str = s.substring(s.indexOf("</h2>",i+1)+5,s.indexOf("</div>",i+1));
            String sp[] = str.split("<br>");
            for(String z : sp){
                str = Utils.trim(z);
                if(str.length()==0) continue;
                i = str.indexOf(":");
                Contest c = new Contest();
                c.title = str.substring(i+2);
                c.mainPage = mainPage();
                c.contestPage = "http://usaco.org/index.php?page=viewcontest";
                c.deadLine = Utils.timeConsts.WEEK;
                String t = str.substring(0,i);
                String tp[] = t.split("[ -]");
                t = tp[1] + " " + tp[0] + " 00:00 -1200";
                try{
                    c.startDate.setTime(dateFormat.parse(t));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                m2 = c.startDate.get(Calendar.MONTH);
                if(m2<m) ++y; m = m2;
                c.startDate.set(Calendar.YEAR, y);
                t = (tp.length==3 ? tp[2] + " " + tp[0] : tp[3] + " " + tp[2]) + " 24:00 -1200";
                try{
                    c.endDate.setTime(dateFormat.parse(t));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                m2 = c.endDate.get(Calendar.MONTH);
                if(m2<m) ++y; m = m2;
                c.endDate.set(Calendar.YEAR, y);
                c.icon = getIcon();
                contests.add(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }
}
