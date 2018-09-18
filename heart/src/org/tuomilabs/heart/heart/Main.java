package org.tuomilabs.heart.heart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Let's go!!");

        new Main().play();
    }

    private void train() throws Exception {
        AlgorithmTrainer at = new AlgorithmTrainer();

        at.trainAlgorithms();
    }

    private void play() throws Exception {
        // Initialize the GameSimulator

        Double[] coefficientsArray = new Double[]{0.07121923642709405, 0.04780899399534995, 0.06708895987395794, 0.07936939055868675, 0.015890456003451556, 3.966960840201052, 4.60695094706519};
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
