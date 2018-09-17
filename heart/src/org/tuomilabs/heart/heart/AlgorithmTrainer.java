package org.tuomilabs.heart.heart;

import org.apache.commons.collections4.ListUtils;

import java.util.*;

public class AlgorithmTrainer {
    private Class algorithmType;
    private List<Algorithm> algorithms;
    Map<Integer, Integer> points;

    AlgorithmTrainer(Class algorithmType) {
        this.algorithmType = algorithmType;
        this.algorithms = new ArrayList<>();

        try {
            for (int i = 0; i < 4; i++) {
                algorithms.add((Algorithm) this.algorithmType.newInstance());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void playGame() {
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
        List<List<Card>> eachPlayerCards = ListUtils.partition(deck, 4);

        for (int i = 0; i < 4; i++) {
            algorithms.get(i).setID(i);
            algorithms.get(i).dealCards(eachPlayerCards.get(i));
        }

        int startingPlayer = getStartingPlayer();
        for (int turn = 0; turn < 13; turn++) {
            List<Card> currentCardsOnTable = new ArrayList<>();

            for (int currentPlayer = startingPlayer; currentPlayer < 4; currentPlayer++, currentPlayer %= 4) {
                // Ask the algorithm to play a card, given the cards currently on the table
                Card playedCard = algorithms.get(currentPlayer).playCard(currentCardsOnTable);

                // Add the played card to the cards on the table
                currentCardsOnTable.add(playedCard);
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
}
