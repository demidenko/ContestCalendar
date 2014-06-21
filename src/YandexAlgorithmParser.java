import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class YandexAlgorithmParser implements SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyy, HH:mm z", Locale.ENGLISH);

    @Override
    public String contestsPage() {
        return "http://algorithm.contest.yandex.ru/schedule/?lang=en";
    }

    @Override
    public String mainPage() {
        return "algorithm.contest.yandex.ru";
    }


    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        try{
            int i,l,k=s.indexOf("<h1>");
            String str;
            i=s.indexOf("<tr>",k+1);
            l=s.indexOf("</table>",i+1);
            for(;i<l;){
                i=s.indexOf("<tr>",i+1);
                if(i==-1 || i>l) break;
                Contest c = new Contest();
                i=s.indexOf("<td>",i+1);
                c.title = Utils.trimTags(s.substring(i+4, s.indexOf("</td",i+1)));
                i = s.indexOf("<td>", i+1);
                str = Utils.trimTags(s.substring(i+4, s.indexOf("</td",i+1))).replace("&nbsp;"," ")+" MSK";
                c.startDate.setTime(dateFormat.parse(str));
                i = s.indexOf("<td>", i+1);
                str = Utils.trimTags(s.substring(i+4, s.indexOf("</td",i+1))).replace("&nbsp;"," ")+" MSK";
                c.endDate.setTime(dateFormat.parse(str));
                i = s.indexOf("<td>", i+1);
                contests.add(c);
                c.mainPage = mainPage();
                c.deadLine = Utils.timeConsts.YEAR;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return contests;
    }
}
