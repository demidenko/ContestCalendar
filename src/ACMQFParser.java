import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ACMQFParser extends SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy HH mm ss z");

    public String contestsPage() {
        String s = Utils.URLToString(mainPage(), "UTF-8");
        if(s==null) return null;
        int i = s.indexOf("Сезоны:");
        if(i<0) return null;
        return "http://ikit.sfu-kras.ru"+s.substring(s.indexOf("a href=\"", i)+8, s.indexOf("\">",i));
    }

    public String mainPage() {
        return "http://ikit.sfu-kras.ru/olimp/acm";
    }


    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;
        
        try{
            int i, j, k = s.indexOf("h3");
            String str, sp[];
            k = s.indexOf("center",k);
            j = s.indexOf("</b>", k);
            i = s.lastIndexOf("/>", j);
            str = Utils.trim(s.substring(i + 2, j));
            str = Utils.trimTags(str);
            sp = str.split(" ");
            Contest c = new Contest();
            c.title = "1/4 ACM ICPC";
            try{
                c.startDate.setTime(dateFormat.parse(sp[1] + " " + Utils.month.get(sp[4]) + " " + sp[5] + " 00 00 00 KRAT"));
                c.endDate.setTime(dateFormat.parse(sp[3] + " " + Utils.month.get(sp[4]) + " " + sp[5] + " 23 59 59 KRAT"));
            }catch (ParseException e){
                e.printStackTrace();
                return null;
            }
            c.deadLine = Utils.timeConsts.YEAR;
            c.mainPage = mainPage();
            c.contestPage = contestsPage();
            c.icon = getIcon();
            contests.add(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contests;
    }
}
