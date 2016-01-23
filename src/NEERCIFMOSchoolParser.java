import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class NEERCIFMOSchoolParser extends SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy HH-mm z");

    public String contestsPage() {
        return "http://neerc.ifmo.ru/school/io/index.html";
    }

    public String mainPage() {
        return "http://neerc.ifmo.ru/school/io/";
    }


    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "windows-1251"); if(s==null) return contests;

        try{
            int i, j, j2, k;
            int k1 = s.indexOf("Расписание командных олимпиад"), k2 = s.indexOf("Расписание личных олимпиад");
            String t, z, sp[];
            j = k1;
            for(;;){
                Contest c = new Contest();
                i = s.indexOf("class=\"date\"",j);
                if(i<0) break;
                j = s.indexOf("class=\"time\"", i);
                sp = s.substring(i+13, s.indexOf("</td>",i)).split(" ");
                t = sp[0]+" "+Utils.month.get(sp[1].toLowerCase())+" "+sp[2]+" ";
                sp = s.substring(j+13, s.indexOf("</td>",j)).split(",");
                t += sp[0]+" MSK";
                try{
                    c.startDate.setTime(dateFormat.parse(t));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                c.endDate.setTime(c.startDate.getTime());
                c.endDate.add(Calendar.HOUR_OF_DAY, 5);
                if(i<k2) c.title = "Командная олимпиада школьников";
                else c.title = "Личная олимпиада школьников";
                z = "";
                k = s.indexOf("</tr>",j);
                for(;;){
                    j2 = s.indexOf("class=\"time\"",j+1);
                    if(j2>k || j2==-1) break;
                    j = j2;
                    if(!z.isEmpty()) z+=",";
                    z+=s.substring(j+13, s.indexOf("</td>",j));
                }
                if(z.isEmpty()) sp = new String[0]; else sp = z.split(",");
                for(i=0;i<sp.length;++i) c.title +=", "+sp[i];
                c.mainPage = mainPage();
                c.contestPage = "http://neerc.ifmo.ru/pcms2client/login.jsp";
                c.icon = getIcon();
                contests.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contests;
    }
}
