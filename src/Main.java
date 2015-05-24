import net.sf.image4j.codec.ico.ICODecoder;

import javax.imageio.ImageIO;
import javax.rmi.CORBA.Util;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * 17.12.12 20:27
 */
public class Main {
    static JFrame window;
    static MyTableModel tableModel;
//    static SystemTray systemTray = SystemTray.getSystemTray();
//    static TrayIcon trayIcon;
    static Timer timerUpdateTable;
    static Timer timerUpdateData;
    static Timer timerInfo;
    static JButton buttonUpdate;
    static JTextArea logText;
    static long updateTime = 1000l*60*60;
    
    static SiteParser allParsers[] = {
            new CodeForcesParser(),
            //new TopCoderParser(),
            new SnarkNewsSeriesParser(),
            new OpenCupParser(),
            new ACMPParser("acmp"),
            new ACMPParser("acmu"),
            new NEERCIFMOSchoolParser(),
            new CodeChefParser(),
            new GoogleCodeJamParser(),
            new ICLParser(),
            new IPSCParser(),
            new SIBSUIRegionalOlympiadParser(),
            new ACMQFParser(),
            new ACMWFParser(),
            new VKOSHPParser(),
            new YandexAlgorithmParser(),
            new TimusParser(),
            new UVaOJParser(),
            new HackerRankParser(),
            new DLGSUParser(),
            new COCIParser(),
            new UserContestsParser(),
            new SnarkNewsContestsParser(),
            new RussianCodeCupParser(),
            new ZaochParser(),
            new HackerEarthParser(),
            new IOIParser(),
            new USACOParser()
    };

    static SiteParser wishParsers[];
    
    
    
    public static void main(String[] args) {
        //System.exit(0);

        window = initWindow();
        window.setVisible(true);

        wishParsers = allParsers;
        
        runParsers(wishParsers);
        

        timerUpdateTable = new Timer();
        timerUpdateTable.schedule(new TimerTask() {
            public void run() {
                synchronized (tableModel.contests){
                    tableModel.refresh();
                }
            }
        }, 1000-System.currentTimeMillis()%1000, 1000);
    }


    static void icotest(){
        SiteParser parsers[] = allParsers;
        //parsers = new SiteParser[] {new IOIParser()};
        JFrame window = new JFrame();

        for(SiteParser p : parsers){
            write(Utils.shortURL(p.mainPage()),": ");
            BufferedImage icon = Utils.getFavIcon(p.mainPage());
            if(icon!=null) write(icon.getWidth(),'x',icon.getHeight());
            else write("null");
            writeln();
        }
        //HackerEarth - bad file ??
        window.setVisible(true);
    }

    static threadCounter counter = new threadCounter();
    static void runParsers(final SiteParser[] parsers){
        new Thread(){
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
        }.start();
    }
    
    public static class ParserThread extends Thread{
        SiteParser parser;
        public ParserThread(SiteParser p){
            parser = p;
        }
        public void run() {
            List<Contest> c = parser.parse();
            synchronized (counter){
                tableModel.addRows(c);
                counter.increase();
            }
            System.out.println(parser.getClass().getName()+" updated"); System.out.flush();
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
        final JFrame window = new JFrame("== ContestCalendar ==");
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
            e.printStackTrace();
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
        buttonUpdate.setFont(new Font(null, Font.BOLD, 11));
        //infoPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        //infoBigString.setBorder(BorderFactory.createLineBorder(Color.blue));
        //infoSmallString.setBorder(BorderFactory.createLineBorder(Color.blue));
        JLabel infoIcon = new JLabel();

        
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
                    infoIcon.setIcon(new ImageIcon(new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR)));
                    return;
                }
                Contest c = tableModel.getValueAtRow(row);

