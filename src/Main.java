import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

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
        final JTable table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, cellRenderer);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        
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

        Insets none = new Insets(0,0,0,0);
        final JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());
        final JLabel info1 = new JLabel(); 
        final JLabel info2 = new JLabel(); 
        info1.setFont(new Font(null, Font.BOLD, 14));
        info2.setFont(new Font(null, Font.ITALIC, 10));
       // infoPanel.setBorder(BorderFactory.createLineBorder(Color.red));
       // info1.setBorder(BorderFactory.createLineBorder(Color.blue));
       // info2.setBorder(BorderFactory.createLineBorder(Color.blue));
        infoPanel.add(info1, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, none, 0, 0));
        infoPanel.add(info2, new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,none, 0,0));
        
        
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int[] rows = table.getSelectedRows();
                if(rows.length==0) {
                    info1.setText("");
                    info2.setText("");
                    return;
                }
                int row = rows[0];
                info1.setText(tableModel.list.get(row).title);
                info2.setText(tableModel.list.get(row).mainPage);
            }
        });
        
        window.add(infoPanel, BorderLayout.SOUTH);
        window.add(new JScrollPane(table), BorderLayout.CENTER);
                
        return window;
    }
    
    
    

    static PrintWriter out = new PrintWriter(System.out);
    static Scanner in = new Scanner(System.in);
    static void write(Object ... w){ for(Object x:w) out.print(x); out.flush(); }
    static void writeln(Object ... w){ for(Object x:w) out.print(x); out.println(); out.flush(); }
}
