import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * 13.01.13 1:32
 */
public class GoogleCodeJamParser implements SiteParser{
    static final SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm Z", Locale.ENGLISH);
    
    
    public String contestsPage() {
        return "http://code.google.com/codejam/schedule.html";
    }

    public String mainPage() {
        return "code.google.com/codejam";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        try{
            int i, j, k = s.indexOf("class=\"block\""), end = s.indexOf("</table>", k);
            String str, t, sp[];
            for(;;){
                k = s.indexOf("<tr>", k+1);
                if(k>end || k<0) break;
                Contest c = new Contest();
                c.mainPage = mainPage();
                i = s.indexOf("<td class=\"date\">",k); j = s.indexOf("</td>",i);
                str = s.substring(i,j);
                while(str.contains("<")) str = str.substring(0,str.indexOf("<"))+str.substring(str.indexOf(">")+1);
                t = Utils.trim(str);
                if(t.indexOf("Ends")>0) t = t.substring(0,t.indexOf("Ends"));
                i = s.indexOf("<td class=\"time\">",k); j = s.indexOf("</td>",i);
                str = s.substring(i,j);
                while(str.contains("<")) str = str.substring(0,str.indexOf("<"))+str.substring(str.indexOf(">")+1);
                if(str.equals("TBD")) str = "00:00 UTC";
                t += " "+Utils.trim(str);
                c.startDate.setTime(format.parse(t));
                i = s.indexOf("<td class=\"duration\">",k); j = s.indexOf("</td>",i);
                str = s.substring(i,j);
                while(str.contains("<")) str = str.substring(0,str.indexOf("<"))+str.substring(str.indexOf(">")+1);
                sp = Utils.trim(str).split(" ");
                c.endDate.setTime(c.startDate.getTime());
                if(sp[0].equals("TBD")) c.endDate.add(Calendar.DAY_OF_MONTH, 1);
                for(String x:sp){
                    if(x.contains("d")) c.endDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(x.replace("d", ""))); else
                    if(x.contains("hr")) c.endDate.add(Calendar.HOUR_OF_DAY, Integer.parseInt(x.replace("hr",""))); else
                    if(x.contains("min")) c.endDate.add(Calendar.MINUTE, Integer.parseInt(x.replace("min","")));
                }
                i = s.indexOf("<td class=\"desc\">",k); j = s.indexOf("</td>",i);
                str = s.substring(i,j);
                while(str.indexOf("<")>=0) str = str.substring(0,str.indexOf("<"))+str.substring(str.indexOf(">")+1);
                c.title = Utils.trim(str);
                contests.add(c);
            }
        }catch (ParseException e){
            e.printStackTrace();
        }
        return contests;
    }
}
