import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class TopCoderParser implements SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm Z", Locale.ENGLISH);
    
    public String contestsPage() {
        return "http://community.topcoder.com/tc?module=Static&d1=calendar&d2=thisMonth";
    }

    public String mainPage() {
        return "topcoder.com/tc";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        try{
            int i, j, k, l, day, stop = s.indexOf("</table>");
            String t, sp[], str;
            for(;;){
                if(s==null) return contests;
                k = s.indexOf("pageSubtitle");
                t = s.substring(s.indexOf(';',k)+1,s.indexOf("</td>",k));
                k = s.indexOf("class=\"calendar\"");
                day = 0;
                while((k=s.indexOf("class=\"value\"",k+1))>=0){
                    ++day;
                    l = s.indexOf("</td>",k);
                    i = k;
                    while((i=s.indexOf("<strong",i+1))<l && i>=0){
                        Contest c = new Contest();
                        c.title = Utils.trimTags(s.substring(s.indexOf(">", i)+1, s.indexOf("</",i)));
                        if(!c.title.toLowerCase().contains("srm")
                                && !c.title.toLowerCase().contains("algorithm")) continue;
                        c.mainPage = mainPage();
                        str = s.substring(s.indexOf("</strong>",i)+9,s.indexOf("</div>",i));
                        str = Utils.trimTags(str);
                        str = Utils.trim(str);
                        sp = str.split("["+Utils.whitespace+"]");
                        for(String z : sp) if(z.matches("[0-9][0-9]:[0-9][0-9]")){
                            c.startDate.setTime(dateFormat.parse(day + " " + t + " " + z + " EDT"));
                            break;
                        }
                        c.endDate.setTime(c.startDate.getTime());
                        c.endDate.add(Calendar.MINUTE, 75 + 5 + 15);
                        c.deadLine = c.title.contains("SRM") ? Utils.timeConsts.DAY*2 : Utils.timeConsts.YEAR;
                        contests.add(c);
                    }
                }
                l = s.indexOf("next &gt;");
                k = s.indexOf("&lt; prev");
                i = s.indexOf("<!--",k); if(i>k && i<l) break;
                i = s.indexOf("\"",k);
                j = s.indexOf("\"",i+1);
                str = "http://community.topcoder.com"+(s.substring(i+1,j).replace("&amp;", "&"));
                s = Utils.URLToString(str, "UTF-8");
            }
        }catch (ParseException e){
            e.printStackTrace();
        }

        return contests;
    }
}
