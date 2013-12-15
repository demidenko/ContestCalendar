import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Contest implements Comparable<Contest> {
    final static SimpleDateFormat format = new SimpleDateFormat("EE. dd MMM yyyy. HH:mm");
    
    public String title;
    public String contestPage;
    public String mainPage;
    public Calendar startDate;
    public Calendar endDate;
    public long deadLine;

    public Contest(){
        title = "";
        contestPage = "";
        mainPage = "";
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        deadLine = Utils.timeConsts.DAY;
    }

    public String toString(){
        return format.format(startDate.getTime())+" - "+ format.format(endDate.getTime())+" - "+ title;
    }

    public int compareTo(Contest o) {
        int cmp = mainPage.compareTo(o.mainPage);
        if(cmp!=0) return cmp; else cmp = title.compareTo(o.title);
        if(cmp!=0) return cmp; else cmp = startDate.compareTo(o.startDate);
        return cmp;
    }
}
