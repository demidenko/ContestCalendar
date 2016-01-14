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
        return "http://www.e-olymp.com/ru/contests";
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
            int k = s.indexOf("eo-list"), i = k, ke = s.indexOf("eo-pagination");
            for(;;){
                if(k==-1 || k>ke) break;
                k = i = s.indexOf("href",k+1);
                Contest c = new Contest();
                c.contestPage = mainPage() + s.substring(i+6, s.indexOf("\"",i+6));
                c.title = Utils.trim(s.substring(s.indexOf(">",i+1)+1,s.indexOf("<span",i+1)));
                i = s.indexOf("<div", i+1);
                i = s.indexOf("<div", i+1);
                try{
                    String s1 = s.substring(s.indexOf(">",i+1)+1, s.indexOf("</div",i+1));
                    i = s.indexOf("<div", i+1);
                    String s2 = s.substring(s.indexOf(">",i+1)+1, s.indexOf("</div",i+1));
                    s1 = Utils.trimTags(s1);
                    s2 = Utils.trimTags(s2);
                    int j = s2.indexOf("-");
                    if(j!=-1){
                        String tmp = s1;
                        s1+=" "+s2.substring(0,j-1);
                        s2 = tmp+" "+s2.substring(j+2);
                    }
                    s1+=" EEST";
                    s2+=" EEST";
                    c.startDate.setTime(dateFormat.parse(s1));
                    c.endDate.setTime(dateFormat.parse(s2));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                c.mainPage = mainPage();
                c.icon = getIcon();
                Main.writeln(c);
                contests.add(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }
}
