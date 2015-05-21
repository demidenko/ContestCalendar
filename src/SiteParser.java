import java.awt.image.BufferedImage;
import java.util.ArrayList;


public abstract class SiteParser{
    public BufferedImage icon = null;
    public BufferedImage getIcon(){
        if(icon!=null) return icon;
        icon = Utils.getFavIcon(mainPage());
        if(icon==null){
            icon = new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR);
        }else{
            icon = Utils.resize(icon,(int)Math.round(16.0*icon.getWidth()/icon.getHeight()),16);
        }
        return icon;
    }

    public abstract String contestsPage();
    
    public abstract String mainPage();

    public abstract ArrayList<Contest> parse();
}
