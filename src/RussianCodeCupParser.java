import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class RussianCodeCupParser extends SiteParser{
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ENGLISH);

    @Override
    public String mainPage() {
        return "http://russiancodecup.ru";
    }

    public String contestsPage() {
        return "http://www.russiancodecup.ru/en/ajax/champ_reg_info/";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;

        try{
            int i = s.indexOf("\"champ\""), p, k, b;
            String t;
            i = s.indexOf("\"name\"", i);
            i = s.indexOf(':', i); i = s.indexOf('\"', i);
            String name = s.substring(i+1, s.indexOf('\"',i+1));
            i = s.indexOf("\"rounds\"");
            for(;;){
                i = s.indexOf("\"name\"", i+1);
                if(i==-1) break;
                b = s.lastIndexOf('{', i);
                k = s.indexOf(':', i);
                k = s.indexOf('\"', k);
                Contest c = new Contest();
                c.title = name + " " + s.substring(k+1, s.indexOf('\"',k+1));
                k = s.indexOf("\"start_date\"", b);
                k = s.indexOf(':', k); k = s.indexOf('\"', k);
                t = s.substring(k + 1, s.indexOf('\"', k + 1));
                p = t.indexOf('T');
                t = t.substring(0, p) + " " + t.substring(p+1, p+9) + " GMT" + t.substring(p+9);
                try{
                    c.startDate.setTime(dateFormat.parse(t));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                k = s.indexOf("\"end_date\"", b);
                k = s.indexOf(':', k); k = s.indexOf('\"', k);
                t = s.substring(k + 1, s.indexOf('\"', k + 1));
                p = t.indexOf('T');
                t = t.substring(0, p) + " " + t.substring(p+1, p+9) + " GMT" + t.substring(p+9);
                try{
                    c.endDate.setTime(dateFormat.parse(t));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                c.mainPage = mainPage();
                c.deadLine = Utils.timeConsts.YEAR;
                c.icon = getIcon();
                contests.add(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }
    /*
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy HH:mm Z");
    public String contestsPage() {
        return "http://russiancodecup.ru/about";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;

        try{
            int i=s.indexOf("<strong>"), j=0, k = s.indexOf("</table>", i);
            for(;;){
                i = s.indexOf("<tr>", i+1);
                if(i<0 || i>=k) break;
                i=s.indexOf("<td>", i+1);
                Contest c = new Contest();
                c.title = "Russian Code Cup "+s.substring(i+4, s.indexOf("</td",i));
                i=s.indexOf("<td>", i + 1);
                i=s.indexOf("<td>", i+1);
                String str = s.substring(i+4, s.indexOf("</td",i)).replace(",","");
                String sp[] = str.split(" ");
                String t = sp[0]+" "+Utils.month.get(sp[1])+" "+Calendar.getInstance().get(Calendar.YEAR)+" ";
                try{
                    c.startDate.setTime(dateFormat.parse(t+(sp.length>4 ? sp[4] : "00:00")+" MSK"));
                    c.endDate.setTime(dateFormat.parse(t+(sp.length>6 ? sp[6] : "24:00")+" MSK"));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                c.mainPage = mainPage();
                c.deadLine = Utils.timeConsts.YEAR;
                c.icon = getIcon();
                contests.add(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }
     */
    /*
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss Z");
    public String contestsPage() {
        return "http://russiancodecup.ru/ical";
    }
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        try{
            int i, j=0, k;
            for(;;){
                i = s.indexOf("BEGIN:VEVENT", j); if(i<0) break;
                j = s.indexOf("END:VEVENT", i);
                Contest c = new Contest();
                k = s.indexOf("SUMMARY:",i);
                c.title = "Russian Code Cup "+s.substring(s.indexOf(':',k)+1, s.indexOf("\r",k));
                k = s.indexOf("DTSTART",i);
                c.startDate.setTime(dateFormat.parse(s.substring(s.indexOf(':', k) + 1, s.indexOf("\r", k)).replace("T", "") + " MSK"));
                k = s.indexOf("DTEND",i);
                c.endDate.setTime(dateFormat.parse(s.substring(s.indexOf(':', k) + 1, s.indexOf("\r", k)).replace("T", "") + " MSK"));
                c.mainPage = mainPage();
                c.deadLine = Utils.timeConsts.YEAR;
                contests.add(c);
            }
        }catch (ParseException e){
            e.printStackTrace();
        }

        return contests;
    }  */
}
