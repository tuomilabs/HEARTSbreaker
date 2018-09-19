package org.tuomilabs.heart.arduino;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendData {
    boolean scanFinished = false;
    String hc05Url =
            "btspp://201610094738:1;authenticate=false;encrypt=false;master=false"; //Replace this with your bluetooth URL

    public static void main(String[] args) {
        try {
            new SendData().go();
        } catch (Exception ex) {
            Logger.getLogger(SendData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void go() throws Exception {
        StreamConnection streamConnection = (StreamConnection)
                Connector.open(hc05Url);
        OutputStream os = streamConnection.openOutputStream();
        InputStream is = streamConnection.openInputStream();
        os.write("1".getBytes()); //'1' means ON and '0' means OFF
        os.close();
        byte[] b = new byte[200];
        Thread.sleep(200);
        is.read(b);
        is.close();
        streamConnection.close();
        System.out.println("received " + new String(b));
    }
}