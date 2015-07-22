import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by demich on 16.06.15.
 */
public class EOlympParser extends SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm Z");

    @Override
    public String contestsPage() {
        return "http://www.e-olymp.com/ru/competitions/";
    }

    @Override
    public String mainPage() {
        return "http://www.e-olymp.com";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        try{
            int k = s.indexOf("main-content"), i = k;
            for(;;){
                k = s.indexOf("\"list-group-item\"", i+1);
                if(k==-1) break;
                i = s.indexOf("href",k);
                Contest c = new Contest();
                c.contestPage = mainPage() + s.substring(i+6, s.indexOf("\"",i+6));
                i = s.indexOf("<h5", i+1);
                c.title = Utils.trim(s.substring(s.indexOf(">",i+1)+1,s.indexOf("</h5",i+1)));
                i = s.indexOf("<div ", i+1);
                try{
                    String str = Utils.trim(s.substring(s.indexOf(">", i + 1) + 1, s.indexOf("<br", i + 1))) + " EEST";
                    c.startDate.setTime(dateFormat.parse(str));
                    i = s.indexOf("<br",i+1);
                    str = Utils.trim(s.substring(s.indexOf(">", i + 1) + 1, s.indexOf("</div", i + 1))) + " EEST";
                    c.endDate.setTime(dateFormat.parse(str));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                c.mainPage = mainPage();
                c.icon = getIcon();
                contests.add(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }
}