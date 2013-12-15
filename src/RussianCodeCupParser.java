import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 13.01.13 20:43
 */
public class RussianCodeCupParser implements SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss Z");
    
    public String contestsPage() {
        return "http://russiancodecup.ru/ical";
    }

    @Override
    public String mainPage() {
        return "russiancodecup.ru";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        try{
            int i, j=0, k;
            for(;;){
                i = s.indexOf("BEGIN:VEVENT", j); if(i<0) break;
                j = s.indexOf("END:VEVENT", i);
                Contest c = new Contest();
                k = s.indexOf("SUMMARY:",i);
                c.title = "Russian Code Cup "+s.substring(s.indexOf(':',k)+1, s.indexOf("\r",k));
                k = s.indexOf("DTSTART",i);
                c.startDate.setTime(dateFormat.parse(s.substring(s.indexOf(':', k) + 1, s.indexOf("\r", k)).replace("T", "") + " MSK"));
                k = s.indexOf("DTEND",i);
                c.endDate.setTime(dateFormat.parse(s.substring(s.indexOf(':', k) + 1, s.indexOf("\r", k)).replace("T", "") + " MSK"));
                c.mainPage = mainPage();
                c.deadLine = Utils.timeConsts.YEAR;
                contests.add(c);
            }
        }catch (ParseException e){
            e.printStackTrace();
        }

        return contests;
    }
}
