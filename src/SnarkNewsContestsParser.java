import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 12.01.13 21:38
 */
public class SnarkNewsContestsParser implements SiteParser {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm z");
    
    public String url() {
        return "http://contests.snarknews.info/index.cgi?data=main/schedule";
    }

    public ArrayList<Contest> parse(){
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(url(), "windows-1251"); if(s==null) return contests;
        try{
            int i, j, k = s.indexOf("class=\"standings\""), l = s.indexOf("</table>", k);
            k = s.indexOf("<tr>", k+1);
            for(;;){
                k = s.indexOf("<tr>", k+1);
                if(k>l || k<0) break;
                Contest c = new Contest();
                c.source = "contests.snarknews.info";
                i = s.indexOf("<b>", k);
                c.tittle = Utils.trim(s.substring(i+3, s.indexOf("</b>",i+1)));
                if(c.tittle.length()==0) continue;
                i = s.indexOf("<b>", i+1);
                i = s.indexOf("<b>", i+1);
                i = s.indexOf("<b>", i+1);
                c.startDate.setTime(dateFormat.parse(Utils.trim(s.substring(i+3, s.indexOf("</b>",i+1)))+" MSK"));
                i = s.indexOf("<b>", i+1);
                c.endDate.setTime(dateFormat.parse(Utils.trim(s.substring(i+3, s.indexOf("</b>",i+1)))+" MSK"));
                contests.add(c);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return contests;
    }
}