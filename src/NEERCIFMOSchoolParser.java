import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 15.01.13 21:44
 */
public class NEERCIFMOSchoolParser implements SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy HH-mm z");

    public String contestsPage() {
        return "http://neerc.ifmo.ru/school/io/index.html";
    }

    public String mainPage() {
        return "neerc.ifmo.ru/school/io";
    }


    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "windows-1251"); if(s==null) return contests;

        try{
            int i, j, k1 = s.indexOf("Расписание командных олимпиад"), k2 = s.indexOf("Расписание личных олимпиад");
            String t, sp[];
            j = k1;
            for(;;){
                Contest c = new Contest();
                i = s.indexOf("class=\"date\"",j);
                if(i<0) break;
                j = s.indexOf("class=\"time\"",i);
                sp = s.substring(i+13, s.indexOf("</td>",i)).split(" ");
                t = sp[0]+" "+Utils.month.get(sp[1].toLowerCase())+" "+sp[2]+" ";
                sp = s.substring(j+13, s.indexOf("</td>",j)).split(",");
                t += sp[0]+" MSK";
                c.startDate.setTime(dateFormat.parse(t));
                c.endDate.setTime(c.startDate.getTime());
                c.endDate.add(Calendar.HOUR_OF_DAY, 5);
                if(i<k2) c.title = "Командная олимпиада школьников";
                else c.title = "Личная олимпиада школьников";
                for(i=1;i<sp.length;++i) c.title +=","+sp[i];
                c.mainPage = mainPage();
                c.contestPage = "http://neerc.ifmo.ru/testing/index.jsp";
                contests.add(c);
            }
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return contests;
    }
}
