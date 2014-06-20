import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.TreeSet;

/**
 * Created by demich on 6/20/14.
 */
public class ACMWFParser implements SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm Z");


    @Override
    public String contestsPage() {
        return "http://icpc.baylor.edu/worldfinals/schedule";
    }

    @Override
    public String mainPage() {
        return "icpc.baylor.edu/worldfinals";
    }


    static final String wikipedia = "http://en.wikipedia.org";
    String getTimeZone(String university){
        university = university.replace(" ", "_");
        String s = Utils.URLToString(wikipedia + "/wiki/" + university, "UTF-8");

        int i = s.indexOf("Location</th>");
        int j = s.indexOf("<a href=", i+1);
        int k = s.indexOf("</tr>", i+1);
        i = s.indexOf("<span class=\"flagicon\">", i+1);

        if(i>=0 && i<k){
            i = s.indexOf("</span>", i+1);
            if(j<i) j = s.indexOf("<a href=", i+1);
        }

        String city = s.substring(j+9, s.indexOf("\"", j+9));

        s = Utils.URLToString(wikipedia + city, "UTF-8");

        i = s.indexOf("Time zone");
        i = s.indexOf("<a href=", i+1);

        return Utils.trimTags(s.substring(i, s.indexOf("</a>", i)));
    }

    static boolean isTitle(String title, int[] _){
        TreeSet<String> q = new TreeSet<String>();
        ///stupid stupid stupid, why not q.addAll(String[]); ????
        for(String str : title.split(" ")) q.add(str.toLowerCase());

        if(q.contains("acm-icpc")) q.remove("acm-icpc"); else
        if(q.contains("acm") && q.contains("icpc")){
            q.remove("acm");
            q.remove("icpc");
        } else return false;



        if(q.contains("worldfinals")) q.remove("worldfinals"); else
        if(q.contains("world") && q.contains("finals")){
            q.remove("world");
            q.remove("finals");
        } else return false;

        if(q.size()!=1) return false;

        try{
            Integer year = Integer.parseInt(q.first());
            _[0] = year;
        }catch (NumberFormatException e){
            return false;
        }

        return true;
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        int k,j,i = s.indexOf("hosted by"), _[] = new int[1];
        String university = s.substring(i+10, s.indexOf("<",i+1));
        String timezone = getTimeZone(university);


        try {
            String date = null;
            for(;;){
                i = s.indexOf("<tr>", i+1);
                if(i<0) break;

                k = s.indexOf("</tr>",i+1);
                j = s.indexOf("<strong>",i+1);
                if(j>=0 && j<k){
                    String sp[] = Utils.trimTags(s.substring(j+7, s.indexOf("</strong>",j+1))).split(" ");
                    date = sp[1] + " " + sp[2];
                }else{
                    i = s.indexOf("<td", i+1);
                    String from = s.substring(s.indexOf(">",i+1)+1, s.indexOf("</td>",i+1));
                    i = s.indexOf("<td", i+1);
                    i = s.indexOf("<td", i+1);
                    String to = s.substring(s.indexOf(">",i+1)+1, s.indexOf("</td>",i+1));
                    i = s.indexOf("<td", i+1);
                    i = s.indexOf("<td", i+1);
                    String title = s.substring(s.indexOf(">",i+1)+1, s.indexOf("</td>",i+1));

                    j = title.indexOf("<br");
                    if(j>=0) title = title.substring(0,j);
                    if(isTitle(title, _)){
                        Contest c = new Contest();
                        c.mainPage = mainPage();
                        c.deadLine = Utils.timeConsts.YEAR;
                        c.title = title;
                        c.startDate.setTime(dateFormat.parse(date + " " + _[0] + " " + from + " " + timezone));
                        c.endDate.setTime(dateFormat.parse(date + " " + _[0] + " " + to + " " + timezone));
                        contests.add(c);
                        break;
                    }
                }

                i = k;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return contests;
    }
}
