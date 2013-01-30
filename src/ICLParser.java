import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 14.01.13 14:24
 */
public class ICLParser implements SiteParser{
    static final SimpleDateFormat frm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    
    public String contestsPage() {
        return "http://www.icl.ru/turnir/contest.php";
    }

    public String mainPage() {
        return "www.icl.ru/turnir";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "windows-1251"); if(s==null) return contests;
        try{
            int i, j, k = s.indexOf("<table>"), l = s.indexOf("</table>", k);
            for(;;){
                k = s.indexOf("<tr class=\"active\">", k+1);
                if(k<0 || k>l) break;
                Contest c = new Contest();
                c.mainPage = mainPage();
                i = s.indexOf("</a>", k);
                c.title = Utils.trim(s.substring(s.lastIndexOf(">",i-4)+1,i));
                if(c.title.equalsIgnoreCase("архив задач")) continue;
                i = s.indexOf("<td>",i+1);
                c.startDate.setTime(frm.parse(s.substring(i+4,s.indexOf("</td>",i))+" MSK"));
                i = s.indexOf("<td>",i+1);
                c.endDate.setTime(frm.parse(s.substring(i+4,s.indexOf("</td>",i))+" MSK"));
                contests.add(c);
            }
        }catch (ParseException e){
            e.printStackTrace();
        }
        return contests;
    }
}
