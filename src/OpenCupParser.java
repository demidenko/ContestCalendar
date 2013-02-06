import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 06.02.13 14:27
 */
public class OpenCupParser implements SiteParser{
    static final SimpleDateFormat frm = new SimpleDateFormat("dd.MM.yyyy HH:mm z");
    
    @Override
    public String contestsPage() {
        String s = Utils.URLToString("http://"+mainPage(), "windows-1251");
        int i = s.indexOf("schedule");
        if(i<0) return "";
        return "http://"+mainPage()+"/"+s.substring(s.lastIndexOf("\"",i)+1, s.indexOf("\"",i));
    }

    @Override
    public String mainPage() {
        return "opencup.ru";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "windows-1251"); if(s==null) return contests;

        try{
            int i, j, k = s.indexOf("<td class=\"maintext\">"), l = s.indexOf("</table>", k);
            String number = s.substring(s.indexOf("<h3>")+4, s.indexOf("</h3>")).split(" ")[2];
            k = s.indexOf("<tr>",k);
            String str, ds;
            for(;k<l;){
                Contest c = new Contest();
                i = s.indexOf("<td>", k);
                j = s.indexOf("</td>", i);
                str = Utils.trim(s.substring(i+4,j));
                i = s.indexOf("<td>", j);
                j = s.indexOf("</td>", i);
                ds = Utils.trim(s.substring(i + 4, j));
                i = s.indexOf("<td>", j);
                j = s.indexOf("</td>", i);
                ds = ds + " " + Utils.trim(s.substring(i + 4, j)).replace("<b>","").replace("</b>","")+" MSK";
                c.startDate.setTime(frm.parse(ds));
                c.endDate.setTime(c.startDate.getTime());
                c.endDate.add(Calendar.HOUR_OF_DAY, 5);
                i = s.indexOf("<td>", j);
                j = s.indexOf("</td>", i);
                c.title = "Open Cup " +number + " " + Utils.trim(s.substring(i+4,j));
                c.mainPage = mainPage();
                if(!str.equals("-")) contests.add(c);
                k = s.indexOf("<tr>", k+1);
            }
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return contests;
    }
}
