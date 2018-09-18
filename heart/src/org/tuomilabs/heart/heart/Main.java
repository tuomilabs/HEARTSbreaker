package org.tuomilabs.heart.heart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Let's go!!");

        new Main().train();
    }

    private void train() throws Exception {
        AlgorithmTrainer at = new AlgorithmTrainer();

        at.trainAlgorithms();
    }

    private void play() throws Exception {
        // Initialize the GameSimulator

        Double[] coefficientsArray = new Double[]{105.0, 0.6491194621242463, 1.752940358463908, 0.0, 0.7914513290561169, 62.0, 0.1136791236788397, 0.49844380209689865, 1.8478553796273784, 46.59543520494638, 161.6416210758341};
        List<Double> coefficients = Arrays.asList(coefficientsArray);

        List<List<Double>> four = new ArrayList<>();
        four.add(coefficients);
        four.add(coefficients);
        four.add(coefficients);
        four.add(coefficients);


        GameSimulator at = new GameSimulator(FirstAlgorithm.class, true);

        at.playGame();
        at.printScores();

    }
}
