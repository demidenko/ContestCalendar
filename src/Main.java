import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
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
    static MyTableModel tableModel;
    static SystemTray systemTray = SystemTray.getSystemTray();
    static TrayIcon trayIcon;
    static Timer timerUpdateTable;
    static Timer timerUpdateData;
    static JButton buttonUpdate;
    static long updateTime = 1000*60*60;
    
    static SiteParser allParsers[] = {
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

    static SiteParser wishParsers[];
    
    
    
    public static void main(String[] args) {
        
        //System.exit(0);
        
        JFrame window = initWindow();
        window.setVisible(true);

        wishParsers = allParsers;
        
        runParsers(wishParsers);
        

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
    
    static threadCounter counter = new threadCounter();
    static void runParsers(final SiteParser[] parsers){
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.print("parse... ");
                if(timerUpdateData!=null) timerUpdateData.cancel();
                buttonUpdate.setEnabled(false);
                counter.init(parsers.length);
                for(SiteParser parser : parsers){
                    Thread t = new ParserThread(parser);
                    t.start();
                }
                synchronized (counter){
                    try {
                        counter.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                buttonUpdate.setEnabled(true);
                timerUpdateData = new Timer();
                timerUpdateData.schedule(new TimerTask() {
                    public void run() {
                        runParsers(wishParsers);
                    }
                }, updateTime, updateTime);
                System.out.println("done. ");
            }
        }).start();
    }
    
    public static class ParserThread extends Thread{
        SiteParser parser;
        public ParserThread(SiteParser p){
            parser = p;
        }
        public void run() {
            List<Contest> c = parser.parse();
            tableModel.addRows(c);
            synchronized (counter){
                counter.increase();
            }
            //System.out.println("update "+parser.getClass().getName()); System.out.flush();
        }
    }
    
    public static class threadCounter{
        int count, current;
        
        public void init(int count){
            this.count = count;
            current = 0;
        }
        
        public void increase(){
            ++current;
            if(current==count) notifyAll();
        }
    }
    
    
    
    public static final JFrame initWindow(){
        final JFrame window = new JFrame("== calendar ==");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(600, 305);
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
        buttonUpdate  = new JButton("Update");
       // infoPanel.setBorder(BorderFactory.createLineBorder(Color.red));
       // info1.setBorder(BorderFactory.createLineBorder(Color.blue));
       // info2.setBorder(BorderFactory.createLineBorder(Color.blue));
        infoPanel.add(buttonUpdate, new GridBagConstraints(0,0,1,2,0,1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        infoPanel.add(info1, new GridBagConstraints(1,0,1,1,1,1,GridBagConstraints.CENTER, GridBagConstraints.BOTH, none, 0, 0));
        infoPanel.add(info2, new GridBagConstraints(1,1,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,none, 0,0));

        final String[] str = new String[]{"",""};

        buttonUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runParsers(wishParsers);
            }
        });
        
        info1.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Utils.launchBrowser(str[0]);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        
        info2.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Utils.launchBrowser(str[1]);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int[] rows = table.getSelectedRows();
                if(rows.length==0){
                    str[0] = str[1] = "";
                    info1.setText("");
                    info2.setText("");
                    return;
                }
                int row = rows[0];
                info1.setText(tableModel.list.get(row).title);
                info2.setText(tableModel.list.get(row).mainPage);
                
                str[0] = tableModel.list.get(row).contestPage;
                str[1] = tableModel.list.get(row).mainPage;
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
