import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by demich on 23.05.15.
 */
public class HackerEarthParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm a Z", Locale.ENGLISH);

    @Override
    public String contestsPage() {
        return "https://www.hackerearth.com/pagelets/challenges-upcoming-challenges/";
    }

    @Override
    public String mainPage() {
        return "https://www.hackerearth.com";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;
        s = s.replace("IST", "India Standard Time");

        try{
            int i = 0, j;
            String str, t, z;
            for(;;){
                i = s.indexOf("challenge-card standard-margin-bottom",i+1);
                if(i<0) break;
                Contest c = new Contest();
                c.mainPage = mainPage();
                i = s.indexOf("href=", i + 1);
                j = s.indexOf("\"", i + 6);
                c.contestPage = mainPage() + s.substring(i+6, j);
                i = s.indexOf("<span", i + 1);
                c.title = Utils.replaceHTMLSymbols(Utils.trimTags(s.substring(i, s.indexOf("</span>", i + 1))));
                i = s.indexOf("Opens at", i + 1);
                i = s.indexOf("<div", i + 1);
                str = Utils.trimTags(s.substring(i, s.indexOf("</div>", i + 1)));
                i = s.indexOf("<div", i + 1);
                str+=" "+Utils.trimTags(s.substring(i,s.indexOf("</div>",i+1)));
                i = s.indexOf("<div",i+1);
                i = s.indexOf("<div",i+1);
                z = Utils.trimTags(s.substring(i, s.indexOf("</div>", i + 1)));
                i = s.indexOf("<div", i + 1);
                i = s.indexOf("<div", i + 1);
                t = Utils.trimTags(s.substring(i, s.indexOf("</div>", i + 1)));
                c.startDate.setTime(dateFormat.parse(str + " " + t.substring(0,t.indexOf("-")-1) + z));
                i = s.indexOf("Closes on", i+1);
                i = s.indexOf("<div",i+1);
                str = Utils.trimTags(s.substring(i, s.indexOf("</div>", i + 1)));
                i = s.indexOf("<div", i + 1);
                str+=" "+Utils.trimTags(s.substring(i,s.indexOf("</div>",i+1)));
                c.endDate.setTime(dateFormat.parse(str + " " + t.substring(t.indexOf("-")+2) + z));
                c.icon = getIcon();
                contests.add(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }
}
