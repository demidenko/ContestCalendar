import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Calendar;

/**
 * 13.01.13 14:00
 */
public class MyTableCellRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel cell = new JLabel();
        Contest c = (Contest) value;
        Calendar nowDate = Utils.getNowDate();
        
        
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
        int status = ((MyTableModel)table.getModel()).status(c, nowDate);
        if(status==-1){
            cell.setForeground(new Color(96, 96, 96));
        }else
        if(status==0){
            cell.setBackground(new Color(0, 224, 0));
        }
        
        if(isSelected){
            cell.setBorder(new MatteBorder(1, 0, 1, 0, new Color(226,32,32)));
        }
        
        return cell;
    }
}
