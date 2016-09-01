import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by demich on 17.01.16.
 */
public class ACMSFNEERCParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy hh:mm z", Locale.ENGLISH);

    @Override
    public String contestsPage() {
        return "http://neerc.ifmo.ru/information/";
    }

    @Override
    public String mainPage() {
        return "http://neerc.ifmo.ru/information/";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;

        try{
            Contest c = new Contest();
            int i = s.indexOf("will take");
            c.title = s.substring(s.lastIndexOf(">",i)+1, i-1);
            i = s.indexOf(" on ", i);
            String t = s.substring(i+4, s.indexOf(".",i));
            t = t.replace("-"," ");
            t = t.replaceAll("[^a-zA-Z0-9 ]","");
            String sp[] = t.split(" ");
            try{
                c.startDate.setTime(dateFormat.parse(sp[0]+" "+sp[1]+" "+sp[3]+" 00:00 MSK"));
                c.endDate.setTime(dateFormat.parse(sp[0]+" "+sp[2]+" "+sp[3]+" 24:00 MSK"));
            }catch (ParseException e){
                e.printStackTrace();
            }
            c.icon = getIcon();
            c.mainPage = mainPage();
            c.contestPage = mainPage();
            c.deadLine = Utils.timeConsts.YEAR;
            contests.add(c);
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }
}
