package org.tuomilabs.heart.heart;

public class Main {
    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        // Initialize the AlgorithmTrainer
        AlgorithmTrainer at = new AlgorithmTrainer(FirstAlgorithm.class);

        at.playGame();
    }
}
