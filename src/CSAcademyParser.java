import java.util.ArrayList;

/**
 * Created by demich on 09.05.16.
 */
public class CSAcademyParser extends SiteParser {
    @Override
    public String contestsPage() {
        return "https://csacademy.com/contests/?";
    }

    @Override
    public String mainPage() {
        return "https://csacademy.com";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;
        try {
            int i = s.indexOf("\"Contest\":"), end = s.indexOf(']', i+1);
            int b = 0;
            for(;;){
                for(;i<end;){
                    ++i;
                    char c = s.charAt(i);
                    if(c=='{'){
                        ++b;
                        if(b==1) break;
                    }else
                    if(c=='}') --b;
                }
                if(i>=end) break;
                Contest c = new Contest();
                int k = s.indexOf("\"longName\":", i+1);
                k = s.indexOf('\"', k+10);
                c.title = s.substring(k+1, s.indexOf('\"', k+1));
                k = s.indexOf("\"name\":", i+1);
                k = s.indexOf('\"', k+6);
                c.contestPage = mainPage() + "/contest/" + s.substring(k+1, s.indexOf('\"', k+1));
                if(c.contestPage.contains("virtual") || c.contestPage.contains("interviews-") || c.contestPage.contains("algorithms-")) continue;
                k = s.indexOf("\"startTime\":", i+1);
                try {
                    c.startDate.setTimeInMillis(Long.parseLong(Utils.trim(s.substring(s.indexOf(':',k)+1, s.indexOf('.', k+1))))*1000);
                }catch (NumberFormatException e){
                    continue;
                }
                k = s.indexOf("\"endTime\":", i+1);
                try {
                    c.endDate.setTimeInMillis(Long.parseLong(Utils.trim(s.substring(s.indexOf(':',k)+1, s.indexOf('.', k+1))))*1000);
                }catch (NumberFormatException e){
                    continue;
                }
                c.mainPage = mainPage();
                c.icon = getIcon();
                contests.add(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return contests;
    }
}
