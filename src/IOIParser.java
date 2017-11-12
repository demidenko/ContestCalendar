import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by demich on 24.05.15.
 */
public class IOIParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.ENGLISH);

    @Override
    public String contestsPage() {
        return "http://www.ioinformatics.org/history.shtml";
    }

    @Override
    public String mainPage() {
        return "http://www.ioinformatics.org/index.shtml";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;



        try{
            int i = s.indexOf("Current Contest");
            String str = s.substring(s.indexOf("<li>",i+1), s.indexOf("</li>",i+1));
            String t = Utils.trimTags(str);
            t = t.replaceAll("[-,./]"," ");
            String sp[] = Utils.trim(t).split("[ ]+");
            int y = Integer.parseInt(sp[0]);
            ArrayList<String> months = new ArrayList<>(), days = new ArrayList<>(); ///rude, i know...
            for(int k=sp.length-1;k>=0;--k){
                String z = sp[k].toLowerCase();
                if(Utils.month.containsKey(z)){
                    if(months.size()==2) continue;
                    months.add(z);
                }else{
                    try{
                        int day = Integer.parseInt(z);
                        if(days.size()==2) continue;
                        days.add(z);
                    }catch (Exception e){
                        continue;
                    }
                }
            }
            if(months.size()==1) months.add(months.get(0));
            Contest c = new Contest();
            c.title = "IOI " + y;
            c.mainPage = mainPage();
            c.deadLine = Utils.timeConsts.YEAR;
            try{
                t = days.get(1) + " " + months.get(1) + " " + y + " 00:00";
                c.startDate.setTime(dateFormat.parse(t));
                t = days.get(0) + " " + months.get(0) + " " + y + " 00:00";
                c.endDate.setTime(dateFormat.parse(t));
            }catch (ParseException e){
                e.printStackTrace();
            }
            c.icon = getIcon();
            contests.add(c);
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }
}
