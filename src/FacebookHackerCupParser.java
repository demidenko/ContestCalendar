import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by demich on 24.05.15.
 */
public class FacebookHackerCupParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm a Z", Locale.ENGLISH);

    @Override
    public String contestsPage() {
        return "https://www.facebook.com/hackercup/notes";
    }

    @Override
    public String mainPage() {
        return "https://www.facebook.com/hackercup";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;

        try{
            int i = -1;
            String t;
            for(;;){
                i = s.indexOf("<a href=", i+1);
                if(i==-1) break;
                String str = s.substring(i,s.indexOf("</a",i+1));
                t = Utils.trimTags(Utils.trim(str));
                if(t.matches("Hacker Cup [0-9]+ FAQ")){
                    str = "https://www.facebook.com/" + str.substring(str.indexOf("\"")+1,str.lastIndexOf("\""));
                    s = Utils.URLToString(str, "UTF-8"); if(s==null) return contests;
                    break;
                }
            }
            if(i!=-1){
                i = s.indexOf("When are the rounds?");
                if (i == -1) throw new AssertionError(i);
                int k = s.indexOf("<strong>",i);
                for(;;){
                    i = s.indexOf("<li>", i+1);
                    if(i==-1 || i>=k) break;
                    t = s.substring(i+4,s.indexOf("</li",i));
                    t = t.replaceAll("[.,]","");
                    String tp[] = t.split("[^\\w:]");
                    Contest c = new Contest();
                    c.title = "";
                    for(String z : tp) if(z.matches("([0-9A-Z])(.*)")) c.title+=z+" "; else break;
                    int h = -1;
                    for(int j=2;j+1<tp.length;++j) if(tp[j-2].equals("will") && tp[j-1].equals("last") && tp[j+1].equals("hours")){
                        h = Integer.parseInt(tp[j]);
                        break;
                    }
                    boolean ok = false;
                    for(int j=3;j+3<tp.length;++j)if(tp[j].equals("at")){
                        String str = tp[j-2] + " " + tp[j-3] + " " + tp[j-1] + " " + tp[j+1] + " " + tp[j+2] + " " + tp[j+3];
                        try{
                            c.startDate.setTime(dateFormat.parse(str));
                        }catch (ParseException e){
                            continue;
                        }
                        c.endDate.setTime(c.startDate.getTime());
                        c.endDate.set(Calendar.HOUR, c.endDate.get(Calendar.HOUR) + h);
                        ok = true;
                        break;
                    }
                    if(!ok) continue;
                    c.deadLine = Utils.timeConsts.YEAR;
                    c.mainPage = c.contestPage = mainPage();
                    c.icon = getIcon();
                    contests.add(c);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }
}
