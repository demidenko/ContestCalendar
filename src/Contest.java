import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Contest implements Comparable<Contest> {
    final static SimpleDateFormat dateFormat = new SimpleDateFormat("EE. dd MMM yyyy. HH:mm");
    
    public String title;
    public String contestPage;
    public String mainPage;
    public Calendar startDate;
    public Calendar endDate;
    public long deadLine;
    public BufferedImage icon;

    public Contest(){
        title = "";
        contestPage = "";
        mainPage = "";
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        deadLine = Utils.timeConsts.DAY;
        icon = SiteParser.defaultIcon;
    }

    public String toString(){
        return dateFormat.format(startDate.getTime())+" - "+ dateFormat.format(endDate.getTime())+" - " + title + "["+contestPage+"]";
    }

    public int compareTo(Contest o) {
        int cmp = mainPage.compareTo(o.mainPage);
        if(cmp!=0) return cmp; else cmp = title.compareTo(o.title);
        if(cmp!=0) return cmp; else cmp = startDate.compareTo(o.startDate);
        return cmp;
    }
}
