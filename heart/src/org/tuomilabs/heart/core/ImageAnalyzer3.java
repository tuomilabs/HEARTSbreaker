package org.tuomilabs.heart.core;

import boofcv.abst.feature.detect.line.DetectLineHoughPolar;
import boofcv.abst.feature.detect.line.DetectLineSegmentsGridRansac;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.alg.feature.detect.edge.EdgeSegment;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.alg.shapes.ShapeFittingOps;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.factory.feature.detect.line.ConfigHoughPolar;
import boofcv.factory.feature.detect.line.FactoryDetectLineAlgs;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.feature.ImageLinePanel;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.gui.image.ShowImages;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.ConnectRule;
import boofcv.struct.PointIndex_I32;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageGray;
import com.jhlabs.image.PosterizeFilter;
import georegression.struct.line.LineParametric2D_F32;
import georegression.struct.line.LineSegment2D_F32;
import georegression.struct.point.Point2D_I32;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ImageAnalyzer3 {
    // Display multiple images in the same window
    static ListDisplayPanel gui = new ListDisplayPanel();

    static int iterations = 1;
    // Used to bias it towards more or fewer sides. larger number = fewer sides
    static double cornerPenalty = 0.9;
    // The fewest number of pixels a side can have
    static int minSide = 6000;

    // adjusts edge threshold for identifying pixels belonging to a line
    private static final float edgeThreshold = 50;
    // adjust the maximum number of found lines in the image
    private static final int maxLines = 20;

    private static ListDisplayPanel listPanel = new ListDisplayPanel();

    /**
     * Detects lines inside the image using different types of Hough detectors
     *
     * @param image     Input image.
     * @param imageType Type of image processed by line detector.
     * @param derivType Type of image derivative.
     */
    public static <T extends ImageGray<T>, D extends ImageGray<D>>
    void detectLines(BufferedImage image,
                     Class<T> imageType,
                     Class<D> derivType) {
        // convert the line into a single band image
        T input = ConvertBufferedImage.convertFromSingle(image, null, imageType);

        // Comment/uncomment to try a different type of line detector
        DetectLineHoughPolar<T, D> detector = FactoryDetectLineAlgs.houghPolar(
                new ConfigHoughPolar(30, 30, 2, Math.PI / 180, edgeThreshold, maxLines), imageType, derivType);
//		DetectLineHoughFoot<T,D> detector = FactoryDetectLineAlgs.houghFoot(
//				new ConfigHoughFoot(3, 8, 5, edgeThreshold,maxLines), imageType, derivType);
//		DetectLineHoughFootSubimage<T,D> detector = FactoryDetectLineAlgs.houghFootSub(
//				new ConfigHoughFootSubimage(3, 8, 5, edgeThreshold,maxLines, 2, 2), imageType, derivType);

        List<LineParametric2D_F32> found = detector.detect(input);

        // display the results
        ImageLinePanel gui = new ImageLinePanel();
        gui.setBackground(image);
        gui.setLines(found);
        gui.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

        listPanel.addItem(gui, "Found Lines");
    }

    /**
     * Detects segments inside the image
     *
     * @param image     Input image.
     * @param imageType Type of image processed by line detector.
     * @param derivType Type of image derivative.
     */
    public static <T extends ImageGray<T>, D extends ImageGray<D>>
    void detectLineSegments(BufferedImage image,
                            Class<T> imageType,
                            Class<D> derivType) {
        // convert the line into a single band image
        T input = ConvertBufferedImage.convertFromSingle(image, null, imageType);

        // Comment/uncomment to try a different type of line detector
        DetectLineSegmentsGridRansac<T, D> detector = FactoryDetectLineAlgs.lineRansac(100, 100, 2.36, true, imageType, derivType);

        List<LineSegment2D_F32> found = detector.detect(input);

        // display the results
        ImageLinePanel gui = new ImageLinePanel();
        gui.setBackground(image);
        gui.setLineSegments(found);
        gui.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

        listPanel.addItem(gui, "Found Line Segments");
    }

    private static BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        return src.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }

    public static void threshold(String imageName) throws IOException {
        BufferedImage imageAA = ImageIO.read(new File(imageName));
        imageAA = cropImage(imageAA, new Rectangle(700, 1300, 2500, 1300));


        BufferedImage image = new BufferedImage(imageAA.getWidth(), imageAA.getHeight(), BufferedImage.TYPE_INT_RGB);


        PosterizeFilter k = new PosterizeFilter();
        k.setNumLevels(2);
        k.filter(imageAA, image);


        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
//                System.out.println((new Color(image.getRGB(i, j))).getRed());
                image.setRGB(i, j, processPixel(image.getRGB(i, j)));
            }
        }

        // convert into a usable format
        GrayF32 input = ConvertBufferedImage.convertFromSingle(image, null, GrayF32.class);
        GrayU8 binary = new GrayU8(input.width, input.height);


        // Global Methods
        GThresholdImageOps.threshold(input, binary, ImageStatistics.mean(input), true);
        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Global: Mean");
        GThresholdImageOps.threshold(input, binary, GThresholdImageOps.computeOtsu(input, 0, 255), true);
        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Global: Otsu");

        BufferedImage gaussian = VisualizeBinaryData.renderBinary(binary, false, null);

        GThresholdImageOps.threshold(input, binary, GThresholdImageOps.computeEntropy(input, 0, 255), true);
        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Global: Entropy");

        // Local method
        GThresholdImageOps.localSquare(input, binary, 57, 1.0, true, null, null);
        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Square");
        GThresholdImageOps.localBlockMinMax(input, binary, 21, 1.0, true, 15);
        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Block Min-Max");
