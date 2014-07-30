import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.TreeMap;


public class Utils {
    static Calendar getNowDate(){
        Calendar c = Calendar.getInstance();
        //c.add(Calendar.HOUR_OF_DAY, 1);
        return c;
    }
    
    static String whitespace = " \t\n\r\0";

    static String trim(String s){
        char c[] = s.toCharArray();
        int i=0, j=c.length-1;
        while(i<=j && whitespace.indexOf(c[i])>=0) ++i;
        while(i<=j && whitespace.indexOf(c[j])>=0) --j;
        return new String(c, i, j-i+1);
    }
    
    static String trimTags(String s){
        while(s.contains("<")) s = s.substring(0,s.indexOf("<"))+s.substring(s.indexOf(">")+1);
        return s;
    }
    
    static String replaceHTMLSymbols(String s){
        for(;;){
            int i = s.indexOf("&#");
            if(i<0) break;
            int j = s.indexOf(";",i+2);
            int code = Integer.parseInt(s.substring(i+2, j));
            s = s.replace(s.substring(i, j+1), ((char)code)+"");
        }
        return s;
    }
    
    static String URLToString(String urlName, String code){
        if(urlName==null) return null;
        try {
            URL url = new URL(urlName);
            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            byte bytes[] = new byte[1<<17];
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
    
    static BufferedImage loadImage(String url){
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void launchBrowser(String uriStr){
        if(uriStr==null || uriStr.length()==0) return;
        uriStr = trim(uriStr);
        Desktop desktop;
        if (Desktop.isDesktopSupported()){
            desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)){
                URI uri;
                try{
                    uri = new URI(uriStr.indexOf("http://")==0 ? uriStr : "http://" + uriStr);
                    desktop.browse(uri);
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }catch (URISyntaxException use){
                    use.printStackTrace();
                }
            }
        }
    }
    
    static long difference(Calendar c1, Calendar c2){
        return c1.getTimeInMillis() - c2.getTimeInMillis();
    }


    static String differenceToString(Calendar c1, Calendar c2){
        long diff = Math.abs(difference(c1,c2));
        String res = "";
        long h = diff/timeConsts.HOUR;
        diff%=timeConsts.HOUR;
        long m = diff/timeConsts.MINUTE;
        diff%=timeConsts.MINUTE;
        long s = diff/timeConsts.SECOND;

        if(h>48) res+=(h/24)+" "+"days";
        else res += h+":"+(m/10)+(m%10)+":"+(s/10)+(s%10);

        return res;
    }
    
    static Calendar sum(Calendar c1, Calendar c2){
        Calendar res = Calendar.getInstance();
        res.setTimeInMillis(c1.getTimeInMillis());
        res.add(Calendar.SECOND, c2.get(Calendar.SECOND));
        res.add(Calendar.MINUTE, c2.get(Calendar.MINUTE));
        res.add(Calendar.HOUR_OF_DAY, c2.get(Calendar.HOUR_OF_DAY));
        res.add(Calendar.DAY_OF_YEAR, c2.get(Calendar.DAY_OF_YEAR));
        res.add(Calendar.YEAR, c2.get(Calendar.YEAR)-1970);
        return res;
    }

    static String timeInfo(Calendar c, int cnt){
        String res = "";
        if(c.get(Calendar.YEAR)!=0){ res+=c.get(Calendar.YEAR)+" years"; --cnt; } if(cnt==0) return res;
        if(c.get(Calendar.MONTH)!=0){ res+=c.get(Calendar.MONTH)+" months"; --cnt; } if(cnt==0) return res;
        if(c.get(Calendar.DAY_OF_MONTH)!=0){ res+=c.get(Calendar.DAY_OF_MONTH)+" days"; --cnt; } if(cnt==0) return res;
        if(c.get(Calendar.HOUR_OF_DAY)!=0){ res+=c.get(Calendar.HOUR_OF_DAY)+" hours"; --cnt; } if(cnt==0) return res;
        if(c.get(Calendar.MINUTE)!=0){ res+=c.get(Calendar.MINUTE)+" min."; --cnt; } if(cnt==0) return res;
        if(c.get(Calendar.SECOND)!=0){ res+=c.get(Calendar.SECOND)+" sec."; --cnt; } if(cnt==0) return res;
        return res;
    }

    static TreeMap<String, String> month = new TreeMap<String,String>();
    static{
        month.put("январь", "01");
        month.put("января", "01");
        month.put("февраль", "02");
        month.put("февраля", "02");
        month.put("март", "03");
        month.put("марта", "03");
        month.put("апрель", "04");
        month.put("апреля", "04");
        month.put("май", "05");
        month.put("мая", "05");
        month.put("июнь", "06");
        month.put("июня", "06");
        month.put("июль", "07");
        month.put("июля", "07");
        month.put("август", "08");
        month.put("августа", "08");
        month.put("сентябрь", "09");
        month.put("сентября", "09");
        month.put("октябрь", "10");
        month.put("октября", "10");
        month.put("ноябрь", "11");
        month.put("ноября", "11");
        month.put("декабрь", "12");
        month.put("декабря", "12");
    }

    static class timeConsts{
        static final long SECOND = 1000l;
        static final long MINUTE = SECOND*60;
        static final long HOUR = MINUTE*60;
        static final long DAY = HOUR*24;
        static final long WEEK = DAY*7;
        static final long YEAR = DAY*365;
    }

    static String replaceMonth(String s){
        for(String x : month.keySet()){
            if(s.contains(x)) s=s.replace(x, month.get(x));
        }
        return s;
    }
}
