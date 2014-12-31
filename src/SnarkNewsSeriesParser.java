import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by demich on 8/14/14.
 */
public class SnarkNewsSeriesParser implements SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy, HH:mm z");

    @Override
    public String contestsPage() {
        return "http://contest.yandex.ru/"+getActual()+"/schedule/?lang=en";
    }

    @Override
    public String mainPage() {
        return "contest.yandex.ru/"+getActual();
    }

    public String getActual(){
        return "snws2015";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;
        try{
            int i = 0, j;
            String t;
            for(;;){
                i = s.indexOf("<tr>", i+1);
                if(i<0) break;
                i = s.indexOf("<td>", i+1);
                j = s.indexOf("</td>", i+1);
                Contest c = new Contest();
                c.mainPage = mainPage();
                if(s.indexOf("<a href", i)<j){
                    c.contestPage = "contest.yandex.ru" + s.substring(s.indexOf("\"",i)+1, s.indexOf("\">",i));
                    i+=4;
                }
                c.title = getActual().toUpperCase() + " " + s.substring(s.indexOf(">",i)+1, s.indexOf("<",i+1));
                i = s.indexOf("<td>", i);
                i = s.indexOf("\">",i+1);
                t = "";
                for(String str : s.substring(i+2, s.indexOf("<",i+1)).split(" ")){
                    if(Utils.month.containsKey(str)) t+=Utils.month.get(str);
                    else t+=str;
                    t+=" ";
                }
                t+=" MSK";
                c.startDate.setTime(dateFormat.parse(t));
                i = s.indexOf("<td>", i);
                i = s.indexOf("\">",i+1);
                t = "";
                for(String str : s.substring(i+2, s.indexOf("<",i+1)).split(" ")){
                    if(Utils.month.containsKey(str)) t+=Utils.month.get(str);
                    else t+=str;
                    t+=" ";
                }
                t+=" MSK";
                c.endDate.setTime(dateFormat.parse(t));
                contests.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contests;
    }
}
