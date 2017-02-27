import javax.rmi.CORBA.Util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by demich on 26.02.17.
 */
public class AtCoderParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm Z", Locale.ENGLISH);
    static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH);

    @Override
    public String contestsPage() {
        return "https://atcoder.jp/contest";
    }

    @Override
    public String mainPage() {
        return "https://atcoder.jp/";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;

        try{
            int i = s.indexOf("<h3>Active Contests</h3>");
            int k = s.indexOf("</div>", s.indexOf("</div>",i)+1);
            i = s.indexOf("<tr>", i);
            for(;;){
                i = s.indexOf("<td class=\"text-center\">", i+1);
                if(i==-1 || i>k) break;
                int r = s.indexOf("</a", i+1);
                int l = s.lastIndexOf(">", r);
                Contest c = new Contest();
                try {
                    c.startDate.setTime(dateFormat.parse(s.substring(l + 1, r) + " JST"));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                i = s.indexOf("<td>", i+1);
                l = s.indexOf("\">", i);
                c.title = s.substring(l+2, s.indexOf("</a",l));
                c.contestPage = s.substring(s.lastIndexOf("href=",l)+6,l);
                i = s.indexOf("<td class=\"text-center\">", i+1);
                String str = s.substring(s.indexOf(">",i)+1,s.indexOf("<",i+1));
                i = s.indexOf("<td class=\"text-center\">", i+1);
                if(str.compareTo("∞")==0) continue;
                try {
                    c.endDate.setTime(timeFormat.parse(str));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                int hour = c.endDate.get(Calendar.HOUR_OF_DAY), minutes = c.endDate.get(Calendar.MINUTE);
                c.endDate.setTime(c.startDate.getTime());
                c.endDate.set(Calendar.HOUR_OF_DAY, hour+c.startDate.get(Calendar.HOUR_OF_DAY));
                c.endDate.set(Calendar.MINUTE, minutes+c.startDate.get(Calendar.MINUTE));
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
