import javax.swing.*;
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
        Calendar nowDate = Calendar.getInstance();
        
        
        if(column==0){
            cell.setText(Contest.format.format(c.startDate.getTime()));
        }else
        if(column==1){
            cell.setText(Contest.format.format(c.endDate.getTime()));
        }else
        if(column==2){
            cell.setText(c.tittle);
        }
        
        cell.setOpaque(true);
        cell.setForeground(Color.black);
        cell.setBackground(new Color(224, 240, 255));
        if(nowDate.compareTo(c.endDate)>0){
            cell.setForeground(new Color(96, 96, 96));
        }else
        if(nowDate.compareTo(c.startDate)>=0){
            cell.setBackground(new Color(0, 224, 0));
        }
        
        return cell;
    }
}
