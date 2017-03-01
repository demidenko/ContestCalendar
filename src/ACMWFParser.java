import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by demich on 6/20/14.
 */
public class ACMWFParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm", Locale.ENGLISH);


    @Override
    public String contestsPage() {
        return "https://icpc.baylor.edu/worldfinals/schedule";
    }

    @Override
    public String mainPage() {
        return "https://icpc.baylor.edu/worldfinals";
    }


    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;

        int k,j,i = s.indexOf("hosted by");
        //String university = s.substring(i+10, s.indexOf("<",i+1));
        //String timezone = getTimeZone(university);


        try {
            String date = null;
            for(;;){
                i = s.indexOf("<tr>", i+1);
                if(i<0) break;

                k = s.indexOf("</tr>",i+1);
                j = s.indexOf("<strong>",i+1);
                if(j>=0 && j<k){
                    String sp[] = Utils.trimTags(s.substring(j+7, s.indexOf("</strong>",j+1))).split(" ");
                    if(sp.length<3) return contests;
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
                    if(j>=0){
                        if(title.substring(j+4).toLowerCase().contains("badges")){
                            Contest c = new Contest();
                            c.mainPage = mainPage();
                            c.contestPage = contestsPage();
                            c.deadLine = Utils.timeConsts.YEAR;
                            c.title = title.substring(0,j);
                            int year = -1;
                            for(String token : c.title.split(" ")){
                                try{
                                    year = Integer.parseInt(token);
                                }catch (NumberFormatException e){
                                    continue;
                                }
                                break;
                            }
                            try{
                                from = "00:00";
                                to = "23:59";
                                c.startDate.setTime(dateFormat.parse(date + " " + year + " " + from));
                                c.endDate.setTime(dateFormat.parse(date + " " + year + " " + to));
                            }catch (ParseException e){
                                e.printStackTrace();
                                continue;
                            }
                            c.icon = getIcon();
                            contests.add(c);
                        }
                    }
                }

                i = k;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return contests;
    }
}
