import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class TopCoderParser extends SiteParser {
    static final int timeOfSRM = 75 + 5 + 15;
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ENGLISH);

    public String contestsPage() {
        String res = "https://clients6.google.com/calendar/v3/calendars/appirio.com_bhga3musitat85mhdrng9035jg@group.calendar.google.com/events?key=AIzaSyBNlYH01_9Hc5S1J9vuFmu2nUqBZJNAXxs";
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        int d = c.get(Calendar.DAY_OF_MONTH);
        int m = c.get(Calendar.MONTH)+1;
        int y = c.get(Calendar.YEAR);
        res+="&timeMin="+String.format("%04d-%02d-%02dT00:00:00Z", y, m, d);
        c.add(Calendar.MONTH, 8);
        d = c.get(Calendar.DAY_OF_MONTH);
        m = c.get(Calendar.MONTH)+1;
        y = c.get(Calendar.YEAR);
        res+="&timeMax="+String.format("%04d-%02d-%02dT00:00:00Z", y, m, d);
        return res;
    }

    @Override
    public String mainPage() {
        return "https://www.topcoder.com/my-dashboard/";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return null;


        try{
            int i = s.indexOf("\"items\""), k, p;
            for(;;){
                i = s.indexOf("\"summary\"", i+1);
                if(i==-1) break;
                k = s.indexOf(':', i);
                k = s.indexOf('\"',k);
                String t = s.substring(k+1, s.indexOf('\"',k+1));
                if(!t.contains("SRM") && !t.contains("TCO")) continue;
                Contest c = new Contest();
                c.title = t;
                if(t.contains("SRM")) c.deadLine = Utils.timeConsts.DAY*2;
                else if(t.contains("TCO")) c.deadLine = Utils.timeConsts.YEAR;
                c.mainPage = mainPage();
                k = s.indexOf("\"start\"", i);
                k = s.indexOf("\"dateTime\"", k);
                k = s.indexOf(':', k); k = s.indexOf('\"',k);
                t = s.substring(k+1, s.indexOf('\"',k+1));
                p = t.indexOf('T');
                t = t.substring(0, p) + " " + t.substring(p+1, p+9) + " GMT" + t.substring(p+9);
                try{
                    c.startDate.setTime(dateFormat.parse(t));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                k = s.indexOf("\"end\"", i);
                k = s.indexOf("\"dateTime\"", k);
                k = s.indexOf(':', k); k = s.indexOf('\"',k);
                t = s.substring(k+1, s.indexOf('\"',k+1));
                p = t.indexOf('T');
                t = t.substring(0, p) + " " + t.substring(p+1, p+9) + " GMT" + t.substring(p+9);
                try{
                    c.endDate.setTime(dateFormat.parse(t));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                c.icon = getIcon();
                contests.add(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }


    /*
    public BufferedImage getIcon(){
        return Utils.getImage("https://www.topcoder.com/images/favicon.ico");
    }

    public String contestsPage() {
        Calendar c = Calendar.getInstance();
        int m = c.get(Calendar.MONTH)+1;
        int y = c.get(Calendar.YEAR);
        int mBefore = m, yBefore = y;
        if(m==1){
            mBefore = 12;
            yBefore--;
        }else mBefore--;
        return "https://calendar.google.com/calendar/htmlembed?src=appirio.com_bhga3musitat85mhdrng9035jg@group.calendar.google.com" + "&dates="+String.format("%04d%02d%02d/%04d%02d%02d",yBefore,mBefore,1,y,m,1);
    }

    @Override
    public String mainPage() {
        return "https://www.topcoder.com";
        //return "http://community.topcoder.com/tc";
    }



    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;
        try{
            for(int cnt=0;s!=null && cnt<5;++cnt){
                int i = 0;
                for(;;){
                    i = s.indexOf("event-link",i+1);
                    if(i==-1) break;
                    int k = s.indexOf("event-summary",i);
                    Contest c = new Contest();
                    c.title = Utils.trimTags(s.substring(s.lastIndexOf("<",k), s.indexOf("</",k)));
                    if(c.title.contains("SRM")) c.deadLine = Utils.timeConsts.DAY*2;
                    else if(c.title.contains("TCO")) c.deadLine = Utils.timeConsts.YEAR;
                    else continue;
                    k = s.indexOf("href=",i);
                    String link = "https://www.google.com/calendar/" + s.substring(k+6, s.indexOf("\"",k+6));
                    String t = Utils.URLToString(link, "UTF-8"), str;
                    k = t.indexOf("\"startDate\"");
                    k = t.indexOf("datetime", k);
                    k = t.indexOf("\"", k);
                    str = t.substring(k+1, t.indexOf("\"",k+1)).replaceAll("[TZ]","") + " GMT";
                    try {
                        c.startDate.setTime(dateFormat.parse(str));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        continue;
                    }
                    k = t.indexOf("\"endDate\"");
                    k = t.indexOf("datetime", k);
                    k = t.indexOf("\"", k);
                    str = t.substring(k+1, t.indexOf("\"",k+1)).replaceAll("[TZ]","") + " GMT";
                    try {
                        c.endDate.setTime(dateFormat.parse(str));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        continue;
                    }
                    if((k = t.indexOf("\"_blank\""))!=-1){
                        k = t.lastIndexOf("\"",k-1);
                        str = t.substring(t.lastIndexOf("\"",k-1)+1,k);
                        str = str.substring(str.indexOf("q=")+2);
                        c.contestPage = Utils.replaceHTMLSymbols(str);
                    }
                    c.mainPage = mainPage();
                    c.icon = getIcon();
                    contests.add(c);
                }
                i = s.indexOf("btn_next");
                i = s.lastIndexOf("href=",i);
                i = s.indexOf("\"",i);
                s = Utils.replaceHTMLSymbols(s.substring(i+1,s.indexOf("\"",i+1)));
                s = Utils.URLToString(s, "UTF-8");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return contests;
    }
     */

    /*static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm Z", Locale.ENGLISH);
    
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
                        for(String z : sp) if(z.matches("[0-9]?[0-9]:[0-9][0-9]")){
                            c.startDate.setTime(dateFormat.parse(day + " " + t + " " + z + " EDT"));
                            break;
                        }
                        c.endDate.setTime(c.startDate.getTime());
                        c.endDate.add(Calendar.MINUTE, timeOfSRM);
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
    */
}
