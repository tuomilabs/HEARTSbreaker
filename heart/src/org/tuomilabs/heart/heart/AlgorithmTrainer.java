package org.tuomilabs.heart.heart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlgorithmTrainer {
    private static final int INITIAL_ALGORITHMS = 200;
    private static final int ALGORITHMS_TO_TEST_PER_ROUND = 50;
    private static final int GENERATIONS = 50;
    private static int ROUNDS_PER_GAME = 10;

    List<AlgorithmQuality> goodAlgorithms;
    private double mutationFactor = 0.03;

    void trainAlgorithms() {
        goodAlgorithms = new ArrayList<>();


        System.out.println("Generating initial random algorithms...");

        // Generate some initial good algorithms
        for (int g = 0; g < INITIAL_ALGORITHMS; g++) {
            // Create a new game simulator
            GameSimulator gs = new GameSimulator(FirstAlgorithm.class, false);

            for (int i = 0; i < ROUNDS_PER_GAME; i++) {
                gs.playGame();
            }

//            gs.printScores();

            // Get the best coefficients
            List<Double> bestCoefficients = gs.getBestCoefficients();
            int points = gs.getScores().get(gs.getBestAlgorithm());
            goodAlgorithms.add(new AlgorithmQuality(bestCoefficients, points));
        }

        System.out.println("Done!");

        Collections.sort(goodAlgorithms);
//        System.out.println(goodAlgorithms.get(0).getScore() + "; " + goodAlgorithms.get(0).getCoefficients());


        for (int generation = 0; generation < GENERATIONS; generation++) {
            System.out.println("Entering Generation " + generation + ".");

            // Take the top 15 algorithms, and mate them
            List<AlgorithmQuality> top15 = goodAlgorithms.subList(0, 15);

//            System.out.println(top15.get(0).getCoefficients());
//            System.out.println(top15.get(1).getCoefficients());

            List<AlgorithmQuality> children = new ArrayList<>();

            for (int i = 0; i < top15.size(); i++) {
                for (int j = i + 1; j < top15.size(); j++) {
                    AlgorithmQuality goodAlgorithm1 = top15.get(i);
                    AlgorithmQuality goodAlgorithm2 = top15.get(j);
                    AlgorithmQuality matedAlgorithm = mate(goodAlgorithm1, goodAlgorithm2);
                    children.add(matedAlgorithm);
                }
            }

            goodAlgorithms.addAll(children);

            for (int i = 0; i < ALGORITHMS_TO_TEST_PER_ROUND; i++) {
                Collections.shuffle(goodAlgorithms);
                List<AlgorithmQuality> fourPlayers = goodAlgorithms.subList(0, 4);

                List<List<Double>> coefficients = new ArrayList<>();

                for (AlgorithmQuality aq : fourPlayers) {
                    coefficients.add(aq.getCoefficients());
                }

                GameSimulator gs = new GameSimulator(FirstAlgorithm.class, coefficients, false);

                for (int r = 0; r < ROUNDS_PER_GAME; r++) {
                    gs.playGame();
                }

//                gs.printScores();

                // Get the best coefficients
                List<Double> bestCoefficients = gs.getBestCoefficients();
                int points = gs.getScores().get(gs.getBestAlgorithm());
                goodAlgorithms.add(new AlgorithmQuality(bestCoefficients, points));
            }


            Collections.sort(goodAlgorithms);

            System.out.print("Average generation score: " + averageGenerationScore(goodAlgorithms) + ";                                ");
            System.out.println("Best algorithm this generation: " + goodAlgorithms.get(0).getScore() + "; " + goodAlgorithms.get(0).getCoefficients());
        }


    }

    private Double averageGenerationScore(List<AlgorithmQuality> algorithms) {
        int score = 0;

        for (AlgorithmQuality algorithm : algorithms) {
            score += algorithm.getScore() < Integer.MAX_VALUE ? algorithm.getScore() : 0;
        }

        return (double)score / (double)algorithms.size();
    }

    private AlgorithmQuality mate(AlgorithmQuality aq1, AlgorithmQuality aq2) {
        List<Double> coefficients1 = aq1.getCoefficients();
        List<Double> coefficients2 = aq2.getCoefficients();

//        System.out.println(coefficients1);
//        System.out.println(coefficients2);

        List<Double> matedCoefficients = mate(coefficients1, coefficients2);

        return new AlgorithmQuality(matedCoefficients, Integer.MAX_VALUE);
    }

    private List<Double> mate(List<Double> coefficients1, List<Double> coefficients2) {
        List<Double> matedCoefficients = new ArrayList<>();

        for (int i = 0; i < coefficients1.size(); i++) {
            matedCoefficients.add(mutationFactor * ((coefficients1.get(i) + coefficients2.get(i)) / 2));
        }

        return matedCoefficients;
    }
}
