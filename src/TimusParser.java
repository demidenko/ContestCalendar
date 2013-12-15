import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: demich
 * Date: 09.06.13
 * Time: 21:44
 * To change this template use File | Settings | File Templates.
 */
public class TimusParser implements SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy HH:mm Z");

    @Override
    public String contestsPage() {
        return "http://acm.timus.ru/schedule.aspx?locale=ru";
    }

    @Override
    public String mainPage() {
        return "acm.timus.ru";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        try{
            int i=0, j, k;
            String str, t;
            for(;;){
                i=s.indexOf("<LI>", i+1);
                if(i==-1) break;
                j=s.indexOf("<A HREF", i+1);
                Contest c = new Contest();
                c.title = s.substring(s.indexOf("\">", j+1)+2, s.indexOf("</A>", j+1));
                c.deadLine = Utils.timeConsts.WEEK;
                c.mainPage = mainPage();
                str=s.substring(j + 9, s.indexOf("\">", j + 1));
                str = "http://" + mainPage() + "/" + str + "&locale=ru";
                c.contestPage = str;
                t=Utils.URLToString(str, "UTF-8");
                k=t.indexOf("<B>");
                str = Utils.replaceMonth(t.substring(k+3, t.indexOf("</B>", k))) + " YEKT";
                c.startDate.setTime(dateFormat.parse(str));
                k=t.indexOf("<B>", k+1);
                str = Utils.replaceMonth(t.substring(k + 3, t.indexOf("</B>", k))) + " YEKT";
                c.endDate.setTime(dateFormat.parse(str));
                contests.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contests;
    }
}
