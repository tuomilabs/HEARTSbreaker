package org.tuomilabs.heart.webcamtests;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WebcamTest {
    public static void main(String[] args) throws IOException {
        Webcam webcam = Webcam.getWebcams().get(1);
        webcam.open();
        BufferedImage image = webcam.getImage();
        ImageIO.write(image, "JPG", new File("test.jpg"));
    }
}
