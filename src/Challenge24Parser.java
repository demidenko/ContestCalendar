import java.util.ArrayList;

/**
 * Created by demich on 20.04.16.
 */
public class Challenge24Parser extends SiteParser {
    @Override
    public String contestsPage() {
        return "http://ch24.org/schedule.html";
    }

    @Override
    public String mainPage() {
        return "http://ch24.org";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;

        try{
            int i = s.indexOf("<strong>");
            int f = s.indexOf("</ul>", i+1);
            for(;;){
                if(i==-1 || i>=f) break;
                Contest c = new Contest();
                int j = s.indexOf("</strong>",i+1);
                c.title = s.substring(i+8, j-1);
                i = s.indexOf("<strong>", i+1);
                if(i>f) i=f;
                String t = s.substring(j + 9, i);
                t = Utils.trim(Utils.trimTags(t));
                if(!t.contains("-")) continue;
                //Main.writeln(c.title,' ',t);
                String sp[] = t.toLowerCase().split("[, ]");

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return contests;
    }
}
