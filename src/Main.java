import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
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
    static MyTableModel tableModel;
//    static SystemTray systemTray = SystemTray.getSystemTray();
//    static TrayIcon trayIcon;
    static Timer timerUpdateTable;
    static Timer timerUpdateData;
    static Timer timerInfo;
    static JButton buttonUpdate;
    static long updateTime = 1000l*60*60;
    
    static SiteParser allParsers[] = {
            new CodeForcesParser(),
            new TopCoderParser(),
            new SnarkNewsContestsParser(),
            new OpenCupParser(),
            new ACMPParser(),
            new ACMUParser(),
            new NEERCIFMOSchoolParser(),
            new CodeChefParser(),
            new GoogleCodeJamParser(),
            new RussianCodeCupParser(),
            new ICLParser(),
            new IPSCParser(),
            new SIBSUIRegionalOlympiadParser(),
            new ACMQFParser(),
            new VKOSHPParser(),
            new YandexAlgorithmParser(),
            new TimusParser(),
            new UVaOJParser(),
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
        }, 1000-System.currentTimeMillis()%1000, 1000);
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
                        e.printStackTrace();
                    }
                }
                buttonUpdate.setText("Update");
                buttonUpdate.setEnabled(true);
                timerUpdateData = new Timer();
                timerUpdateData.schedule(new TimerTask() {
                    public void run() {
                        runParsers(wishParsers);
                    }
                }, updateTime-System.currentTimeMillis()%updateTime, updateTime);
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
            buttonUpdate.setText(0+"/"+count);
        }
        
        public void increase(){
            ++current;
            buttonUpdate.setText(current+"/"+count);
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
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);

        /*
        try {
            trayIcon = new TrayIcon(ImageIO.read(new File("ico.png")), "Contests schedule");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }      */
        
        /*trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.setVisible(true);
                window.setExtendedState(JFrame.NORMAL);
                window.setEnabled(true);
                systemTray.remove(trayIcon);
            }
        }); */
        
        /*window.addWindowListener(new WindowAdapter() {
            public void windowIconified(WindowEvent e) {
                window.setVisible(false);
                try {
                    systemTray.add(trayIcon);
                } catch (AWTException e1) {
                    e1.printStackTrace();
                }
            }

        }); */

        Insets none = new Insets(0,0,0,0);
        final JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());
        final JLabel infoBigString = new JLabel();
        final JLabel infoSmallString = new JLabel();
        final JLabel infoTimer = new JLabel();
        infoBigString.setFont(new Font(null, Font.BOLD, 14));
        infoSmallString.setFont(new Font(null, Font.ITALIC, 10));
        infoTimer.setFont(new Font(null, Font.PLAIN, 10));
        buttonUpdate  = new JButton("Update");
        //infoPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        //infoBigString.setBorder(BorderFactory.createLineBorder(Color.blue));
        //infoSmallString.setBorder(BorderFactory.createLineBorder(Color.blue));
        infoPanel.add(buttonUpdate, new GridBagConstraints(0,0,1,2,0,1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0));
        infoPanel.add(infoBigString, new GridBagConstraints(1,0,2,1,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0));
        infoPanel.add(infoSmallString, new GridBagConstraints(1,1,1,1,1,1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,1,0), 0,0));
        infoPanel.add(infoTimer, new GridBagConstraints(2,1,1,1,0,1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,5,1,5), 0,0));

        
        
        final String[] infoStrings = new String[]{"",""};

        buttonUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runParsers(wishParsers);
            }
        });
        
        infoBigString.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Utils.launchBrowser(infoStrings[0]);
            }

            public void mousePressed(MouseEvent e) {}

            public void mouseReleased(MouseEvent e) {}

            public void mouseEntered(MouseEvent e) {}

            public void mouseExited(MouseEvent e) {}
        });
        
        infoSmallString.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Utils.launchBrowser(infoStrings[1]);
            }

            public void mousePressed(MouseEvent e) {}

            public void mouseReleased(MouseEvent e) {}

            public void mouseEntered(MouseEvent e) {}

            public void mouseExited(MouseEvent e) {}
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e){
                int row = table.getSelectedRow();
                if(row==-1){
                    infoStrings[0] = infoStrings[1] = "";
                    infoBigString.setText("");
                    infoSmallString.setText("");
                    infoTimer.setText("");
                    return;
                }

                infoBigString.setText(tableModel.list.get(row).title);
                infoSmallString.setText(tableModel.list.get(row).mainPage);
                infoStrings[0] = tableModel.list.get(row).contestPage;
                infoStrings[1] = tableModel.list.get(row).mainPage;
                infoBigString.setCursor(Cursor.getPredefinedCursor(infoStrings[0].length() != 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                infoSmallString.setCursor(Cursor.getPredefinedCursor(infoStrings[1].length() != 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));

                int status = ((MyTableModel)table.getModel()).status(tableModel.list.get(row), Utils.getNowDate());
                if(status<0) infoTimer.setText(""); else
                if(status==0)
                    infoTimer.setText("ends in "+Utils.differenceToString(Calendar.getInstance(), tableModel.list.get(row).endDate));
                else
                    infoTimer.setText("starts in "+Utils.differenceToString(Calendar.getInstance(), tableModel.list.get(row).startDate));

                table.repaint();
            }
        });


        timerInfo = new Timer();
        timerInfo.schedule(new TimerTask() {
            public void run() {
                int row = table.getSelectedRow();
                if(row==-1){
                    infoTimer.setText("");
                    return;
                }
                int status = ((MyTableModel)table.getModel()).status(tableModel.list.get(row), Utils.getNowDate());
                if(status<0) infoTimer.setText(""); else
                if(status==0)
                    infoTimer.setText("ends in "+Utils.differenceToString(Calendar.getInstance(), tableModel.list.get(row).endDate));
                else
                    infoTimer.setText("starts in "+Utils.differenceToString(Calendar.getInstance(), tableModel.list.get(row).startDate));
            }
        }, 1000-System.currentTimeMillis()%1000, 1000);

        window.add(infoPanel, BorderLayout.SOUTH);
        window.add(new JScrollPane(table), BorderLayout.CENTER);
                
        return window;
    }
    
    
    

    static PrintWriter out = new PrintWriter(System.out);
    static Scanner in = new Scanner(System.in);
    static void write(Object ... w){ for(Object x:w) out.print(x); out.flush(); }
    static void writeln(Object ... w){ for(Object x:w) out.print(x); out.println(); out.flush(); }
}
