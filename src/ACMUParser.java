import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 14.01.13 23:19
 */
public class ACMUParser implements SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy HH:mm:ss Z");

    public String contestsPage() {
        return "http://acmu.ru/asp/champ/";
    }

    public String mainPage() {
        return "acmu.ru";
    }


    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "windows-1251"); if(s==null) return contests;

        try{
            int i, j, k = s.indexOf("<h1>Расписание</h1>");
            String str, t, sp[];
            i = s.indexOf("<h4>", k);
            for(;i>=0;){
                Contest c = new Contest();
                c.mainPage = mainPage();
                j = s.indexOf("</h4>",i);
                str = Utils.trim(s.substring(i+4, j)).replace("&nbsp;", " ");
                c.title = Utils.trim(str.substring(str.indexOf('.')+1));
                if((i=c.title.indexOf("<"))>=0) c.title = Utils.trim(c.title.substring(0,i));
                sp = str.split(" ");
                t = sp[0]+Utils.month.get(sp[1].toLowerCase())+sp[2];
                i = s.indexOf("<li>",j);
                j = s.indexOf("</li>",i);
                str = s.substring(i+4,j).split(" ")[1];
                t += " "+str+" MSK";
                c.startDate.setTime(dateFormat.parse(t));
                i = s.indexOf("<li>",j);
                j = s.indexOf("</li>",i);
                str = s.substring(i+4,j).split(" ")[1];
                c.endDate.setTime(c.startDate.getTime());
                c.endDate.add(Calendar.MINUTE, Integer.parseInt(str));
                k = s.indexOf("</ul>", i);
                for(;;){
                    i = s.indexOf("<li>",j);
                    if(i<0 || i>k) break;
                    j = s.indexOf("</li>",i);
                    str = s.substring(i+4, j);
                    if(!str.contains("Количество задач")) continue;
                    c.contestPage = contestsPage() + str.substring(str.indexOf("href=")+5, str.indexOf(">")).replace("rating", "stage_info");
                    break;
                }
                contests.add(c);
                i = s.indexOf("<h4>", k);
            }
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return contests;
    }
}
