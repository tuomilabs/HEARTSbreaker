package org.tuomilabs.heart.heart;

public class Main {
    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        // Initialize the GameSimulator
        GameSimulator at = new GameSimulator(FirstAlgorithm.class);

        at.playGame();
    }
}
