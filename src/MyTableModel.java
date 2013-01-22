import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 13.01.13 13:31
 */
public class MyTableModel extends AbstractTableModel {
    public ArrayList<Contest> list;
    public TreeSet<Contest> contests;
    public ArrayList<Integer> oldStatus;
    
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
        oldStatus = new ArrayList<Integer>();
        needRefresh = true;
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
    
    static final int tooFarTime = 1000*60*60*24;
    static boolean isTooFar(Contest c, Calendar date){
        if(date.getTimeInMillis()-c.endDate.getTimeInMillis()<tooFarTime) return false;
        return true;
    }
    
    public static int status(Contest c, Calendar date){
        if(c.endDate.compareTo(date)<=0) return -1;
        if(c.startDate.compareTo(date)<=0) return 0;
        return 1;
    }

    public boolean needRefresh;
    public void refresh(){
        final Calendar nowDate = Utils.getNowDate();
        
        List<Contest> toRemove = new ArrayList<Contest>();
        for(Contest c : contests) if(isTooFar(c, nowDate)) toRemove.add(c);
        if(toRemove.size()>0){
            contests.removeAll(toRemove);
            needRefresh = true;
        }
        
        if(!needRefresh)
        for(int i=0;i<list.size();++i)
            if(status(list.get(i), nowDate)!=oldStatus.get(i)){
                needRefresh = true;
                break;
            }

        if(needRefresh){
            list = new ArrayList<Contest>(contests);
            Collections.sort(list, new Comparator<Contest>() {
                public int compare(Contest o1, Contest o2) {
                    int cmp1 = status(o1, nowDate);
                    int cmp2 = status(o2, nowDate);
                    if(cmp1!=cmp2) return Integer.signum(cmp1-cmp2);
                    return comparatorTime.compare(o1, o2);
                }
            });
            oldStatus = new ArrayList<Integer>();
            for(Contest c : list) oldStatus.add(status(c, nowDate));
            fireTableDataChanged();
            needRefresh = false;
        }
    }

    public void addRows(Collection<Contest> newContests){
        synchronized (contests){
            for(Contest c : newContests){
                if(contests.contains(c)) contests.remove(c);
                contests.add(c);
            }
            needRefresh = true;
        }
    }
}
