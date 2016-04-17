import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;


public class UserContestsParser extends SiteParser {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm Z");
    
    public String contestsPage() {
        return "usercontests.txt";
    }

    public String mainPage() {
        return null;
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();


        Scanner in = null;
        try {
            in = new Scanner(new File(contestsPage()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        try {
            Contest c = null;
            while(in.hasNext()){
                String tag = in.next();
                if(tag.equalsIgnoreCase("start")){
                    if(c!=null) contests.add(c);
                    c = new Contest();
                    c.deadLine = Utils.timeConsts.YEAR;
                    c.mainPage = contestsPage();
                }
                if(tag.equalsIgnoreCase("title")) c.title = Utils.trim(in.nextLine());
                if(tag.equalsIgnoreCase("mainpage")) c.mainPage = Utils.trim(in.nextLine());
                if(tag.equalsIgnoreCase("contestpage")) c.contestPage = Utils.trim(in.nextLine());
                if(tag.equalsIgnoreCase("page")) c.contestPage = c.mainPage = Utils.trim(in.nextLine());
                try{
                    if(tag.equalsIgnoreCase("from")) c.startDate.setTime(dateFormat.parse(Utils.trim(in.nextLine())));
                    if(tag.equalsIgnoreCase("to")) c.endDate.setTime(dateFormat.parse(Utils.trim(in.nextLine())));
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }
            if(c!=null) contests.add(c);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contests;
    }
}
