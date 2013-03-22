import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 22.03.13 17:04
 */
public class SIBSUIRegionalOlympiadParser implements SiteParser{
    static final SimpleDateFormat frm = new SimpleDateFormat("dd MM yyyy HH mm ss z");
    
    @Override
    public String contestsPage() {
        return "http://www.sibsiu.ru/olymp/programming/info.php";
    }

    @Override
    public String mainPage() {
        return "www.sibsiu.ru/olymp/programming";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "windows-1251"); if(s==null) return contests;
        try{
            int i, j;
            String str, sp[];
            i = s.indexOf("strong");
            j = s.indexOf("strong", i+1);
            str = Utils.trim(s.substring(i+7, j-2));
            sp = str.split("[ -]");
            Contest c = new Contest();
            c.title = "Региональная олимпиада по программированию, г. Новокузнецк";
            c.startDate.setTime(frm.parse(sp[0]+" "+Utils.month.get(sp[2])+" "+sp[3]+" 00 00 00 KRAT"));
            c.endDate.setTime(frm.parse(sp[1]+" "+Utils.month.get(sp[2])+" "+sp[3]+" 23 59 59 KRAT"));
            c.mainPage = mainPage();
            contests.add(c);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return contests;
    }
}
