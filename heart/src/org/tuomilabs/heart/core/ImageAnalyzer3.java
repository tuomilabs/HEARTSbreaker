package org.tuomilabs.heart.core;

import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.ConnectRule;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import com.jhlabs.image.PosterizeFilter;
import georegression.struct.point.Point2D_I32;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImageAnalyzer3 {
    // Create a DisplayPanel to show many images in one window
    private static ListDisplayPanel gui = new ListDisplayPanel();

    /**
     * Crops an image.
     *
     * @param src  - the source image
     * @param rect - a rectangle, defining the region to crop
     * @return - the cropped image
     */
    private static BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        return src.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * Threshold the image, using intermediate posterization.
     *
     * @param inputImage - the input image
     * @return - the thresholded image
     * @throws IOException
     */
    private static BufferedImage threshold(BufferedImage inputImage) throws IOException {
        // STEP 1: Posterise the image

        BufferedImage image = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        PosterizeFilter k = new PosterizeFilter();
        k.setNumLevels(2);
        k.filter(inputImage, image);


        // STEP 2: Convert to BoofCV format
        GrayF32 input = ConvertBufferedImage.convertFromSingle(image, null, GrayF32.class);
        GrayU8 binary = new GrayU8(input.width, input.height);


        // STEP 3: Threshold image
        GThresholdImageOps.threshold(input, binary, GThresholdImageOps.computeOtsu(input, 0, 255), true);
        return VisualizeBinaryData.renderBinary(binary, false, null);
    }


    public static void main(String[] args) throws IOException {
//        System.out.println(getDirection(new int[]{1698, 474}, new int[]{1699, 473}));
//        System.out.println(getDirection(new int[]{1699, 473}, new int[]{1706, 473}));
//        System.out.println(getDirection(new int[]{1706, 473}, new int[]{1710, 474}));

//        if (true) {
//            System.exit(12);
//        }



        BufferedImage original = ImageIO.read(new File("C:\\development\\HEARTSbreaker\\heart\\saved_frame_82.jpg"));
        BufferedImage croppedImage = cropImage(original, new Rectangle(700, 1300, 2500, 1300));
        BufferedImage thresholded = threshold(croppedImage);

        gui.addImage(original, "Original");
        gui.addImage(croppedImage, "Cropped");
        gui.addImage(thresholded, "Thresholded");
        ShowImages.showWindow(gui, "Cool Stuff", true);


        GrayU8 gray = ConvertBufferedImage.convertFrom(thresholded, (GrayU8) null);
        GrayU8 edgeImage = gray.createSameShape();

        // Create a canny edge detector which will dynamically compute the threshold based on maximum edge intensity
        // It has also been configured to save the trace as a graph.  This is the graph created while performing
        // hysteresis thresholding.
        CannyEdge<GrayU8, GrayS16> canny = FactoryEdgeDetectors.canny(2, true, true, GrayU8.class, GrayS16.class);

        // The edge image is actually an optional parameter.  If you don't need it just pass in null
        canny.process(gray, 0.1f, 0.3f, edgeImage);

        // First get the contour created by canny
        List<EdgeContour> edgeContours = canny.getContours();
        // The 'edgeContours' is a tree graph that can be difficult to process.  An alternative is to extract
        // the contours from the binary image, which will produce a single loop for each connected cluster of pixels.
        // Note that you are only interested in external contours.
        List<Contour> contours = BinaryImageOps.contour(edgeImage, ConnectRule.EIGHT, null);
        List<Contour> realContours = new ArrayList<>();

        for (Contour c : contours) {
//            System.out.println(c.external.size());

            if (c.external.size() > 1800 && c.external.size() < 1900) {
                System.out.println(c.external.size());
                realContours.add(c);
            }
        }

        System.out.println(realContours.size());


        for (Contour r : realContours) {
            System.out.println("Getting corners!");
            List<int[]> corners = getCorners(r, 20);

            System.out.println("Corners: ");
            for (int[] corner : corners) {
                System.out.print(Arrays.toString(corner));
            }

            System.out.println();
        }

        // display the results
        BufferedImage visualBinary = VisualizeBinaryData.renderBinary(edgeImage, false, null);
        BufferedImage visualCannyContour = VisualizeBinaryData.renderContours(edgeContours, null,
                gray.width, gray.height, null);
        BufferedImage visualEdgeContour = new BufferedImage(gray.width, gray.height, BufferedImage.TYPE_INT_RGB);
        VisualizeBinaryData.render(realContours, Color.RED, visualEdgeContour);

        ListDisplayPanel panel = new ListDisplayPanel();
        panel.addImage(visualBinary, "Binary Edges from Canny");
        panel.addImage(visualCannyContour, "Canny Trace Graph");
        panel.addImage(visualEdgeContour, "Contour from Canny Binary");
        ShowImages.showWindow(panel, "Canny Edge", true);
    }

    private static List<int[]> getCorners(Contour contour, int tolerance) {
        tolerance = (int) (((double)tolerance / 100) * ((double)contour.external.size()));

        List<int[]> nePoints = new ArrayList<>();
        List<int[]> sePoints = new ArrayList<>();
        List<int[]> swPoints = new ArrayList<>();
        List<int[]> nwPoints = new ArrayList<>();


        int direction = getStartingDirection(contour);
        int errors = 0;
        int changes = 0;

        System.out.println("Starting direction: " + direction);

        int startIndex = getIndexOfLeftmostPoint(contour);
        Point2D_I32 prev = contour.external.get(startIndex);
        for (int i = startIndex + 1; i < contour.external.size() + startIndex; i++) {
//            if (changes > 10000) {
//                return new ArrayList<>();
//            }

            Point2D_I32 p = contour.external.get(i % contour.external.size());


            // If the number of errors exceeded the tolerance, we probably changed direction.
            // Update direction and reset number of errors to zero.
            if (errors > tolerance) {
//                System.out.println("Direction changed.");
                direction += whichWay(prev, p, direction);
                direction += 4;
                direction %= 4;
                changes++;
                errors = 0;
            }


            System.out.println("Current point: " + p + "; Current direction: " + direction + " (moving: " + getDirection(prev, p) + ") with error " + errors + "/" + tolerance);
//            System.out.println(p.getX() + ", " + p.getY());


            switch (direction) {
                case 0:
                    if (getDirection(prev, p) == 0) {
                        nePoints.add(new int[]{p.getX(), p.getY()});
                    } else {
//                        System.out.println("Switching from " + direction + " to " + getDirection(prev, p) + "!");
                        errors++;
                    }
                    break;
                case 1:
                    if (getDirection(prev, p) == 1) {
                        sePoints.add(new int[]{p.getX(), p.getY()});
                    } else {
//                        System.out.println("Switching from " + direction + " to " + getDirection(prev, p) + "!");
                        errors++;
                    }
                    break;
                case 2:
                    if (getDirection(prev, p) == 2) {
                        nwPoints.add(new int[]{p.getX(), p.getY()});
                    } else {
//                        System.out.println("Switching from " + direction + " to " + getDirection(prev, p) + "!");
                        errors++;
                    }
                    break;
                case 3:
                    if (getDirection(prev, p) == 3) {
                        swPoints.add(new int[]{p.getX(), p.getY()});
                    } else {
//                        System.out.println("Switching from " + direction + " to " + getDirection(prev, p) + "!");
                        errors++;
                    }
                    break;
            }

            prev = p;
        }


        if (changes < 4) {
            System.err.println("Only " + changes + " direction changes!");
            return new ArrayList<>();
        } else if (changes > 4) {
            System.err.println("Too many:  " + changes + " direction changes!");
            return new ArrayList<>();

        }


        List<int[]> corners = new ArrayList<>();
        corners.add(nePoints.get(nePoints.size() - 1));
        corners.add(sePoints.get(sePoints.size() - 1));
        corners.add(swPoints.get(swPoints.size() - 1));
        corners.add(nwPoints.get(nwPoints.size() - 1));

        return corners;
    }

    private static int getDirection(Point2D_I32 prev, Point2D_I32 p) {
        return getDirection(new int[]{prev.getX(), prev.getY()}, new int[]{p.getX(), p.getY()});
    }

    private static int whichWay(Point2D_I32 prev, Point2D_I32 p, int currentDirection) {
        int direction = getDirection(new int[]{prev.getX(), prev.getY()}, new int[]{p.getX(), p.getY()});

        if (direction == currentDirection - 1) {
            return -1;
        } else if (direction == currentDirection + 1) {
            return 1;
        } else {
            return 0;
        }
    }

    private static int getStartingDirection(Contour contour) {
        int samples = 15;

        int firstIndex = getIndexOfLeftmostPoint(contour);
        Point2D_I32 firstPoint = contour.external.get(firstIndex);

        List<Integer> directions = new ArrayList<>();

        // Generate some random next points at an index distance from 10 to 100, and get which direction they're in
        for (int i = 0; i < samples; i++) {
            Point2D_I32 nextPoint = contour.external
                    .get(i + ThreadLocalRandom.current().nextInt(10, 100 + 1));

            directions.add(getDirection(new int[]{firstPoint.getX(), firstPoint.getY()}, new int[]{nextPoint.getX(), nextPoint.getY()}));
        }

        return mode(directions);
    }

    /**
     * Gets the mode of a list of numbers. Based on https://stackoverflow.com/a/4191729
     * @param numbers - the list of numbers
     * @return - the mode
     */
    private static int mode(final List<Integer> numbers) {
        final Map<Integer, Long> countFrequencies = numbers.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        final long maxFrequency = countFrequencies.values().stream()
                .mapToLong(count -> count)
                .max().orElse(-1);

        return countFrequencies.entrySet().stream()
                .filter(tuple -> tuple.getValue() == maxFrequency)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .get(0);
    }

    // Gets the direction from p1 to p2.
    private static int getDirection(int[] p1, int[] p2) {
        if (p2[0] >= p1[0] && p2[1] >= p1[1]) {
            return 0;
        } else if (p2[0] >= p1[0] && p2[1] <= p1[1]) {
//            System.out.println(p2[0] + " >= " + p1[0] + " and " + p2[1] + " <= " + p1[1]);
            return 1;
        } else if (p2[0] <= p1[0] && p2[1] <= p1[1]) {
            return 2;
        } else if (p2[0] <= p1[0] && p2[1] >= p1[1]) {
            return 3;
        }

        System.err.println("Direction from " + Arrays.toString(p1) + " to " + Arrays.toString(p2) + " is undefined.");

        return -1;
    }

    private static int getIndexOfLeftmostPoint(Contour contour) {
//        try {
//            System.setOut(new PrintStream(new File("out.txt")));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        int lowestX = contour.external.get(0).getX();
        int index = 0;

        for (int i = 1; i < contour.external.size(); i++) {
            Point2D_I32 p = contour.external.get(i);

            if (p.getX() < lowestX) {
                lowestX = p.getX();
                index = i;
            }
        }

        return index;
    }
}
