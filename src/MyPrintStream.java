import javax.swing.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;


class MyPrintStream extends PrintStream {
    JTextArea text;
    SimpleDateFormat dateFormat;

    public MyPrintStream(OutputStream out, JTextArea logArea) {
        super(out);
        text = logArea;
        dateFormat = new SimpleDateFormat("HH:mm:ss");
    }

    public void println(Object x){
        super.println(x);
        if(x instanceof Exception){
            Exception e = (Exception)x;

            for(StackTraceElement d : e.getStackTrace())
            if(d.getMethodName().equals("parse") && d.getClassName().endsWith("Parser")){
                text.append(dateFormat.format(Utils.getNowDate().getTime())+" "+d.getClassName()+" "+e+"\n");
            }
        }
    }
}
