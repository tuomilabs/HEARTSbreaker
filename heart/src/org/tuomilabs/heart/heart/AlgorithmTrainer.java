package org.tuomilabs.heart.heart;

import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlgorithmTrainer {
    private Class algorithmType;
    private List<Algorithm> algorithms;

    AlgorithmTrainer(Class algorithmType) {
        this.algorithmType = algorithmType;

        try {
            for (int i = 0; i < 4; i++) {
                algorithms.add((Algorithm) this.algorithmType.newInstance());
                algorithms.setID(i);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void playGame() {
        // Generate a deck
        List<Card> deck = DeckFactory.generateDeck();

        // Shuffle the deck
        Collections.shuffle(deck);

        // Deal the cards
        List<List<Card>> eachPlayerCards = ListUtils.partition(deck, 4);

        for (int i = 0; i < 4; i++) {
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

            // Notify the algorithms whether they're starting the next trick or not
            for (int i = 0; i < 4; i++) {
                algorithms.get(i).getIsStartingNextTrick(i == winningPlayerIndex);
            }
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
