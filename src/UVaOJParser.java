import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class UVaOJParser extends SiteParser {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    @Override
    public String contestsPage() {
        return "https://uva.onlinejudge.org/index.php?option=com_onlinejudge&Itemid=12";
    }

    @Override
    public String mainPage() {
        return "https://uva.onlinejudge.org";
    }


    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        try{
            int i, j, k=s.indexOf("sectiontableheader"), l=s.indexOf("sectiontableheader",k+1);
            i = 0;
            for(;;){
                Contest c = new Contest();
                i = s.indexOf("sectiontableentry",i+1);
                if(i<0 || i>l) break;
                i = s.indexOf("<td", i+1);
                i = s.indexOf("<td", i+1);
                i = s.indexOf("<td", i+1);
                j = s.indexOf("a href=\"",i);
                c.contestPage = "http://" + mainPage() + "/" + s.substring(j+8, s.indexOf("\">",j+1));
                c.mainPage = mainPage();
                c.title = s.substring(s.indexOf("\">", j) + 2, s.indexOf("</a>", j));
                i = s.indexOf("<td", i + 1);
                try{
                    c.startDate.setTime(dateFormat.parse(s.substring(s.indexOf(">",i)+1,s.indexOf("</td>",i))+" UTC"));
                    i = s.indexOf("<td", i+1);
                    c.endDate.setTime(dateFormat.parse(s.substring(s.indexOf(">",i)+1,s.indexOf("</td>",i))+" UTC"));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                c.deadLine = Utils.timeConsts.DAY;
                c.icon = getIcon();
                contests.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contests;
    }
}
