package org.tuomilabs.heart.heart;

import java.util.ArrayList;
import java.util.List;

public class DeckFactory {
    private static char[] SUITS = {'c', 'd', 'h', 's'};

    public static List<Card> generateDeck() {
        List<Card> deck = new ArrayList<>();

        for (int i = 2; i < 15; i++) {
            for (char c : SUITS) {
                deck.add(new Card(i, c));
            }
        }

        return deck;
    }

}
