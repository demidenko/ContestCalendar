import java.util.ArrayList;

/**
 * Created by demich on 6/19/14.
 */
public class HackerRankParser extends SiteParser {

    @Override
    public String contestsPage() {
        return "https://www.hackerrank.com/rest/contests/upcoming";
    }

    @Override
    public String mainPage() {
        return "https://www.hackerrank.com";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;
        try{
            int i = 0;
            for(;;){
                i = s.indexOf("\"name\"", i+1);
                if(i<0) break;
                int b = s.lastIndexOf('{', i);
                int e = s.indexOf('}',i);
                int k = s.indexOf("\"epoch_starttime\"",b);
                if(!(k>b && k<e)) continue;
                Contest c = new Contest();
                c.startDate.setTimeInMillis(Long.parseLong(Utils.trim(s.substring(s.indexOf(':',k)+1,s.indexOf(',',k)))+"000"));
                k = s.indexOf("\"epoch_endtime\"",b);
                c.endDate.setTimeInMillis(Long.parseLong(Utils.trim(s.substring(s.indexOf(':',k)+1,s.indexOf(',',k)))+"000"));
                k = s.indexOf('\"', i+6);
                c.title = s.substring(k + 1, s.indexOf('\"', k + 1));
                c.mainPage = mainPage();
                k = s.indexOf("\"slug\"", b);
                k = s.indexOf('\"', k + 6);
                c.contestPage = c.mainPage + "/contests/" + s.substring(k+1, s.indexOf('\"',k+1));
                c.icon = getIcon();
                contests.add(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return contests;
    }

    /*
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    public String contestsPage() {
        return "https://www.hackerrank.com/calendar/feed.rss";
    }
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;
        s = s.replace("<url/>", "<url></url>");
        try{
            int i = 0, j;
            for(;;){
                i = s.indexOf("<item>", i+1);
                if(i==-1) break;
                i = s.indexOf("<title>", i);
                j = s.indexOf("</title>", i+1);
                Contest c = new Contest();
                c.mainPage = mainPage();
                c.title = Utils.replaceHTMLSymbols(Utils.trim(s.substring(i+7,j)));
                i = s.indexOf("<url>", j+1);
                j = s.indexOf("</url>", i+1);
                c.contestPage = Utils.trim(s.substring(i+5,j));
                if(!c.contestPage.contains(mainPage())) continue;
                i = s.indexOf("<startTime>", j+1);
                j = s.indexOf("</startTime>", i+1);
                try{
                    c.startDate.setTime(dateFormat.parse(Utils.trim(s.substring(i+11,j))));
                    i = s.indexOf("<endTime>", j+1);
                    j = s.indexOf("</endTime>", i+1);
                    c.endDate.setTime(dateFormat.parse(Utils.trim(s.substring(i+9,j))));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                boolean ok = c.endDate.getTimeInMillis()-c.startDate.getTimeInMillis()<Utils.timeConsts.DAY*10;
                //ok|=c.title.contains("Week");
                //ok|=c.title.contains("Infinitum");
                if(!ok) continue;
                c.icon = getIcon();
                contests.add(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return contests;
    }*/
}
