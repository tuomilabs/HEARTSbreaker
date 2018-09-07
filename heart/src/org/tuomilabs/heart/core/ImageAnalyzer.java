package org.tuomilabs.heart.core;

import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.feature.detect.interest.InterestPointDetector;
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
import boofcv.factory.feature.detect.interest.FactoryInterestPoint;
import boofcv.factory.feature.detect.line.ConfigHoughPolar;
import boofcv.factory.feature.detect.line.FactoryDetectLineAlgs;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.feature.FancyInterestPointRender;
import boofcv.gui.feature.ImageLinePanel;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.gui.image.ShowImages;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.ConnectRule;
import boofcv.struct.PointIndex_I32;
import boofcv.struct.image.*;
import georegression.struct.line.LineParametric2D_F32;
import georegression.struct.line.LineSegment2D_F32;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I32;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ImageAnalyzer {
    private static int iterations = 100;

    private static BufferedImage generateBW() throws IOException {
        // load and convert the image into a usable format
        BufferedImage image = UtilImageIO.loadImage(UtilIO.pathExample("C:\\development\\HEARTSbreaker\\heart\\first.jpg"));

        // convert into a usable format
        GrayF32 input = ConvertBufferedImage.convertFromSingle(image, null, GrayF32.class);
        GrayU8 binary = new GrayU8(input.width, input.height);
        GrayS32 label = new GrayS32(input.width, input.height);

        // Select a global threshold using Otsu's method.
        double threshold = GThresholdImageOps.computeOtsu(input, 0, 255);
//        double threshold = GThresholdImageOps.computeEntropy(input, 0, 255);
//        threshold = threshold * 1.1;

        // Apply the threshold to create a binary image
        ThresholdImageOps.threshold(input, binary, (float) threshold, true);

        // remove small blobs through erosion and dilation
        // The null in the input indicates that it should internally declare the work image it needs
        // this is less efficient, but easier to code.
        GrayU8 filtered = BinaryImageOps.erode8(binary, 1, null);
        filtered = BinaryImageOps.dilate8(filtered, 1, null);

        // Detect blobs inside the image using an 8-connect rule
        List<Contour> contours = BinaryImageOps.contour(filtered, ConnectRule.EIGHT, label);

        // colors of contours
        int colorExternal = 0xFFFFFF;
        int colorInternal = 0xFF2020;

        // display the results
        BufferedImage visualBinary = VisualizeBinaryData.renderBinary(binary, false, null);
        BufferedImage visualFiltered = VisualizeBinaryData.renderBinary(filtered, false, null);
        BufferedImage visualLabel = VisualizeBinaryData.renderLabeledBG(label, contours.size(), null);
        BufferedImage visualContour = VisualizeBinaryData.renderContours(contours, colorExternal, colorInternal,
                input.width, input.height, null);

//        ListDisplayPanel panel = new ListDisplayPanel();
//        panel.addImage(visualBinary, "Binary Original");
//        panel.addImage(visualFiltered, "Binary Filtered");
//        panel.addImage(visualLabel, "Labeled Blobs");
//        panel.addImage(visualContour, "Contours");
//        ShowImages.showWindow(panel, "Binary Operations", true);

        return visualFiltered;
    }


    private static BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        return src.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }


    // Used to bias it towards more or fewer sides. larger number = fewer sides
    static double cornerPenalty = 0.25;
    // The fewest number of pixels a side can have
    static int minSide = 10;

    static ListDisplayPanel gui = new ListDisplayPanel();

    /**
     * Fits polygons to found contours around binary blobs.
     */
    public static void fitBinaryImage(GrayF32 input) {

        GrayU8 binary = new GrayU8(input.width, input.height);
        BufferedImage polygon = new BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB);

        // the mean pixel value is often a reasonable threshold when creating a binary image
        double mean = ImageStatistics.mean(input);

        // create a binary image by thresholding
        ThresholdImageOps.threshold(input, binary, (float) mean, true);

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
            g2.setColor(Color.BLUE);
            for (List<Point2D_I32> internal : c.internal) {
                vertexes = ShapeFittingOps.fitPolygon(internal, true, minSide, cornerPenalty, iterations);
                VisualizeShapes.drawPolygon(vertexes, true, g2);
            }
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


    public static <T extends ImageGray<T>>
    void detect(BufferedImage image, Class<T> imageType) {
        T input = ConvertBufferedImage.convertFromSingle(image, null, imageType);

        // Create a Fast Hessian detector from the SURF paper.
        // Other detectors can be used in this example too.
        InterestPointDetector<T> detector = FactoryInterestPoint.fastHessian(
                new ConfigFastHessian(10, 2, 100, 2, 9, 3, 4));

        // find interest points in the image
        detector.detect(input);

        // Show the features
        displayResults(image, detector);
    }

    private static <T extends ImageGray<T>>
    void displayResults(BufferedImage image, InterestPointDetector<T> detector) {
        Graphics2D g2 = image.createGraphics();
        FancyInterestPointRender render = new FancyInterestPointRender();


        for (int i = 0; i < detector.getNumberOfFeatures(); i++) {
            Point2D_F64 pt = detector.getLocation(i);

            // note how it checks the capabilities of the detector
            if (detector.hasScale()) {
                int radius = (int) (detector.getRadius(i));
                render.addCircle((int) pt.x, (int) pt.y, radius);
            } else {
                render.addPoint((int) pt.x, (int) pt.y);
            }
        }
        // make the circle's thicker
        g2.setStroke(new BasicStroke(3));

        // just draw the features onto the input image
        render.draw(g2);
        ShowImages.showWindow(image, "Detected Features", true);
    }


    // adjusts edge threshold for identifying pixels belonging to a line
    private static final float edgeThreshold = 25;
    // adjust the maximum number of found lines in the image
    private static final int maxLines = 10;

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
                new ConfigHoughPolar(3, 30, 2, Math.PI / 180, edgeThreshold, maxLines), imageType, derivType);
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
        DetectLineSegmentsGridRansac<T, D> detector = FactoryDetectLineAlgs.lineRansac(40, 30, 2.36, true, imageType, derivType);

        List<LineSegment2D_F32> found = detector.detect(input);

        // display the results
        ImageLinePanel gui = new ImageLinePanel();
        gui.setBackground(image);
        gui.setLineSegments(found);
        gui.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

        listPanel.addItem(gui, "Found Line Segments");
    }


    public static void main(String args[]) throws IOException {
        // Crop the image
        BufferedImage loaded = ImageIO.read(new File("C:\\development\\HEARTSbreaker\\heart\\saved_frame_82.jpg"));
        loaded = cropImage(loaded, new Rectangle(700, 1300, 2500, 1300));

        File outputfile = new File("first.jpg");
        ImageIO.write(loaded, "jpg", outputfile);


        BufferedImage second = generateBW();


        GrayU8 gray = ConvertBufferedImage.convertFrom(second, (GrayU8) null);
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
        // Note that you are only interested in verticesnal contours.
        List<Contour> contours = BinaryImageOps.contour(edgeImage, ConnectRule.EIGHT, null);

        // display the results
        BufferedImage visualBinary = VisualizeBinaryData.renderBinary(edgeImage, false, null);
        BufferedImage visualCannyContour = VisualizeBinaryData.renderContours(edgeContours, null,
                gray.width, gray.height, null);
        BufferedImage visualEdgeContour = new BufferedImage(gray.width, gray.height, BufferedImage.TYPE_INT_RGB);
        VisualizeBinaryData.render(contours, (int[]) null, visualEdgeContour);

        ListDisplayPanel panel = new ListDisplayPanel();
        panel.addImage(visualBinary, "Binary Edges from Canny");
        panel.addImage(visualCannyContour, "Canny Trace Graph");
        panel.addImage(visualEdgeContour, "Contour from Canny Binary");
        ShowImages.showWindow(panel, "Canny Edge", true);


//        GrayU8 gray = ConvertBufferedImage.convertFrom(second, (GrayU8) null);
//        GrayU8 edgeImage = gray.createSameShape();
//
//        // Create a canny edge detector which will dynamically compute the threshold based on maximum edge intensity
//        // It has also been configured to save the trace as a graph.  This is the graph created while performing
//        // hysteresis thresholding.
//        CannyEdge<GrayU8, GrayS16> canny = FactoryEdgeDetectors.canny(2, true, true, GrayU8.class, GrayS16.class);
//
//        // The edge image is actually an optional parameter.  If you don't need it just pass in null
//        canny.process(gray, 0.1f, 0.3f, edgeImage);
//
//        // First get the contour created by canny
//        List<EdgeContour> edgeContours = canny.getContours();
//        // The 'edgeContours' is a tree graph that can be difficult to process.  An alternative is to extract
//        // the contours from the binary image, which will produce a single loop for each connected cluster of pixels.
//        // Note that you are only interested in external contours.
//        List<Contour> contours = BinaryImageOps.contour(edgeImage, ConnectRule.EIGHT, null);
//
//
//        List<EdgeContour> realContours = new ArrayList<>();
//
//        for (EdgeContour contour : edgeContours) {
//            if (contour.segments.size() < 50) {
//                realContours.add(contour);
//            }
//        }
//
//
//        // display the results
//        BufferedImage visualBinary = VisualizeBinaryData.renderBinary(edgeImage, false, null);
//        BufferedImage visualCannyContour = VisualizeBinaryData.renderContours(edgeContours, null,
//                gray.width, gray.height, null);
//
//        BufferedImage visualReal = VisualizeBinaryData.renderBinary(edgeImage, false, null);
//        BufferedImage visualRealContour = VisualizeBinaryData.renderContours(realContours, null,
//                gray.width, gray.height, null);
//
//        BufferedImage visualEdgeContour = new BufferedImage(gray.width, gray.height, BufferedImage.TYPE_INT_RGB);
//        VisualizeBinaryData.render(contours, (int[]) null, visualEdgeContour);
//
////        ListDisplayPanel panel = new ListDisplayPanel();
////        panel.addImage(visualBinary,"Binary Edges from Canny");
////        panel.addImage(visualRealContour,"Real Contour");
////        panel.addImage(visualCannyContour, "Canny Trace Graph");
////        panel.addImage(visualEdgeContour,"Contour from Canny Binary");
////        ShowImages.showWindow(panel,"Canny Edge", true);
//
//        GrayF32 input = ConvertBufferedImage.convertFromSingle(visualCannyContour, null, GrayF32.class);
//
//        gui.addImage(visualCannyContour, "Original");
//
//        fitCannyEdges(input);
//        fitCannyBinary(input);
//        fitBinaryImage(input);
//
//        ShowImages.showWindow(gui, "Polygon from Contour", true);

    }
}
