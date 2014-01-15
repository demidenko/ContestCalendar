import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by demich on 1/12/14.
 */
public class MyKeyEventDispatcher implements KeyEventDispatcher {
    private JTextField textField;
    public MyKeyEventDispatcher(JTextField field){
        textField = field;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        boolean needRepaint = false;
        if(keyEvent.getKeyCode()==KeyEvent.VK_ESCAPE){
            textField.setVisible(false);
            needRepaint = true;
        }else
        if(keyEvent.getKeyCode()==KeyEvent.VK_F && (keyEvent.getModifiers()&KeyEvent.CTRL_MASK)!=0){
            textField.setVisible(true);
            textField.requestFocus();
            needRepaint = true;
        }
        if(needRepaint){
            Main.window.validate();
            Main.window.repaint();
        }
        return false;
    }
}
