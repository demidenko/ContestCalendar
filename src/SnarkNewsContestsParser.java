import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class SnarkNewsContestsParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm z");
    
    public String contestsPage() {
        return "http://contests.snarknews.info/index.cgi?data="+getActual()+"/schedule&menu=index&head=index&mod="+getActual()+"&class="+getActual();
    }

    public String mainPage() {
        return "http://contests.snarknews.info/";
    }

    public String getActual(){
        return "snws15";
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
                c.icon = getIcon();
                c.mainPage = mainPage();
                i = s.indexOf("<td>", k);
                c.title = Utils.trim(s.substring(i+4, s.indexOf("</td>",i+1)));
                if(c.title.length()==0) continue;
                i = s.indexOf("<td>", i+1);
                c.startDate.setTime(dateFormat.parse(Utils.trim(Utils.trimTags(s.substring(i+4, s.indexOf("</td>",i+1))))+" MSK"));
                i = s.indexOf("<td>", i+1);
                c.endDate.setTime(dateFormat.parse(Utils.trim(Utils.trimTags(s.substring(i+4, s.indexOf("</td>",i+1))))+" MSK"));
                c.deadLine = Utils.timeConsts.WEEK;
                contests.add(c);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return contests;
    }
}
