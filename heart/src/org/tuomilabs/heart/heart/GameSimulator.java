package org.tuomilabs.heart.heart;

import org.apache.commons.collections4.ListUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameSimulator {
    private Class algorithmType;
    private List<Algorithm> algorithms;
    Map<Integer, Integer> points;

    private boolean displayGame;

    GameSimulator(Class algorithmType) {
        this.algorithmType = algorithmType;
        this.algorithms = new ArrayList<>();
        displayGame = false;

        points = new HashMap<>();
        points.put(0, 0);
        points.put(1, 0);
        points.put(2, 0);
        points.put(3, 0);

        try {
            for (int i = 0; i < 4; i++) {
                algorithms.add((Algorithm) this.algorithmType.newInstance());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    GameSimulator(Class algorithmType, boolean displayGame) {
        this.algorithmType = algorithmType;
        this.algorithms = new ArrayList<>();
        this.displayGame = true;

        points = new HashMap<>();
        points.put(0, 0);
        points.put(1, 0);
        points.put(2, 0);
        points.put(3, 0);

        try {
            for (int i = 0; i < 4; i++) {
                algorithms.add((Algorithm) this.algorithmType.newInstance());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void playGame() {
        for (int i = 0; i < 4; i++) {
            algorithms.get(i).setCoefficients(randomCoefficients());
        }


        Map<Integer, List<Card>> takenCards = new HashMap<>();
        takenCards.put(0, new ArrayList<>());
        takenCards.put(1, new ArrayList<>());
        takenCards.put(2, new ArrayList<>());
        takenCards.put(3, new ArrayList<>());



        // Generate a deck
        List<Card> deck = DeckFactory.generateDeck();

        // Shuffle the deck
        Collections.shuffle(deck);

        // Deal the cards and set the player IDs
        List<List<Card>> eachPlayerCards = ListUtils.partition(deck, 13);

        for (int i = 0; i < 4; i++) {
            algorithms.get(i).setID(i);
            algorithms.get(i).dealCards(eachPlayerCards.get(i));

            if (displayGame) {
                System.out.println("Player " + i + " just got the cards: " + Game.sortCards(eachPlayerCards.get(i)) + "");
            }
        }

        int startingPlayer = getStartingPlayer();

        if (displayGame) {
            System.out.println("The starting player is player " + startingPlayer + ".");
        }

        for (int turn = 0; turn < 13; turn++) {
            if (displayGame) {
                System.out.println("Starting turn " + turn + "!");
            }

            List<Card> currentCardsOnTable = new ArrayList<>();

            int cardsPlayed = 0;
            for (int currentPlayer = startingPlayer; currentPlayer < 4; currentPlayer++, currentPlayer %= 4) {
                // Ask the algorithm to play a card, given the cards currently on the table
                Card playedCard = algorithms.get(currentPlayer).playCard(currentCardsOnTable);
                cardsPlayed++;

                System.out.println("Player " + currentPlayer + " just played a " + playedCard + "");

                // Add the played card to the cards on the table
                currentCardsOnTable.add(playedCard);

                // If four cards have been played, end the trick.
                if (cardsPlayed == 4) {
                    break;
                }
            }

            // Send the final cards to each algorithm
            for (Algorithm a : algorithms) {
                a.getFinalCards(currentCardsOnTable);
            }

            // Figure out which player played the winning card
            int winningIndex = Game.getIndexOfWinningCard(currentCardsOnTable);
            int winningPlayerIndex = (winningIndex + startingPlayer) % 4; // Just gonna hope that the math works out

            // Notify the algorithms as to who's starting the next trick
            for (int i = 0; i < 4; i++) {
                algorithms.get(i).getWhoIsStartingNextTrick(winningPlayerIndex);
            }


            // Add the taken cards to that algorithm's stash
            List<Card> currentTakenCards = takenCards.get(winningPlayerIndex);
            currentTakenCards.addAll(currentCardsOnTable);
            takenCards.put(winningPlayerIndex, currentTakenCards);
        }


        // Calculate the number of points for each player at the end of the game
        for (int i = 0; i < 4; i++) {
            int currentPoints = points.get(i);
            currentPoints += Game.calculatePoints(takenCards.get(i));
            points.put(i, currentPoints);
        }


        assert points.get(0) + points.get(1) + points.get(2) + points.get(3) == 26;

        System.out.println("");
        System.out.println("");
        System.out.println("Player 0 points: " + points.get(0));
        System.out.println("Player 1 points: " + points.get(1));
        System.out.println("Player 2 points: " + points.get(2));
        System.out.println("Player 3 points: " + points.get(3));
    }

    private List<Double> randomCoefficients() {
        List<Double> coefficients = new ArrayList<>();

        coefficients.add(ThreadLocalRandom.current().nextDouble(0, 3));
        coefficients.add(ThreadLocalRandom.current().nextDouble(0, 3));
        coefficients.add(ThreadLocalRandom.current().nextDouble(0, 3));
        coefficients.add(ThreadLocalRandom.current().nextDouble(0, 3));
        coefficients.add(ThreadLocalRandom.current().nextDouble(0, 3));
        coefficients.add(ThreadLocalRandom.current().nextDouble(0, 3));
        coefficients.add(ThreadLocalRandom.current().nextDouble(0, 3));
        coefficients.add(ThreadLocalRandom.current().nextDouble(0, 3));
        coefficients.add(ThreadLocalRandom.current().nextDouble(0, 3));
        coefficients.add(ThreadLocalRandom.current().nextDouble(0, 200));
        coefficients.add(ThreadLocalRandom.current().nextDouble(0, 200));

        return coefficients;
    }

    private int getStartingPlayer() {
        for (int i = 0; i < algorithms.size(); i++) {
            Algorithm a = algorithms.get(i);
            if (Game.isStartingPlayer(a.getCards())) {
                return i;
            }
        }

        return -1;
    }

    List<Double> getBestCoefficients() {
        int bestPoints = points.get(0);
        List<Double> bestCoefficients = algorithms.get(0).getCoefficients();

        for (int i = 0; i < algorithms.size(); i++) {
            if (points.get(i) > bestPoints) {
                bestPoints = points.get(i);
                bestCoefficients = algorithms.get(i).getCoefficients();
            }
        }

        return bestCoefficients;
    }
}
