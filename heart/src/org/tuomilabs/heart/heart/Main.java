package org.tuomilabs.heart.heart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Let's go!!");

        new Main().run();
    }

    private void run() throws Exception {
        // Initialize the GameSimulator

        Double[] coefficientsArray = new Double[]{133.0, 0.06742121460764763, 0.03129535784045004, 1.0, 0.08158386768743209, 55.0, 0.03482095169761712, 0.051721344650463776, 0.012530869790994655, 1.6284763890297371, 2.2278345325520537};
        List<Double> coefficients = Arrays.asList(coefficientsArray);

        List<List<Double>> four = new ArrayList<>();
        four.add(coefficients);
        four.add(coefficients);
        four.add(coefficients);
        four.add(coefficients);


        GameSimulator at = new GameSimulator(FirstAlgorithm.class, four, true);

        at.playGame();
        at.printScores();

//        AlgorithmTrainer at = new AlgorithmTrainer();

//        at.trainAlgorithms();
    }
}
