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
    
    static final Font fontTime = new Font("Monospaced", Font.BOLD, 12);
    static final Font fontTextPlain = new Font(null, Font.PLAIN, 12);
    static final Font fontTextBold = new Font(null, Font.BOLD, 12);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel cell = new JLabel();
        Contest c = (Contest) value;
        Calendar nowDate = Utils.getNowDate();
        int status = ((MyTableModel)table.getModel()).status(c, nowDate);
        
        if(column==0){
            cell.setText(Contest.format.format(c.startDate.getTime()));
        }else
        if(column==1){
            cell.setText(Contest.format.format(c.endDate.getTime()));
        }else
        if(column==2){
            cell.setText(c.title);
        }
        
        cell.setOpaque(true);
        cell.setForeground(Color.black);
        cell.setBackground(new Color(224, 224, 255));
        
        if(status==-1){
            cell.setForeground(new Color(96, 96, 96));
        }else
        if(status==0){
            cell.setBackground(new Color(0, 224, 0));
        }
        
        if(isSelected){
            cell.setBorder(new MatteBorder(1, 0, 1, 0, new Color(226,32,32)));
        }



        if(column==0 || column==1) cell.setFont(fontTime);
        else{
            if(status>=0 && status<=1) cell.setFont(fontTextBold);
            else cell.setFont(fontTextPlain);
        }
        
        return cell;
    }
}
