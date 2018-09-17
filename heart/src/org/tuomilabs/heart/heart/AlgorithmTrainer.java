package org.tuomilabs.heart.heart;

import java.util.List;

public class AlgorithmTrainer {
    private static int ROUNDS_PER_GAME = 10;

    static void trainAlgorithms() {
        // Create a new game simulator
        GameSimulator gs = new GameSimulator(FirstAlgorithm.class);

        for (int i = 0; i < ROUNDS_PER_GAME; i++) {
            gs.playGame();
        }

        // Get the best coefficients
        List<Double> bestCoefficients = gs.getBestCoefficients();
        System.out.println(bestCoefficients);
    }
}
