import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 14.01.13 19:04
 */
public class UserContestsParser implements SiteParser {
    static final SimpleDateFormat frm = new SimpleDateFormat("dd.MM.yyyy HH:mm Z");
    
    public String contestsPage() {
        return "UserContests.txt";
    }

    public String mainPage() {
        return "";
    }

    public ArrayList<Contest> parse() {
        ArrayList<Contest> contests = new ArrayList<Contest>();

        try {
            Scanner in = new Scanner(new File(contestsPage()));
            while(in.hasNextLine()){
                Contest c = new Contest();
                c.title = in.nextLine();
                c.startDate.setTime(frm.parse(in.nextLine()));
                c.endDate.setTime(frm.parse(in.nextLine()));
                c.mainPage = mainPage();
                contests.add(c);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return contests;
    }
}
