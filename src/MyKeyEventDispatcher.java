import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by demich on 1/12/14.
 */
public class MyKeyEventDispatcher implements KeyEventDispatcher {
    private JComponent component;
    private int openCode, openMask;
    private int closeCode, closeMask;
    public MyKeyEventDispatcher(JComponent field, int openCode, int openMask, int closeCode, int closeMask){
        component = field;
        this.openCode = openCode;
        this.openMask = openMask;
        this.closeCode = closeCode;
        this.closeMask = closeMask;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        boolean needRepaint = false;
        if(keyEvent.getID()!=KeyEvent.KEY_RELEASED) return false;
        if(component.isVisible() && keyEvent.getKeyCode()==closeCode && (keyEvent.getModifiers()&closeMask)==closeMask){
            component.setVisible(false);
            needRepaint = true;
        }else
        if(!component.isVisible() && keyEvent.getKeyCode()==openCode && (keyEvent.getModifiers()&openMask)==openMask){
            component.setVisible(true);
            component.requestFocus();
            needRepaint = true;
        }
        if(needRepaint){
            component.repaint();
            Main.window.validate();
            Main.window.repaint();
        }
        return false;
    }
}
