import net.sf.image4j.codec.ico.ICODecoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.List;


public class Utils {
    static Calendar getNowDate(){
        Calendar c = Calendar.getInstance();
        return c;
    }
    
    static String whitespace = " \t\n\r\0";

    static String trim(String s){
        char c[] = s.toCharArray();
        int i=0, j=c.length-1;
        while(i<=j && whitespace.indexOf(c[i])>=0) ++i;
        while(i<=j && whitespace.indexOf(c[j])>=0) --j;
        s = new String(c, i, j-i+1);
        s = s.replace("\n","");
        s = s.replace("\r","");
        return s;
    }
    
    static String trimTags(String s){
        while(s.contains("<")) s = s.substring(0,s.indexOf("<"))+s.substring(s.indexOf(">")+1);
        return s;
    }
    
    static String replaceHTMLSymbols(String s){
        s = s.replace("&amp;", "&");
        s = s.replace("&nbsp;", " ");
        s = s.replace("&quot;", "\"");
        for(;;){
            int i = s.indexOf("&#");
            if(i<0) break;
            int j = s.indexOf(";",i+2);
            int code = Integer.parseInt(s.substring(i+2, j));
            s = s.replace(s.substring(i, j+1), ((char)code)+"");
        }
        for(;;){
            int i = s.indexOf("%");
            if(i<0) break;
            int j = i+3;
            int code = Integer.parseInt(s.substring(i+1, j), 16);
            s = s.replace(s.substring(i, j), ((char)code)+"");
        }
        return s;
    }

    static URLConnection getConnection(String urlName) throws Exception{
        URL url = new URL(urlName);
        URLConnection con = url.openConnection();
        con.addRequestProperty("User-Agent","parser ContestCalendar");
        con.setConnectTimeout(60000);
        return con;
    }

    static String URLToString(String urlName, String code){
        if(urlName==null) return null;
        try {
            URLConnection con;
            for(;;){
                con = getConnection(urlName);
                String loc = con.getHeaderField("Location");
                if(loc==null) break;
                urlName = loc;
            }
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

    static String shortURL(String url){
        if(url==null) return null;
        if(url.startsWith("http://")) url = url.substring(7);
        if(url.startsWith("https://")) url = url.substring(8);
        if(url.startsWith("www.")) url = url.substring(4);
        if(url.startsWith("/")) url = url.substring(1);
        if(url.endsWith("/")) url = url.substring(0,url.length()-1);
        return url;
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
                    uri = new URI(uriStr.indexOf("http://")==0 || uriStr.indexOf("https://")==0 ? uriStr : "http://" + uriStr);
                    desktop.browse(uri);
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }catch (URISyntaxException use){
                    use.printStackTrace();
                }
            }
        }
    }

    static BufferedImage getFavIcon(String url){
        String page = URLToString(url, "UTF-8");
        if(page==null) return null;
        int i = -1;
        i = page.toLowerCase().indexOf("rel=\"shortcut icon\"");
        if(i==-1) i = page.toLowerCase().indexOf("rel=\"icon\"");
        String iconUrl;
        if(i!=-1){
            int l = page.lastIndexOf('<',i);
            int r = page.indexOf('>', i);
            String str = page.substring(l, r + 1);
            i = str.toLowerCase().indexOf("href=");
            iconUrl = str.substring(i + 6, str.indexOf("\"", i + 6));
        }else{
            iconUrl = "/favicon.ico";
        }

        if(!iconUrl.startsWith("http")){
            if(iconUrl.startsWith("/")){
                URL address = null;
                try {
                    address = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                iconUrl = address.getProtocol() + "://" + address.getHost() + iconUrl;
            }else{
                iconUrl = url + iconUrl;
            }
        }

        return getImage(iconUrl);
    }

    public static BufferedImage getImage(String iconUrl){
        try {
            getConnection(iconUrl).getInputStream();
        } catch (Exception e){
            //e.printStackTrace();
            return null;
        }

        for(;;){
            String str = null;
            try {
                str = getConnection(iconUrl).getHeaderField("Location");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(str==null) break;
            iconUrl = str;
        }

        BufferedImage res = null;

        try {
            List<BufferedImage> imgs = ICODecoder.read(getConnection(iconUrl).getInputStream());
            for(BufferedImage img : imgs)
            if(res==null || res.getWidth()*res.getHeight()<img.getWidth()*img.getHeight()) res = img;
        } catch (Exception e1) {
            //e1.printStackTrace();
            try {
                res = ImageIO.read(getConnection(iconUrl).getInputStream());
            } catch (Exception e2) {
                //e2.printStackTrace();public
            }
        }

        return res;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
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
        month.put("january", "01");
        month.put("february", "02");
        month.put("march", "03");
        month.put("may", "04");
        month.put("april", "05");
        month.put("june", "06");
        month.put("july", "07");
        month.put("august", "08");
        month.put("september", "09");
        month.put("october", "10");
        month.put("november", "11");
        month.put("december", "12");
        month.put("jan", "01");
        month.put("feb", "02");
        month.put("mar", "03");
        month.put("may", "04");
        month.put("apr", "05");
        month.put("jun", "06");
        month.put("jul", "07");
        month.put("aug", "08");
        month.put("sep", "09");
        month.put("oct", "10");
        month.put("nov", "11");
        month.put("dec", "12");
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

    static Random rnd = new Random();
    static void shuffle(Object a[]){
        for(int i=a.length-1;i>0;--i){
            int j = rnd.nextInt(i+1);
            Object t = a[i];
            a[i] = a[j];
            a[j] = t;
        }
    }
}
