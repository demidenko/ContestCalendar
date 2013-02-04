import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * 13.01.13 1:38
 */
public class TopCoderParser implements SiteParser {
    static final SimpleDateFormat frm = new SimpleDateFormat("dd MMM yyyy HH:mm Z", Locale.ENGLISH);
    
    public String contestsPage() {
        return "http://community.topcoder.com/tc?module=Static&d1=calendar&d2=thisMonth";
    }

    public String mainPage() {
        return "community.topcoder.com/tc";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        try{
            int i, j, k, l, day;
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
                        c.title = s.substring(s.indexOf("\">", i)+2, s.indexOf("</",i));
                        if(!c.title.toLowerCase().contains("srm") && !c.title.toLowerCase().contains("algorithm")) continue;
                        c.mainPage = mainPage();
                        str = s.substring(s.indexOf("</strong>",i)+9,s.indexOf("</div>",i));
                        while(str.contains("<")) str = str.substring(0,str.indexOf("<"))+str.substring(str.indexOf(">")+1);
                        str = Utils.trim(str);
                        sp = str.split(" ");
                        c.startDate.setTime(frm.parse(day + " " + t + " " + sp[sp.length - 1] + " EST"));
                        c.endDate.setTime(c.startDate.getTime());
                        c.endDate.add(Calendar.MINUTE, 60 + 45);
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
