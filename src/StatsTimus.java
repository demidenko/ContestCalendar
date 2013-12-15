import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: demich
 * Date: 01.05.13
 * Time: 20:53
 * To change this template use File | Settings | File Templates.
 */
public class StatsTimus{
    static PrintWriter out = new PrintWriter(System.out);
    static Scanner in = new Scanner(System.in);
    static void write(Object ... w){ for(Object x:w) out.print(x); out.flush(); }
    static void writeln(Object ... w){ for(Object x:w) out.print(x); out.println(); out.flush(); }

    public static void main(String[] args) {
        getProblems();
        String id = "74435";//in.next();
        runStats(id);
    }

    static void runStats(String id){
        //http://acm.timus.ru/status.aspx?author=74435&status=accepted&count=1000
        String url = "http://acm.timus.ru/status.aspx?author="+id+"&status=accepted&count=1000";
        TreeMap<Integer, Date> map = new TreeMap<Integer, Date>();
        SimpleDateFormat frm = new SimpleDateFormat("HH:mm:ss dd MMMM yyyy", Locale.US);
        for(;;){
            String s = Utils.URLToString(url, "UTF-8");
            int i = s.indexOf("Memory used"), k = s.indexOf("<TD class=\"status_footer\" colspan=\"9\">");
            for(;;){
                i = s.indexOf("<TD", i+1);
                if(i==-1 || i>=k) break;
                i = s.indexOf("<TD", i+1);
                String str = s.substring(s.indexOf("<NOBR>", i), s.indexOf("</TD",i));
                str = str.replace("<NOBR>", "").replace("</NOBR>", " ").replace("<BR>","");//.toUpperCase();
                Date date = null;
                try {
                    date = frm.parse(str);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
                i = s.indexOf("<TD", i+1);
                i = s.indexOf("<TD", i+1);
                i = s.indexOf("<A", i+1);
                int problem = -1;
                try{
                    problem = Integer.parseInt(s.substring(s.indexOf("\">",i)+2, s.indexOf("<",i+1)));
                }catch (NumberFormatException e){

                }
                i = s.indexOf("<TD", i+1);
                i = s.indexOf("<TD", i+1);
                i = s.indexOf("<TD", i+1);
                i = s.indexOf("<TD", i+1);
                i = s.indexOf("<TD", i+1);
                if(problem!=-1){
                    map.put(problem, date);
                    //writeln(problem);
                }
            }
            k = s.indexOf("script type");
            i = s.indexOf("class=\"footer_right\"");
            i = s.indexOf("<A HREF=", i+1);
            if(i==-1 || i>=k) break;
            if(s.substring(s.indexOf("\">",i+1)+2,s.indexOf("</A",i+1)).equals("To the top")) break;
            url = "http://acm.timus.ru/"+s.substring(i+9, s.indexOf("\">", i+1)).replace("&amp;","&")+"&count=1000";
           // writeln(url);
        }

        long dif[][] = new long[3000][12];
        int cnt[][] = new int[3000][12];
        Calendar c = Calendar.getInstance();
        for(int p : map.keySet()){
            c.setTime(map.get(p));
            int m = c.get(Calendar.MONTH);
            int y = c.get(Calendar.YEAR);
            dif[y][m]+=problems[p].difficulty;
            cnt[y][m]+=1;
        }

        int mxi=-1, mxj=-1;
        for(int y=0;y<dif.length;++y)
        for(int m=0;m<dif[y].length;++m){
            if(mxi==-1 || dif[mxi][mxj]<dif[y][m]){ mxi=y; mxj=m; }
        }
        writeln("Best month (difficulty) = ", mxj+1,'.',mxi, " (", dif[mxi][mxj],")");

        mxi=-1; mxj=-1;
        for(int y=0;y<cnt.length;++y)
            for(int m=0;m<cnt[y].length;++m){
                if(mxi==-1 || cnt[mxi][mxj]<cnt[y][m]){ mxi=y; mxj=m; }
            }
        writeln("Best month (count) = ", mxj+1,'.',mxi, " (", cnt[mxi][mxj],")");
    }

    static Problem problems[] = new Problem[65536];
    public static void getProblems(){
        String s = Utils.URLToString(
                "http://acm.timus.ru/problemset.aspx?space=1&page=all&locale=en",
                "UTF-8"
        );

        int i = s.indexOf("Difficulty"), k = s.indexOf("<TR CLASS=\"navigation\">");
        k = s.indexOf("<TR CLASS=\"navigation\">", k+1);
        for(;;){
            i = s.indexOf("<TD", i+1);
            if(i==-1 || i>k) break;
            i = s.indexOf("<TD", i+1);
            Problem p = new Problem();
            p.id = Integer.parseInt(s.substring(i+4, s.indexOf("</TD",i)));
            i = s.indexOf("<TD", i + 1);
            i = s.indexOf("<A", i + 1);
            p.name = s.substring(s.indexOf("\">", i) + 2, s.indexOf("</A", i));
            i = s.indexOf("<TD", i + 1);
            i = s.indexOf("<TD", i+1);
            i = s.indexOf("<TD", i+1);
            p.difficulty = Integer.parseInt(s.substring(i + 4, s.indexOf("</TD", i)));
            problems[p.id] = p;
        }
    }

    public static class Problem implements Comparable<Problem>{
        int id;
        String name;
        int difficulty;

        @Override
        public int compareTo(Problem o) {
            return Integer.signum(id-o.id);
        }
    }
}