                infoBigString.setText(c.title);
                infoSmallString.setText(Utils.shortURL(c.mainPage));
                infoStrings[0] = c.contestPage;
                infoStrings[1] = c.mainPage;
                infoBigString.setCursor(Cursor.getPredefinedCursor(infoStrings[0].length() != 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                infoSmallString.setCursor(Cursor.getPredefinedCursor(infoStrings[1].length() != 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                infoIcon.setIcon(new ImageIcon(c.icon));

                int status = ((MyTableModel)table.getModel()).status(c, Utils.getNowDate());
                if(status<0) infoTimer.setText(""); else
                if(status==0)
                    infoTimer.setText("ends in "+Utils.differenceToString(Calendar.getInstance(), c.endDate));
                else
                    infoTimer.setText("starts in "+Utils.differenceToString(Calendar.getInstance(), c.startDate));

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
                Contest c = tableModel.getValueAtRow(row);
                int status = ((MyTableModel)table.getModel()).status(c, Utils.getNowDate());
                if(status<0) infoTimer.setText(""); else
                if(status==0)
                    infoTimer.setText("ends in "+Utils.differenceToString(Calendar.getInstance(), c.endDate));
                else
                    infoTimer.setText("starts in "+Utils.differenceToString(Calendar.getInstance(), c.startDate));
            }
        }, 1000-System.currentTimeMillis()%1000, 1000);



        final JTextField findText = new JTextField();
        findText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                changeFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                changeFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                changeFilter();
            }

            void changeFilter() {
                tableModel.filter = findText.getText().toLowerCase();
                synchronized (tableModel.contests) {
                    tableModel.needRefresh = true;
                    tableModel.refresh();
                }
            }
        });

        findText.setVisible(false);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MyKeyEventDispatcher(findText, KeyEvent.VK_F, KeyEvent.CTRL_MASK, KeyEvent.VK_ESCAPE, 0));

        logText = new JTextArea();
        logText.setEditable(false);
        System.setErr(new MyPrintStream(System.err, logText));
        logText.setVisible(false);
        JButton logClear = new JButton("clear");
        logClear.setVisible(false);
        logClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                logText.setText("");
            }
        });
        JButton logStats = new JButton("stats");
        logStats.setVisible(false);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MyKeyEventDispatcher(logText, KeyEvent.VK_L, KeyEvent.CTRL_MASK, KeyEvent.VK_L, KeyEvent.CTRL_MASK));
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MyKeyEventDispatcher(logClear, KeyEvent.VK_L, KeyEvent.CTRL_MASK, KeyEvent.VK_L, KeyEvent.CTRL_MASK));
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MyKeyEventDispatcher(logStats, KeyEvent.VK_L, KeyEvent.CTRL_MASK, KeyEvent.VK_L, KeyEvent.CTRL_MASK));


        infoPanel.add(findText, new GridBagConstraints(0,0,4,1,1,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,none,0,0));
        infoPanel.add(buttonUpdate, new GridBagConstraints(0,1,1,2,0,1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0));
        infoPanel.add(infoBigString, new GridBagConstraints(2,1,2,1,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0));
        infoPanel.add(infoSmallString, new GridBagConstraints(2,2,1,1,1,1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,1,0), 0,0));
        infoPanel.add(infoTimer, new GridBagConstraints(3,2,1,1,0,1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,5,1,5), 0,0));
        infoPanel.add(logText, new GridBagConstraints(1,3,3,2,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,1,5), 0,0));
        infoPanel.add(logClear, new GridBagConstraints(0,3,1,1,0,0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0));
        infoPanel.add(logStats, new GridBagConstraints(0,4,1,1,0,1, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0));
        infoPanel.add(infoIcon, new GridBagConstraints(1,1,1,2,0,1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0));

        window.add(new JScrollPane(table), BorderLayout.CENTER);
        window.add(infoPanel, BorderLayout.SOUTH);

        return window;
    }
    

    static void write(Object ... w){ for(Object x:w) System.out.print(x); System.out.flush(); }
    static void writeln(Object ... w){ for(Object x:w) System.out.print(x); System.out.println(); System.out.flush(); }
}
