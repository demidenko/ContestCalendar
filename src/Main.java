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
import java.util.List;
import java.util.*;
import java.util.Timer;

/**
 * 17.12.12 20:27
 */
public class Main {
    
    static SiteParser parsers[] = {
            new CodeForcesParser(),
            new TopCoderParser(),
            new SnarkNewsContestsParser(),
            new ACMPParser(),
            new NEERCIFMOSchoolParser(),
            new CodeChefParser(),
            new GoogleCodeJamParser(),
            new RussianCodeCupParser(),
            new ICLParser(),
            new IPSCParser(),
            new UserContestsParser(),
    };
    
    
    
    public static void main(String[] args) {
        
        //System.exit(0);
        
        JFrame window = initWindow();
        window.setVisible(true);


        timerUpdateData = new Timer();
        timerUpdateData.schedule(new TimerTask() {
            public void run() {
                runParsers(parsers);
            }
        }, 0, 1000*60*60);
        

        timerUpdateTable = new Timer();
        timerUpdateTable.schedule(new TimerTask() {
            public void run() {
                synchronized (tableModel.contests){
                    //System.out.println("refresh"); System.out.flush();
                    tableModel.refresh();
                }
            }
        }, 0, 1000);
        
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
    static Timer timerUpdateTable;
    static Timer timerUpdateData;
    
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