//        GThresholdImageOps.blockMean(input, binary, ConfigLength.fixed(21), 1.0, true);
//        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Block Mean");
//        GThresholdImageOps.blockOtsu(input, binary, false, ConfigLength.fixed(21), 0.5, 1.0, true);
//        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Block Otsu");
        GThresholdImageOps.localGaussian(input, binary, 85, 1.0, true, null, null);
        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Gaussian");

//        GThresholdImageOps.localSauvola(input, binary, 11, 0.30f, true);
//        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Sauvola");
//        GThresholdImageOps.localSauvola(input, binary, 11, 0.50f, true);
//        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Sauvola");
//        GThresholdImageOps.localSauvola(input, binary, 11, 0.70f, true);
//        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Sauvola");
//        GThresholdImageOps.localSauvola(input, binary, 11, 0.90f, true);
//        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Sauvola");
//        GThresholdImageOps.localSauvola(input, binary, 5, 0.30f, true);
//        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Sauvola");
//        GThresholdImageOps.localSauvola(input, binary, 20, 0.30f, true);
//        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Sauvola");
//        GThresholdImageOps.localSauvola(input, binary, 50, 0.30f, true);
//        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Sauvola");
//        GThresholdImageOps.localSauvola(input, binary, 30, 0.30f, true);
//        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Sauvola");
//        GThresholdImageOps.localSauvola(input, binary, 40, 0.30f, true);
//        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Sauvola");
        GThresholdImageOps.localSauvola(input, binary, 60, 0.15f, true);
        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Sauvola");
//        GThresholdImageOps.localSauvola(input, binary, 100, 0.30f, true);
//        gui.addImage(VisualizeBinaryData.renderBinary(binary, false, null), "Local: Sauvola");
//        GThresholdImageOps.localSauvola(input, binary, 300, 0.30f, true);
//        gui.addImage(VisualizeBinaryData.renderBinary(binary, true, null), "Local: Sauvola");
//        GThresholdImageOps.localSauvola(input, binary, 60, 0.30f, true);

        // Sauvola is tuned for text image.  Change radius to make it run better in others.

        // Show the image image for reference
        gui.addImage(ConvertBufferedImage.convertTo(input, null), "Input Image");

        detectLines(gaussian, GrayU8.class, GrayS16.class);

        // line segment detection is still under development and only works for F32 images right now
        detectLineSegments(gaussian, GrayF32.class, GrayF32.class);

        ShowImages.showWindow(listPanel, "Detected Lines", true);


        String fileName = imageName.substring(imageName.lastIndexOf('/') + 1);
