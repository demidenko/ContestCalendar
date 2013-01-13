import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 12.01.13 20:46
 */
public class Contest implements Comparable<Contest> {
    static SimpleDateFormat format = new SimpleDateFormat("EE. dd MMM yyyy. HH:mm");
    
    public String tittle;
    public String url;
    public String source;
    public Calendar startDate;
    public Calendar endDate;

    public Contest(){
        tittle = "";
        url = "";
        source = "";
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
    }

    public String toString(){
        return format.format(startDate.getTime())+" - "+ format.format(endDate.getTime())+" - "+tittle;
    }

    public int compareTo(Contest o) {
        int cmp = 0;
        if(cmp!=0) return cmp; else cmp = source.compareTo(o.source);
        if(cmp!=0) return cmp; else cmp = tittle.compareTo(o.tittle);
        return cmp;
    }
}
