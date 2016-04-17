import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class GoogleCodeJamParser extends SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm Z", Locale.ENGLISH);
    
    
    public String contestsPage() {
        return "http://code.google.com/codejam/schedule.html";
    }

    public String mainPage() {
        return "https://code.google.com/codejam";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;

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
                str = Utils.trim(str);
                if(str.equals("TBD")) str = "00:00 UTC";
                t += " "+str;
                try{
                    c.startDate.setTime(dateFormat.parse(t));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
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
                str = Utils.trimTags(s.substring(i,j));
                c.title = Utils.trim(str);
                c.deadLine = Utils.timeConsts.YEAR;
                c.icon = getIcon();
                contests.add(c);
            }
            Collections.sort(contests, new Comparator<Contest>() {
                @Override
                public int compare(Contest o1, Contest o2) {
                    return o1.startDate.compareTo(o2.startDate);
                }
            });
            Calendar nowDate = Utils.getNowDate();
            for(Contest c : contests) if(MyTableModel.status(c, nowDate)>=0){
                str = Utils.URLToString("https://code.google.com/codejam/contest/microsite-info","UTF-8");
                if(str!=null){
                    i = str.indexOf("contestId");
                    if(i!=-1){
                        str = Utils.trim(str.substring(str.indexOf(":",i)+1,str.indexOf(",",i)));
                        c.contestPage = "https://code.google.com/codejam/contest/" + str + "/dashboard";
                    }
                }
                break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return contests;
    }
}
