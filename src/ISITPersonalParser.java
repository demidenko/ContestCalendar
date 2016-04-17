import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by demich on 11.04.16.
 */
public class ISITPersonalParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy HH:mm:ss z");

    @Override
    public String contestsPage() {
        String s = Utils.URLToString(mainPage(), "UTF-8");
        if(s==null) return null;
        int i = s.indexOf("Сезоны:");
        if(i<0) return null;
        return "http://ikit.sfu-kras.ru"+s.substring(s.indexOf("a href=\"", i)+8, s.indexOf("\">",i));
    }

    @Override
    public String mainPage() {
        return "http://ikit.sfu-kras.ru/lich_pervenstvo";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;

        try{
            int i = s.indexOf("content full");
            Contest c = new Contest();
            String sp[] = Utils.trim(Utils.trimTags(s.substring(s.indexOf("<b>",i),s.indexOf("</b>",i)))).split("[^0-9а-я]");
            c.title = "Личное первенство "+sp[3];
            try{
                c.startDate.setTime(dateFormat.parse(sp[0]+" "+Utils.month.get(sp[2])+" "+sp[3]+" 00:00:00 KRAT"));
                c.endDate.setTime(dateFormat.parse(sp[1]+" "+Utils.month.get(sp[2])+" "+sp[3]+" 24:00:00 KRAT"));
            }catch (ParseException e){
                e.printStackTrace();
                return null;
            }
            c.mainPage = mainPage();
            c.contestPage = contestsPage();
            c.deadLine = Utils.timeConsts.YEAR;
            c.icon = getIcon();
            contests.add(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contests;
    }
}
