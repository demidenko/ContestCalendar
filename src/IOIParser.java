import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by demich on 24.05.15.
 */
public class IOIParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.ENGLISH);

    @Override
    public String contestsPage() {
        return "http://www.ioinformatics.org/history.shtml";
    }

    @Override
    public String mainPage() {
        return "http://www.ioinformatics.org/index.shtml";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        try{
            int i = s.indexOf("Current Contest");
            String str = s.substring(s.indexOf("<li>",i+1), s.indexOf("</li>",i+1));
            String t = Utils.trimTags(str);
            Contest c = new Contest();
            i = t.lastIndexOf(" - ");
            int y = Integer.parseInt(t.substring(1,t.indexOf(" ",1)));
            c.title = "IOI" + t.substring(0,i);
            c.mainPage = mainPage();
            c.deadLine = Utils.timeConsts.YEAR;
            t = t.substring(i+3);
            String sp[] = t.split(" ");
            if(sp[0].indexOf("/")==-1) sp[0]+="/"+sp[0];
            try{
                t = sp[1].substring(0,sp[1].indexOf('-')) + " " + sp[0].substring(0,sp[0].indexOf('/')) + " " + y + " 00:00";
                c.startDate.setTime(dateFormat.parse(t));
                t = sp[1].substring(sp[1].indexOf('-')+1) + " " + sp[0].substring(sp[0].indexOf('/')+1) + " " + y + " 23:59";
                c.endDate.setTime(dateFormat.parse(t));
            }catch (ParseException e){
                e.printStackTrace();

            }
            i = str.indexOf("href=");
            if(i!=-1){
                s = Utils.URLToString("http://www.ioinformatics.org/" + str.substring(i+6,str.indexOf("\">",i)), "UTF-8");
                if(s!=null && (i = s.indexOf("Home Page"))!=-1){
                    i = s.lastIndexOf("\"",i);
                    int j = s.lastIndexOf("\"",i-1);
                    c.contestPage = s.substring(j+1,i);
                }
            }
            c.icon = getIcon();
            contests.add(c);
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }
}
