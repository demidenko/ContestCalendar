import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class SnarkNewsContestsParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm z");
    
    public String contestsPage() {
        return "http://contests.snarknews.info/index.cgi?data="+getActual()+"/schedule&menu=index&head=index&mod="+getActual()+"&class="+getActual();
    }

    public String mainPage() {
        return "http://contests.snarknews.info/";
    }

    static final String gs = "wwwwssssssww";
    static final int gm[] = new int[]{0,0,0,0,0,0,0,0,0,0,1,1};
    public String getActual(){
        Calendar c = Calendar.getInstance();
        int m = c.get(Calendar.MONTH);
        int y = c.get(Calendar.YEAR);
        return "sn"+gs.charAt(m)+"s"+((y+gm[m])%100);
    }

    public ArrayList<Contest> parse(){
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;
        try{
            int i, k = s.indexOf("class=\"maintext\""), l = s.indexOf("</table>", k);
            k = s.indexOf("<tr>", k+1);
            for(;;){
                k = s.indexOf("<tr>", k+1);
                if(k>l || k<0) break;
                Contest c = new Contest();
                c.mainPage = mainPage();
                i = s.indexOf("<td>", k);
                c.title = Utils.trim(s.substring(i+4, s.indexOf("</td>",i+1)));
                if(c.title.length()==0) continue;
                i = s.indexOf("<td>", i+1);
                try{
                    c.startDate.setTime(dateFormat.parse(Utils.trim(Utils.trimTags(s.substring(i+4, s.indexOf("</td>",i+1))))+" MSK"));
                    i = s.indexOf("<td>", i+1);
                    c.endDate.setTime(dateFormat.parse(Utils.trim(Utils.trimTags(s.substring(i+4, s.indexOf("</td>",i+1))))+" MSK"));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                c.deadLine = Utils.timeConsts.WEEK;
                c.icon = getIcon();
                contests.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contests;
    }
}
