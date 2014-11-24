import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class VKOSHPParser implements SiteParser{
    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy HH mm ss z");
    @Override
    public String contestsPage() {
        String s = Utils.URLToString("http://"+mainPage(), "UTF-8");
        if(s==null) return null;
        int i = s.indexOf("Сезоны:");
        if(i<0) return null;
        return "http://ikit.sfu-kras.ru"+s.substring(s.indexOf("a href=\"", i)+8, s.indexOf("\">",i));
    }

    @Override
    public String mainPage() {
        return "ikit.sfu-kras.ru/olimp_shkola";
    }

    @Override
    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();
        Main.writeln(contestsPage());
        String s = Utils.URLToString(contestsPage(), "UTF-8"); if(s==null) return contests;
        
        try{
            int i, j, k = s.indexOf("h3");
            String str, sp[];
            k = s.indexOf("center",k);
            i = s.indexOf("<b>", k);
            j = s.indexOf("</b>", k);
            Contest c = new Contest();
            c.title = Utils.trim(Utils.trimTags(s.substring(i + 3, j).replace("\n"," ")));
            i = s.indexOf("<p>", j);
            j = s.indexOf("</p>", i);
            str = Utils.trim(s.substring(i+3, j));
            sp = str.split(" ");
            for(int l=0; l<sp.length; ++l) if(sp[l].contains("–")){
                int ind = sp[l].indexOf('–');
                if(ind==-1) continue;
                c.startDate.setTime(dateFormat.parse(sp[l].substring(0, ind) + " " + Utils.month.get(sp[l + 1]) + " " + sp[l + 2] + " 00 00 00 KRAT"));
                c.endDate.setTime(dateFormat.parse(sp[l].substring(ind + 1) + " " + Utils.month.get(sp[l + 1]) + " " + sp[l + 2] + " 23 59 59 KRAT"));
                c.deadLine = Utils.timeConsts.YEAR;
                c.mainPage = mainPage();
                c.contestPage = contestsPage();
                contests.add(c);
                break;
            }
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return contests;
    }
}
