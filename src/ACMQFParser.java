import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 01.04.13 21:57
 */
public class ACMQFParser implements SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy HH mm ss z");

    public String contestsPage() {
        String s = Utils.URLToString("http://"+mainPage(), "UTF-8");
        if(s==null) return null;
        int i = s.indexOf("Сезоны:");
        if(i<0) return null;
        return "http:"+s.substring(s.indexOf("a href=\"", i)+8, s.indexOf("\">",i));
    }

    public String mainPage() {
        return "ikit.sfu-kras.ru/olimpACM";
    }


    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;
        
        try{
            int i, j, k = s.indexOf("h3");
            String str, sp[];
            k = s.indexOf("center",k);
            j = s.indexOf("</b>", k);
            i = s.lastIndexOf("/>", j);
            str = Utils.trim(s.substring(i + 2, j));
            sp = str.split(" ");
            Contest c = new Contest();
            c.title = "1/4 ACM ICPC";
            c.startDate.setTime(dateFormat.parse(sp[1] + " " + Utils.month.get(sp[4]) + " " + sp[5] + " 00 00 00 KRAT"));
            c.endDate.setTime(dateFormat.parse(sp[3] + " " + Utils.month.get(sp[4]) + " " + sp[5] + " 23 59 59 KRAT"));
            c.deadLine = Utils.timeConsts.YEAR;
            c.mainPage = mainPage();
            c.contestPage = contestsPage();
            contests.add(c);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return contests;
    }
}
