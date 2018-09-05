package org.tuomilabs.heart.core;

import org.tuomilabs.heart.logging.Logger;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Logger.setLevel(Logger.LEVEL_DEBUG);

        new Main().run();
    }

    private void run() throws IOException {
        IPWebcamViewer viewer = new IPWebcamViewer(1, "192.168.77.4", 25522);

        viewer.initialize();

        viewer.grabFrame();
        viewer.saveFrame(1);
    }
}
