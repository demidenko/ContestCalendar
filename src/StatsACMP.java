import java.util.*;

/**
 * Created by demich on 14.05.16.
 */
public class StatsACMP {
    public static void main(String[] args) {
        bestProgressRegionals("40569", "40455");
    }

    public static void bestProgressRegionals(String id_now, String id_before){
        String ss[] = new String[]{
                Utils.URLToString("http://acmp.ru/asp/champ/index.asp?main=rating&id_stage="+id_before, "windows-1251"),
                "",
                Utils.URLToString("http://acmp.ru/asp/champ/index.asp?main=rating&id_stage="+id_now, "windows-1251")
        };
        TreeMap<String, Integer> q = new TreeMap<>();
        TreeMap<String, Integer> qb = new TreeMap<>();
        for(int k=-1;k<2;k+=2){
            String s = ss[k+1];
            int i = s.indexOf("<h1>");
            int end = s.indexOf("<center>", i);
            for(;;){
                i = s.indexOf("<tr class=white", i+1);
                if(i==-1 || i>end) break;
                i = s.indexOf("<td", i+1);
                i = s.indexOf("<td", i+1);
                String name = s.substring(s.indexOf('>',i+1)+1, s.indexOf("</td",i+1));
                name = Utils.trim(name);
                name = name.replaceAll("[^А-Яа-яёЁ]+", " ");
                for(int j=0;j<11;++j) i = s.indexOf("<td", i+1);
                String score_s = s.substring(s.indexOf('>',i+1)+1, s.indexOf("</td",i+1));
                int score = Integer.parseInt(Utils.trim(Utils.trimTags(score_s)));
                if(!q.containsKey(name)) q.put(name, 0);
                q.put(name, q.get(name)+score*k);
                if(!qb.containsKey(name)) qb.put(name, 0);
                qb.put(name, qb.get(name)|(1<<(k+1)));
            }
        }
        ArrayList<String> t = new ArrayList<>();
        for(String name : q.keySet()) if(qb.get(name) == 5) t.add(name);
        Collections.sort(t, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return q.get(o2) - q.get(o1);
            }
        });

        for(String name : t) Main.writeln(name,": ", q.get(name));
    }
}