//        ShowImages.showWindow(gui, fileName);

//        BufferedImage gaussian = VisualizeBinaryData.renderBinary(binary, false, null);


        gui.addImage(image, "Original");


        GrayF32 gaussianGray = new GrayF32(gaussian.getWidth(), gaussian.getHeight());
        ConvertBufferedImage.convertFrom(gaussian, gaussianGray);
//
//
//        GrayF32 denoised = gaussianGray.createSameShape();
//
//        // How many levels in wavelet transform
//        int numLevels = 10;
//        // Create the noise removal algorithm
//        WaveletDenoiseFilter<GrayF32> denoiser =
//                FactoryImageDenoise.waveletBayes(GrayF32.class, numLevels, 0, 255);
//
//        // remove noise from the image
//        denoiser.process(gaussianGray, denoised);
//        gui.addImage(ConvertBufferedImage.convertTo(denoised, null), "Denoised");

        fitCannyEdges(gaussianGray);
        fitCannyBinary(gaussianGray);
        fitBinaryImage(gaussianGray);


        gui.addImage(image, "Cool");

        ShowImages.showWindow(gui, "Polygon from Contour", true);
    }

    public static int processPixel(int pixel) {
        int red = (0xff & (pixel >> 16));
        int green = (0xff & (pixel >> 8));
        int blue = (0xff & pixel);
        red = (red - (red % 64));
        green = (green - (green % 64));
        blue = (blue - (blue % 64));
        if (red > 255) red = 255;
        if (red < 0) red = 0;
        if (green > 255) green = 255;
        if (green < 0) green = 0;
        if (blue > 255) blue = 255;
        if (blue < 0) blue = 0;
        pixel = (0xff000000 | red << 16 | green << 8 | blue);
        return pixel;
    }

    /**
     * Fits polygons to found contours around binary blobs.
     */
    public static void fitBinaryImage(GrayF32 input) {

        GrayU8 binary = new GrayU8(input.width, input.height);
        BufferedImage polygon = new BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB);

        // the mean pixel value is often a reasonable threshold when creating a binary image
        double mean = ImageStatistics.mean(input);

        // create a binary image by thresholding
        ThresholdImageOps.threshold(input, binary, 100, true);

        // reduce noise with some filtering
        GrayU8 filtered = BinaryImageOps.erode8(binary, 1, null);
        filtered = BinaryImageOps.dilate8(filtered, 1, null);

        // Find the contour around the shapes
        List<Contour> contours = BinaryImageOps.contour(filtered, ConnectRule.EIGHT, null);

        // Fit a polygon to each shape and draw the results
        Graphics2D g2 = polygon.createGraphics();
        g2.setStroke(new BasicStroke(2));

        for (Contour c : contours) {
            // Fit the polygon to the found external contour.  Note loop = true
            List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(c.external, true, minSide, cornerPenalty, iterations);

            g2.setColor(Color.RED);
            VisualizeShapes.drawPolygon(vertexes, true, g2);

            // handle internal contours now
//            g2.setColor(Color.BLUE);
//            for (List<Point2D_I32> internal : c.internal) {
//                vertexes = ShapeFittingOps.fitPolygon(internal, true, minSide, cornerPenalty, iterations);
//                VisualizeShapes.drawPolygon(vertexes, true, g2);
//            }
        }

        gui.addImage(polygon, "Binary Blob Contours");
    }

    /**
     * Fits a sequence of line-segments into a sequence of points found using the Canny edge detector.  In this case
     * the points are not connected in a loop. The canny detector produces a more complex tree and the fitted
     * points can be a bit noisy compared to the others.
     */
    public static void fitCannyEdges(GrayF32 input) {

        BufferedImage displayImage = new BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB);

        // Finds edges inside the image
        CannyEdge<GrayF32, GrayF32> canny =
                FactoryEdgeDetectors.canny(2, true, true, GrayF32.class, GrayF32.class);

        canny.process(input, 0.1f, 0.3f, null);
        List<EdgeContour> contours = canny.getContours();

        Graphics2D g2 = displayImage.createGraphics();
        g2.setStroke(new BasicStroke(2));

        // used to select colors for each line
        Random rand = new Random(234);

        for (EdgeContour e : contours) {
            g2.setColor(new Color(rand.nextInt()));

            for (EdgeSegment s : e.segments) {
                // fit line segments to the point sequence.  Note that loop is false
                List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(s.points, false, minSide, cornerPenalty, iterations);

                VisualizeShapes.drawPolygon(vertexes, false, g2);
            }
        }

        gui.addImage(displayImage, "Canny Trace");
    }


    /**
     * Detects contours inside the binary image generated by canny.  Only the external contour is relevant. Often
     * easier to deal with than working with Canny edges directly.
     */
    public static void fitCannyBinary(GrayF32 input) {

        BufferedImage displayImage = new BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB);
        GrayU8 binary = new GrayU8(input.width, input.height);

        // Finds edges inside the image
        CannyEdge<GrayF32, GrayF32> canny =
                FactoryEdgeDetectors.canny(2, false, true, GrayF32.class, GrayF32.class);

        canny.process(input, 0.1f, 0.3f, binary);

        List<Contour> contours = BinaryImageOps.contour(binary, ConnectRule.EIGHT, null);

        Graphics2D g2 = displayImage.createGraphics();
        g2.setStroke(new BasicStroke(2));

        // used to select colors for each line
        Random rand = new Random(234);

        for (Contour c : contours) {
            // Only the external contours are relevant.
            List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(c.external, true, minSide, cornerPenalty, iterations);

            g2.setColor(new Color(rand.nextInt()));
            VisualizeShapes.drawPolygon(vertexes, true, g2);
        }

        gui.addImage(displayImage, "Canny Contour");
    }


    private static int getLongDiagonal(List<PointIndex_I32> vertexes) {
        int[] bounds = getBounds(vertexes);

        double x1 = (double) bounds[0];
        double y1 = (double) bounds[1];
        double x2 = (double) bounds[2];
        double y2 = (double) bounds[3];

        return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private static int[] getBounds(List<PointIndex_I32> vertices) {
        int minX = 1000000;
        int minY = 1000000; // Hi people with higher resolution
        int maxX = 0;
        int maxY = 0;

        for (Point2D_I32 vertex : vertices) {
            int currentX = vertex.x;
            int currentY = vertex.y;

            if (currentX > maxX) {
                maxX = currentX;
            }
            if (currentX < minX) {
                minX = currentX;
            }
            if (currentY > maxY) {
                maxY = currentY;
            }
            if (currentY < minY) {
                minY = currentY;
            }
        }

        return new int[]{minX, minY, maxX, maxY};
    }

    public static void main(String[] args) throws IOException {
        // example in which global thresholding works best
        threshold(UtilIO.pathExample("C:\\development\\HEARTSbreaker\\heart\\saved_frame_82.jpg"));
        // example in which adaptive/local thresholding works best
//        threshold(UtilIO.pathExample("C:\\development\\HEARTSbreaker\\heart\\saved_frame_82.jpg"));
        // hand written text with non-uniform stained background
//        threshold(UtilIO.pathExample("C:\\development\\HEARTSbreaker\\heart\\saved_frame_82.jpg"));
    }
}
