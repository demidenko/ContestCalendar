import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ICLParser extends SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    
    public String contestsPage() {
        return "http://www.icl.ru/turnir/contest.php";
    }

    public String mainPage() {
        return "http://icl.ru/turnir/";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "windows-1251"); if(s==null) return contests;
        try{
            String str;
            int i, j, k = s.indexOf("<table>"), l = s.indexOf("</table>", k);
            k = s.indexOf("<tr", k);
            for(;;){
                k = s.indexOf("<tr", k+1);
                if(k<0 || k>l) break;
                Contest c = new Contest();
                c.mainPage = mainPage();
                i = s.indexOf("<td",k+1);
                i = s.indexOf("<td",i+1);
                j = s.indexOf("</td>",i);
                str = Utils.trimTags(s.substring(s.indexOf(">",i)+1,j));
                c.title = Utils.trim(str);
                i = s.indexOf("<td",i+1);
                try{
                    c.startDate.setTime(dateFormat.parse(s.substring(i + 4, s.indexOf("</td>", i)) + " MSK"));
                    i = s.indexOf("<td",i+1);
                    c.endDate.setTime(dateFormat.parse(s.substring(i + 4, s.indexOf("</td>", i)) + " MSK"));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                if(c.title.equalsIgnoreCase("архив задач")) continue;
                c.deadLine = Utils.timeConsts.YEAR;
                c.icon = getIcon();
                contests.add(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return contests;
    }
}
