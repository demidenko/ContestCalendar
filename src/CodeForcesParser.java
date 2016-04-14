import java.util.ArrayList;


public class CodeForcesParser extends SiteParser{

    public String mainPage() {
        return "http://codeforces.com";
    }

    public String contestsPage() {
        return "http://codeforces.com/api/contest.list?gym=false&locale=ru";
    }

    public ArrayList<Contest> parse(){
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;
        
        try{
            int i = 0;
            for(;;){
                i = s.indexOf("\"name\"", i+1);
                if(i<0) break;
                int b = s.lastIndexOf('{', i);
                int e = s.indexOf('}',i);
                int k = s.indexOf("\"startTimeSeconds\"",b);
                if(!(k>b && k<e)) continue;
                Contest c = new Contest();
                long start = Long.parseLong(Utils.trim(s.substring(s.indexOf(':', k) + 1, s.indexOf(',', k))) + "000");
                k = s.indexOf("\"durationSeconds\"",b);
                long end = start + Long.parseLong(Utils.trim(s.substring(s.indexOf(':',k)+1,s.indexOf(',',k)))+"000");
                c.startDate.setTimeInMillis(start);
                c.endDate.setTimeInMillis(end);
                k = s.indexOf('\"', i+6);
                c.title = s.substring(k + 1, s.indexOf('\"', k + 1));
                c.mainPage = mainPage();
                k = s.indexOf("\"id\"", b);
                k = s.indexOf(':', k+1);
                c.contestPage = c.mainPage + "/contest/" + s.substring(k+1, s.indexOf(',',k+1));
                c.icon = getIcon();
                contests.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        
        return contests;
    }

    /*
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    }
    public String contestsPage() {
        return "http://codeforces.com/contests?complete=true&locale=ru";
    }

    public ArrayList<Contest> parse(){
        ArrayList<Contest> contests = new ArrayList<Contest>();
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;

        try{
            int i, j, k = s.indexOf("data-contestId="), end = s.indexOf("class=\"contests-table\"");
            String str, sp[];
            while(k<end){
                Contest c = new Contest();
                int id = Integer.parseInt(s.substring(k+16,s.indexOf("\"",k+16)));
                i = s.indexOf("<td>", k);
                j = s.indexOf("</td>", i);
                str = s.substring(i + 4, j);
                while((i=str.indexOf("<br"))>=0) str = str.substring(0,i);
                c.title = Utils.trim(Utils.replaceHTMLSymbols(str));
                j = s.indexOf("</td>", j+1);
                j = s.indexOf("<span", j+1);
                i = s.indexOf(">",j+1);
                j = s.indexOf("</",j+1);
                str = Utils.trim(s.substring(i + 1, j));
                try{
                    c.startDate.setTime(dateFormat.parse(str));
                }catch (ParseException e){
                    e.printStackTrace();
                    continue;
                }
                i = s.indexOf("<td>", j);
                j = s.indexOf("</td>", i);
                str = Utils.trim(s.substring(i + 4, j));
                if(str.indexOf(':')==str.lastIndexOf(':')) str="0:"+str;
                sp = str.split(":");
                c.endDate.setTime(c.startDate.getTime());
                c.endDate.add(Calendar.DAY_OF_YEAR, Integer.parseInt(sp[0]));
                c.endDate.add(Calendar.HOUR_OF_DAY, Integer.parseInt(sp[1]));
                c.endDate.add(Calendar.MINUTE, Integer.parseInt(sp[2]));
                if(c.startDate.compareTo(Utils.getNowDate())<=0) c.contestPage = "http://codeforces.ru/contest/" + id;
                else c.contestPage = "http://codeforces.ru/contestRegistration/" + id;
                c.mainPage = mainPage();
                if(!c.title.contains("Codeforces")) c.deadLine = Utils.timeConsts.YEAR;
                else c.deadLine = Utils.timeConsts.DAY*2;
                c.icon = getIcon();
                contests.add(c);
                k = s.indexOf("data-contestId=",k+1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contests;
    }
     */
}
