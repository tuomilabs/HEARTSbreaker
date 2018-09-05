package org.tuomilabs.heart.core;

import org.tuomilabs.heart.logging.Logger;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.MissingResourceException;

public class IPWebcamViewer {
    private int id;
    private String ip;
    private int port;
    private String imageURL;
    private URL imageURLAsURL;
    private String savePath = System.getProperty("user.dir");

    BufferedImage currentFrame;


    public IPWebcamViewer(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.imageURL = "";
    }

    public void initialize() throws IOException {
        // Get the image url from the ip and port
        imageURL = "http://" + ip + ":" + port + "/photoaf.jpg";


        URL imageURLAsURL;
        try {
            imageURLAsURL = new URL(imageURL);
        } catch (MalformedURLException e) {
            throw new MalformedURLException("URL " + imageURL + " is not a valid URL. Check the IP and port number and make sure they're valid!");
        }
        this.imageURLAsURL = imageURLAsURL;

        boolean imageURLExists;
        try {
            imageURLExists = checkImageURLExists(imageURLAsURL);
        } catch (IOException e) {
            throw new IOException("Unable to open an HTTP connection to the IP Webcam. Check the IP Webcam options and try again.");
        }

        if (!imageURLExists) {
            throw new IOException("Unable to get the IP Webcam image. Check if " + imageURL + " works in a web browser, as well as the IP Webcam settings.");
        }

        // If we're still here, initialization was successful
        Logger.info("Initialization of IPWebcamViewer." + id + " successful.");
    }

    private boolean checkImageURLExists(URL u) throws IOException {
        HttpURLConnection huc = (HttpURLConnection) u.openConnection();
        huc.setRequestMethod("GET");
        huc.connect();
        int code = huc.getResponseCode();
        System.out.println(code);

        return code == 200;
    }

    public void grabFrame() throws IOException {
        currentFrame = ImageIO.read(imageURLAsURL);
    }

    public void saveFrame(int id) throws IOException {
        File outputfile = new File(savePath + "\\saved_frame_" + id + ".jpg");
        ImageIO.write(currentFrame, "jpg", outputfile);
    }
}
