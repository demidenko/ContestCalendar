import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 12.01.13 20:53
 */
public class CodeForcesParser implements SiteParser{
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm z");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public String url() {
        return "http://codeforces.ru/contests";
    }

    public ArrayList<Contest> parse(){
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(url(), "UTF-8"); if(s==null) return contests;
        
        try{
            int i, j, k = s.indexOf("data-contestId="), end = s.indexOf("class=\"contests-table\"");
            String str;
            Calendar d = Calendar.getInstance();
            while(k<end){
                Contest c = new Contest();
                c.source = "codeforces.ru";
                i = s.indexOf("<td>", k);
                j = s.indexOf("</td>", i);
                str = s.substring(i + 4, j);
                while((i=str.indexOf("<br"))>=0) str = str.substring(0,i);
                c.tittle = Utils.trim(str);
                i = s.indexOf(">", s.indexOf(">", j + 5) + 1);
                j = s.indexOf("<", i);
                str = Utils.trim(s.substring(i + 1, j));
                c.startDate.setTime(dateFormat.parse(str+" MSK"));
                i = s.indexOf("<td>", j);
                j = s.indexOf("</td>", i);
                str = Utils.trim(s.substring(i + 4, j));
                d.setTime(timeFormat.parse(str));
                c.endDate = Utils.sum(c.startDate, d);
                contests.add(c);
                k = s.indexOf("data-contestId=",k+1);
            }
        } catch (ParseException e) {
            e.printStackTrace(); 
        }
        
        return contests;
    }
}
