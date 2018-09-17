package org.tuomilabs.heart.heart;

import com.google.common.collect.Lists;
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

        }
    }

    public int getStartingPlayer() {
        for ()
    }
}
