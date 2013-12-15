import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class OpenCupParser implements SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm z");
    
    @Override
    public String contestsPage() {
        String s = Utils.URLToString("http://"+mainPage(), "windows-1251");
        if(s==null) return null;
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
                boolean valid = true;
                try {
                    c.startDate.setTime(dateFormat.parse(ds));
                } catch (ParseException e) {
                    e.printStackTrace(); 
                    valid = false;
                }
                if(valid){
                    c.endDate.setTime(c.startDate.getTime());
                    c.endDate.add(Calendar.HOUR_OF_DAY, 5);
                    i = s.indexOf("<td>", j);
                    j = s.indexOf("</td>", i);
                    c.title = "Open Cup " +number + " " + Utils.trim(s.substring(i+4,j));
                    c.mainPage = mainPage();
                    c.deadLine = Utils.timeConsts.WEEK;
                    if(!str.equals("-")) contests.add(c);
                }
                k = s.indexOf("<tr>", k+1);
            }

            s = Utils.URLToString("http://"+mainPage(), "windows-1251"); if(s==null) return contests;
            k = 0;
            for(;;){
                k = s.indexOf("Enter contest", k+1);
                if(k<0) break;
                i = s.lastIndexOf("href=\"", k);
                str = s.substring(i+6, s.indexOf("\"",i+6));
                j = s.lastIndexOf("</li>", i);
                i = s.lastIndexOf("<li>", j);
                List<String> tr = Arrays.asList(Utils.trimTags(s.substring(i + 4, j)).split(" "));
                Contest best = null;
                int cbest = 0;
                for(Contest c : contests){
                    int cnt = 0;
                    for(String t : c.title.split(" ")) if(tr.contains(t)) ++cnt;
                    if(cnt>cbest){
                        cbest = cnt;
                        best = c;
                    }
                }
                if(best!=null && cbest>1) best.contestPage = str;
            }
        
        return contests;
    }
}
