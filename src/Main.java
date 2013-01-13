import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
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
    public static void main(String[] args) {
        JFrame window = initWindow();
        window.setVisible(true);

        SiteParser parsers[] = {
            new CodeForcesParser(),        
            new SnarkNewsContestsParser(),        
            new TopCoderParser(),        
            //new GoogleCodeJamParser(),        
        };
        
        for(SiteParser parser : parsers){
            List<Contest> list = parser.parse();
            table.addRows(list);
        }
        
        /*Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_MONTH, -1);
        date.add(Calendar.SECOND, 5);
        Contest c1 = new Contest();
        c1.startDate = c1.endDate = date;
        c1.tittle = "test contest #1";
        table.addRow(c1);

        date = Calendar.getInstance();
        date.add(Calendar.SECOND, 10);
        Contest c2 = new Contest();
        c2.startDate = date;
        date = Calendar.getInstance();
        date.add(Calendar.SECOND, 15);
        c2.endDate = date;
        c2.tittle = "test contest #2";
        table.addRow(c2);*/
        
        
        
        Timer timerUpdateTable = new Timer();
        timerUpdateTable.schedule(new TimerTask() {
            public void run() {
                table.refresh();
            }
        }, 1000, 1000);
    }
    
    
    static MyTableModel table;
    static SystemTray systemTray = SystemTray.getSystemTray();
    static TrayIcon trayIcon;
    
    public static final JFrame initWindow(){
        final JFrame window = new JFrame("=== calendar ===");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(600, 300);
        window.setLocationRelativeTo(null);

        MyTableCellRenderer cellRenderer = new MyTableCellRenderer();
        table = new MyTableModel();
        JTable t = new JTable(table);
        
        t.setDefaultRenderer(Object.class, cellRenderer);
        window.add(new JScrollPane(t));

        try {
            trayIcon = new TrayIcon(ImageIO.read(new File("ico.png")), "Contests schedule");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        
        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.setState(JFrame.NORMAL);
                window.setVisible(true);
                window.requestFocus();
                systemTray.remove(trayIcon);
            }
        });
        
        window.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                if(e.getNewState()==JFrame.ICONIFIED){
                    window.setVisible(false);
                    try {
                        systemTray.add(trayIcon);
                    } catch (AWTException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
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
