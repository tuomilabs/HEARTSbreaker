package org.tuomilabs.heart.heart;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlgorithmTrainer {
    private Class algorithmType;
    private Algorithm a1;
    private Algorithm a2;
    private Algorithm a3;
    private Algorithm a4;

    AlgorithmTrainer(Class algorithmType) {
        this.algorithmType = algorithmType;

        try {
            a1 = (Algorithm) algorithmType.newInstance();
            a2 = (Algorithm) algorithmType.newInstance();
            a3 = (Algorithm) algorithmType.newInstance();
            a4 = (Algorithm) algorithmType.newInstance();
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

        a1.dealCards(eachPlayerCards.get(0));
        a2.dealCards(eachPlayerCards.get(1));
        a3.dealCards(eachPlayerCards.get(2));
        a4.dealCards(eachPlayerCards.get(3));

        for (int turn = 0; turn < 13; turn++) {

        }
    }
}
