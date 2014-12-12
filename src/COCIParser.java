import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by demich on 12/12/14.
 */
public class COCIParser implements SiteParser {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.HH:mm z");

    @Override
    public String contestsPage() {
        return "http://hsin.hr/coci/";
    }

    @Override
    public String mainPage() {
        return "hsin.hr/coci";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        try{
            int i, k = 0;
            for(;;){
                k = s.indexOf("timeanddate", k+1);
                if(k<0) break;
                Contest c = new Contest();
                i = s.lastIndexOf("naslov", k);
                c.title = s.substring(i + 8, s.indexOf("</", i));
                c.contestPage = mainPage();
                c.mainPage = mainPage();
                c.startDate.setTime(dateFormat.parse(s.substring(s.indexOf("\">",k)+2,s.indexOf("</a>",k)).replace("<br />", "")));
                c.endDate.setTime(c.startDate.getTime());
                c.endDate.set(Calendar.HOUR, c.startDate.get(Calendar.HOUR) + 3);
                c.deadLine = Utils.timeConsts.DAY;
                contests.add(c);
            }
        }catch (ParseException e){
            e.printStackTrace();
        }

        return contests;
    }
}
