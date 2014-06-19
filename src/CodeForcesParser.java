import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class CodeForcesParser implements SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm z");
    static final SimpleDateFormat timeFormat = new SimpleDateFormat("dd:HH:mm");

    public String contestsPage() {
        return "http://codeforces.ru/contests?complete=true";
    }

    public String mainPage() {
        return "codeforces.ru";
    }

    public ArrayList<Contest> parse(){
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;
        
        try{
            int i, j, k = s.indexOf("data-contestId="), end = s.indexOf("class=\"contests-table\"");
            String str;
            Calendar d = Calendar.getInstance();
            while(k<end){
                Contest c = new Contest();
                int id = Integer.parseInt(s.substring(k+16,s.indexOf("\"",k+16)));
                i = s.indexOf("<td>", k);
                j = s.indexOf("</td>", i);
                str = s.substring(i + 4, j);
                while((i=str.indexOf("<br"))>=0) str = str.substring(0,i);
                c.title = Utils.trim(Utils.replaceHTMLSymbols(str));
                i = s.indexOf(">", s.indexOf(">", j + 5) + 1);
                j = s.indexOf("<", i);
                str = Utils.trim(s.substring(i + 1, j));
                c.startDate.setTime(dateFormat.parse(str+" MSK"));
                i = s.indexOf("<td>", j);
                j = s.indexOf("</td>", i);
                str = Utils.trim(s.substring(i + 4, j));
                if(str.indexOf(':')==str.lastIndexOf(':')) str="0:"+str;
                d.setTime(timeFormat.parse(str));
                c.endDate = Utils.sum(c.startDate, d);
                if(c.startDate.compareTo(Utils.getNowDate())<=0) c.contestPage = "http://codeforces.ru/contest/" + id;
                else c.contestPage = "http://codeforces.ru/contestRegistration/" + id;
                c.mainPage = mainPage();
                if(!c.title.contains("Codeforces")) c.deadLine = Utils.timeConsts.YEAR;
                c.deadLine = Utils.timeConsts.DAY*2;
                contests.add(c);
                k = s.indexOf("data-contestId=",k+1);
            }
        } catch (ParseException e) {
            e.printStackTrace(); 
        }
        
        return contests;
    }
}
