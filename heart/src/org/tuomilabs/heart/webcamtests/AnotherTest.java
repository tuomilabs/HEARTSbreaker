package org.tuomilabs.heart.webcamtests;


import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.VideoInputFrameGrabber;

public class AnotherTest implements Runnable {
    //final int INTERVAL=1000;///you may use interval
    opencv_core.IplImage image;
    CanvasFrame canvas = new CanvasFrame("Web Cam");

    public AnotherTest() {
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        FrameGrabber grabber = new VideoInputFrameGrabber(1); // 1 for next camera
        int i = 0;
        try {
            grabber.start();
            Frame img;
            while (true) {
                img = grabber.grab();
                if (img != null) {
                    canvas.showImage(img);
                }
                //Thread.sleep(INTERVAL);
            }
        } catch (Exception e) {
        }
    }


    public static void main(String[] args) {
        AnotherTest gs = new AnotherTest();
        Thread th = new Thread(gs);
        th.start();
    }
}
