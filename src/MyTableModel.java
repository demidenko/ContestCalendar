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
    
    static final long tooFarTime = 1000l*60*60*24;
    
    public static int status(Contest c, Calendar date){
        if(c.endDate.compareTo(date)<=0){
            if(Utils.difference(date, c.endDate)>tooFarTime) return -2;
            return -1;
        }
        if(c.startDate.compareTo(date)<=0) return 0;
        if(Utils.difference(c.startDate, date)<=tooFarTime) return 1;
        return 2;
    }

    public boolean needRefresh;
    public void refresh(){
        final Calendar nowDate = Utils.getNowDate();
        
        List<Contest> toRemove = new ArrayList<Contest>();
        for(Contest c : contests) if(status(c, nowDate)<-1) toRemove.add(c);
        if(toRemove.size()>0){
            contests.removeAll(toRemove);
            needRefresh = true;
        }
        
        if(!needRefresh)
        for(int i=0;i<list.size();++i){
            if(status(list.get(i), nowDate)!=oldStatus.get(i)){
                needRefresh = true;
                break;
            }
            if(oldStatus.get(i)>1) break;
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

    public void addRows(List<Contest> newContests){
        synchronized (contests){
            if(newContests==null || newContests.size()==0) return;
            String source = newContests.get(0).mainPage;
            Calendar nowDate = Utils.getNowDate();
            for(Contest c : new ArrayList<Contest>(contests)) 
                if(c.mainPage==source && status(c, nowDate)>=0) contests.remove(c);
            for(Contest c : newContests) if(status(c, nowDate)>=-1){
                contests.add(c);
                needRefresh = true;
            }
        }
    }
}
