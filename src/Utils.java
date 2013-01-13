import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

/**
 * 12.01.13 21:05
 */
public class Utils {
    static String whitespace = " \t\n\r\0";

    static String trim(String s){
        char c[] = s.toCharArray();
        int i=0, j=s.length()-1;
        while(i<=j && whitespace.indexOf(c[i])>=0) ++i;
        while(i<=j && whitespace.indexOf(c[j])>=0) --j;
        return new String(c, i, j-i+1);
    }
    
    static String URLToString(String urlName, String code){
        try {
            URL url = new URL(urlName);
            URLConnection con = url.openConnection();
            DataInputStream dis = new DataInputStream(con.getInputStream());
            byte bytes[] = new byte[1<<18];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for(;;){
                int cnt = dis.read(bytes);
                if(cnt==-1) break;
                baos.write(bytes, 0, cnt);
            }
            return new String(baos.toByteArray(), code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    static Calendar sum(Calendar c1, Calendar c2){
        Calendar res = Calendar.getInstance();
        res.set(
                c1.get(Calendar.YEAR)+c2.get(Calendar.YEAR)-1970,
                c1.get(Calendar.MONTH)+c2.get(Calendar.MONTH),
                c1.get(Calendar.DAY_OF_MONTH)+c2.get(Calendar.DAY_OF_MONTH)-1,
                c1.get(Calendar.HOUR_OF_DAY)+c2.get(Calendar.HOUR_OF_DAY),
                c1.get(Calendar.MINUTE)+c2.get(Calendar.MINUTE),
                c1.get(Calendar.SECOND)+c2.get(Calendar.SECOND)
                
                );
        return res;
    }
}
