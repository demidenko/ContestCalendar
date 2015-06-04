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
        return "http://olympiads.ru/zaoch/"+y+"-"+((y+1)%100)+"/info.shtml";
    }

    @Override
    public String mainPage() {
        return "http://olympiads.ru/zaoch/";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "KOI8-R"); if(s==null) return contests;

        try{
            Contest c = new Contest();
            c.title = Utils.trim(Utils.trimTags(s.substring(s.indexOf("<h4"), s.indexOf("</h4>"))));
            c.deadLine = Utils.timeConsts.YEAR;
            c.contestPage = mainPage();
            c.mainPage = mainPage();
            int i = s.indexOf("<li>");
            String sp[] = Utils.trimTags(s.substring(s.indexOf("<b",i+1),s.indexOf("</b>",i+1))).toLowerCase().split(" ");
            try{
                c.startDate.setTime(dateFormat.parse(sp[1] + " " + Utils.month.get(sp[2]) + " " + sp[3] + " 00:00 MSK"));
                c.endDate.setTime(dateFormat.parse(sp[6] + " " + Utils.month.get(sp[7]) + " " + sp[8] + " 23:59 MSK"));
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
