import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Calendar;

/**
 * 13.01.13 14:00
 */
public class MyTableCellRenderer implements TableCellRenderer {
    static final Font[] fonts = new Font[]{
            new Font("Consolas", Font.BOLD, 12),
            new Font("Courier New", Font.BOLD, 12),
            new Font("DialogInput", Font.BOLD, 12),
            new Font("Monospaced", Font.BOLD, 12),
            new Font(null, Font.BOLD, 12),
    };
    
    static final Font fontTime = new Font("Monospaced", Font.PLAIN, 12);
    static final Font fontTimeBold = new Font("Monospaced", Font.BOLD, 12);
    static final Font fontTextPlain = new Font(null, Font.PLAIN, 12);
    static final Font fontTextBold = new Font(null, Font.BOLD, 12);

    static final Color foregroundDefault = new Color(0,0,0);
    static final Color foregroundPast = new Color(128,128,128);
    static final Color foregroundCrossed = new Color(200,0,0);
    static final Color backgroundDefault = new Color(225,225,255);
    static final Color backgroundNow = new Color(0,215,0);

    static MatteBorder selectBorder = new MatteBorder(1, 0, 1, 0, new Color(255,64,0));


    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel cell = new JLabel();
        Contest c = (Contest) value;
        Calendar nowDate = Utils.getNowDate();
        int status = ((MyTableModel)table.getModel()).status(c, nowDate);
        
        if(column==0){
            cell.setText(Contest.dateFormat.format(c.startDate.getTime()));
        }else
        if(column==1){
            cell.setText(Contest.dateFormat.format(c.endDate.getTime()));
        }else
        if(column==2){
            cell.setText(c.title);
        }
        
        cell.setOpaque(true);
        Color foreground = foregroundDefault;
        Color background = backgroundDefault;

        if(status==-1){
            foreground = foregroundPast;
        }else
        if(status==0){
            background = backgroundNow;
        }

        if(isSelected){
            cell.setBorder(selectBorder);
        }else
        if(column==2 && status>=0 && table.getSelectedRow()!=-1){
            Contest selectedContest = (Contest) table.getValueAt(table.getSelectedRow(), 0);
            long s1 = c.startDate.getTimeInMillis();
            long s2 = selectedContest.startDate.getTimeInMillis();
            long e1 = c.endDate.getTimeInMillis();
            long e2 = selectedContest.endDate.getTimeInMillis();
            if(Math.max(e1,e2)-Math.min(s1,s2)<=e1-s1+e2-s2) foreground = foregroundCrossed;
        }

        if(column==0 || column==1){
            if(status==0) cell.setFont(fontTimeBold);
            else cell.setFont(fontTime);
        }else{
            if(status>=0 && status<=1) cell.setFont(fontTextBold);
            else cell.setFont(fontTextPlain);
        }

        cell.setForeground(foreground);
        cell.setBackground(background);
        
        return cell;
    }
}
