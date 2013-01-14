import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * 17.12.12 20:27
 */
public class Main {
    
    static SiteParser parsers[] = {
            new CodeForcesParser(),
            new SnarkNewsContestsParser(),
            new TopCoderParser(),
            new GoogleCodeJamParser(),
            new RussianCodeCupParser(),
            new ICLParser(),
            new CodeChefParser(),
            new UserContestsParser(),
    };
    
    
    static void trash(){
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {

                long tm  =System.currentTimeMillis();
                System.out.println("start...");System.out.flush();
                Random r = new Random();
                double sum = 0;
                double its = 1e7;
                for(int i=0;i<its;++i) sum+=Math.acos((r.nextDouble() * 2 - 1));
                System.out.println(String.format("%.3f",sum/its)+" "+(System.currentTimeMillis()-tm));
                System.out.flush();
            }
        }, 1000, 1000);

        String s;
        do{
            s = in.next();
        }while(!s.equals("stop"));
        System.exit(0);
    }
    
    public static void main(String[] args) {
        //trash();
        
        JFrame window = initWindow();
        window.setVisible(true);



        timerUpdateTable = new Timer();
        timerUpdateTable.schedule(new TimerTask() {
            public void run() {
                synchronized (tableModel.contests){
                    //System.out.println("refresh"); System.out.flush();
                    tableModel.refresh();
                }
            }
        }, 0, 1000);
        

        runParsers(parsers);
        
        /*Calendar date = getNowDate;
        date.add(Calendar.DAY_OF_MONTH, -1);
        date.add(Calendar.SECOND, 5);
        Contest c1 = new Contest();
        c1.startDate = c1.endDate = date;
        c1.tittle = "test contest #1";
        tableModel.addRow(c1);

        date = getNowDate;
        date.add(Calendar.SECOND, 10);
        Contest c2 = new Contest();
        c2.startDate = date;
        date = getNowDate();
        date.add(Calendar.SECOND, 15);
        c2.endDate = date;
        c2.tittle = "test contest #2";
        tableModel.addRow(c2);*/

        
    }
    
    static void runParsers(SiteParser[] parsers){
        for(SiteParser parser : parsers){
            Thread t = new ParserThread(parser);
            t.start();
        }
    }
    
    public static class ParserThread extends Thread{
        SiteParser parser;
        public ParserThread(SiteParser p){
            parser = p;
        }
        public void run() {
            List<Contest> c = parser.parse();
            tableModel.addRows(c);
            //System.out.println("update "+parser.getClass().getName()); System.out.flush();
        }
    }
    
    static MyTableModel tableModel;
    static SystemTray systemTray = SystemTray.getSystemTray();
    static TrayIcon trayIcon;
    static Timer timerUpdateTable = new Timer();
    static Timer timerUpdateData = new Timer();
    
    public static final JFrame initWindow(){
        final JFrame window = new JFrame("== calendar ==");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(600, 300);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());
        
        MyTableCellRenderer cellRenderer = new MyTableCellRenderer();
        tableModel = new MyTableModel();
        JTable t = new JTable(tableModel);
        
        t.setDefaultRenderer(Object.class, cellRenderer);
        window.add(new JScrollPane(t), BorderLayout.CENTER);
        
        try {
            trayIcon = new TrayIcon(ImageIO.read(new File("ico.png")), "Contests schedule");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        
        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.setVisible(true);
                window.setExtendedState(JFrame.NORMAL);
                window.setEnabled(true);
                systemTray.remove(trayIcon);
            }
        });
        
        window.addWindowListener(new WindowAdapter() {
            public void windowIconified(WindowEvent e) {
                window.setVisible(false);
                try {
                    systemTray.add(trayIcon);
                } catch (AWTException e1) {
                    e1.printStackTrace();
                }
            }

        });

        return window;
    }
    
    
    

    static PrintWriter out = new PrintWriter(System.out);
    static Scanner in = new Scanner(System.in);
    static void write(Object ... w){ for(Object x:w) out.print(x); out.flush(); }
    static void writeln(Object ... w){ for(Object x:w) out.print(x); out.println(); out.flush(); }
}
