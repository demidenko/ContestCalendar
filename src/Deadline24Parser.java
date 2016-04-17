import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by demich on 14.01.16.
 */
public class Deadline24Parser extends SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    public String contestsPage() {
        return "https://deadline24.pl/deadline24.ics";
    }

    @Override
    public String mainPage() {
        return "https://deadline24.pl/";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;



        try{
            int i, j = 0, k;
            String t;
            i = s.indexOf("TIMEZONE");
            if(i!=-1){
                t = s.substring(s.indexOf(":",i)+1,s.indexOf("\r",i));
                dateFormat.setTimeZone(TimeZone.getTimeZone(t));
            }
            for(;;){
                i = s.indexOf("BEGIN:VEVENT", j);
                if(i<0) break;
                j = s.indexOf("END:VEVENT", i);
                Contest c = new Contest();
                k = s.indexOf("SUMMARY", i);
                c.mainPage = mainPage();
                c.deadLine = Utils.timeConsts.YEAR;
                c.title = s.substring(s.indexOf(":", k) + 1, s.indexOf("\r", k));
                k = s.indexOf("DTSTART", i);
                t = s.substring(s.indexOf(":", k) + 1, s.indexOf("\r", k));
                if(t.indexOf("T")==-1) t+="000000"; t = t.replaceAll("[A-Z]", "");
                try{
                    c.startDate.setTime(dateFormat.parse(t));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                k = s.indexOf("DTEND", i);
                t = s.substring(s.indexOf(":", k) + 1, s.indexOf("\r", k));
                if(t.indexOf("T")==-1) t+="240000"; t = t.replaceAll("[A-Z]", "");
                try{
                    c.endDate.setTime(dateFormat.parse(t));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                c.icon = getIcon();
                contests.add(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }
}
