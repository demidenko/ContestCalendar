import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 13.01.13 13:31
 */
public class MyTableModel extends AbstractTableModel {
    public ArrayList<Contest> list;
    public TreeSet<Contest> contests;
    private final Comparator<Contest> comparatorTime = new Comparator<Contest>() {
        @Override
        public int compare(Contest o1, Contest o2) {
            int cmp = 0;
            if(cmp!=0) return cmp; else cmp = o1.startDate.compareTo(o2.startDate);
            if(cmp!=0) return cmp; else cmp = o1.endDate.compareTo(o2.endDate);
            return cmp;
        }
    };
    
    public MyTableModel(){
        
        contests = new TreeSet<Contest>();
        list = new ArrayList<Contest>();
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Contest c = list.get(rowIndex);
        return c;
    }

    @Override
    public String getColumnName(int c) {
        switch (c) {
            case 0:
                return "start";
            case 1:
                return "end";
            case 2:
                return "contest";
            default:
                return "";
        }
    }
    /*
     
     */

    public void refresh(){
        final Calendar nowDate = Utils.getNowDate();
        List<Contest> toRemove = new ArrayList<Contest>();
        for(Contest c : contests) 
            if(nowDate.getTimeInMillis()-c.endDate.getTimeInMillis()>1000*60*60*24) toRemove.add(c);
        contests.removeAll(toRemove);
        list = new ArrayList<Contest>(contests);
        Collections.sort(list, new Comparator<Contest>() {
            public int compare(Contest o1, Contest o2) {
                int cmp1 = o1.endDate.compareTo(nowDate)<0 ? -1 : (o1.startDate.compareTo(nowDate)<=0 ? 0 : 1);
                int cmp2 = o2.endDate.compareTo(nowDate)<0 ? -1 : (o2.startDate.compareTo(nowDate)<=0 ? 0 : 1);
                if(cmp1!=cmp2) return Integer.signum(cmp1-cmp2);
                return comparatorTime.compare(o1, o2);
            }
        });
        fireTableDataChanged();
    }

    public void addRows(Collection<Contest> newContests){
        synchronized (contests){
            for(Contest c : newContests){
                if(contests.contains(c)) contests.remove(c);
                contests.add(c);
            }
        }
    }
}
