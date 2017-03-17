import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class GoogleCodeJamParser extends SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ENGLISH);

    
    public String contestsPage() {
        return "http://code.google.com/codejam/schedule?data=1";
    }

    public String mainPage() {
        return "https://code.google.com/codejam/";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;

        try{
            int i = 0, j = 0;
            String title = "";
            for(;;){
                int inext = s.indexOf("event_title", i+1);
                int jnext = s.indexOf("duration", j+1);
                if(inext!=-1 && inext<jnext){
                    i = inext;
                    title = s.substring(s.indexOf("\"", i+12)+1, s.indexOf("\"", i+15));
                    continue;
                }
                j = jnext;
                if(j==-1) break;
                Contest c = new Contest();
                int duration = Integer.parseInt(Utils.trim(s.substring(s.indexOf(':',j)+1, s.indexOf(',',j))));
                j = s.indexOf("name", j+1);
                c.title = title + " " + s.substring(s.indexOf("\"", j+5)+1, s.indexOf("\"", j+8));
                j = s.indexOf("startDateTime", j+1);
                try {
                    String d = s.substring(s.indexOf("\"", j+14)+1, s.indexOf("\"", j+17));
                    int k = d.indexOf('T');
                    c.startDate.setTime(dateFormat.parse(d.substring(0, k) + " " + d.substring(k+1, k+9) + " GMT" + d.substring(k+9)));
                    c.endDate.setTimeInMillis(c.startDate.getTimeInMillis());
                    c.endDate.add(Calendar.MINUTE, duration);
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                c.deadLine = Utils.timeConsts.YEAR;
                c.contestPage = mainPage();
                c.mainPage = mainPage();
                c.icon = getIcon();
                contests.add(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        /*try{
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
        }*/
        return contests;
    }
}
