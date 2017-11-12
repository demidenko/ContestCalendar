import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by demich on 22.05.15.
 */
public class ZaochParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy HH:mm Z");

    @Override
    public String contestsPage() {
        Calendar c = Calendar.getInstance();
        int m = c.get(Calendar.MONTH);
        int y = c.get(Calendar.YEAR);
        if(m<9) --y;
        return "http://olympiads.ru/zaoch/"+y+"-"+((y+1)%100)+"/zaoch_rules.shtml";
    }

    @Override
    public String mainPage() {
        return "http://olympiads.ru/zaoch/";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "KOI8-R"); if(s==null) return null;

        try{
            Contest c = new Contest();
            int i = s.indexOf("<h4");
            c.title = Utils.trim(Utils.trimTags(s.substring(i, s.indexOf("</h4>",i+1))));
            c.deadLine = Utils.timeConsts.YEAR;
            c.contestPage = mainPage();
            c.mainPage = mainPage();
            i = s.indexOf("<p>", i+1);
            String sp[] = Utils.trimTags(Utils.trim(s.substring(i,s.indexOf("<p>",i+1)))).toLowerCase().split(" ");
            i = sp.length;
            try{
                c.startDate.setTime(dateFormat.parse(sp[i-9] + " " + Utils.month.get(sp[i-8]) + " " + sp[i-7] + " 00:00 MSK"));
                c.endDate.setTime(dateFormat.parse(sp[i-4] + " " + Utils.month.get(sp[i-3]) + " " + sp[i-2] + " 23:59 MSK"));
            }catch (ParseException e){
                e.printStackTrace();
            }
            c.icon = getIcon();
            contests.add(c);
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }
}
