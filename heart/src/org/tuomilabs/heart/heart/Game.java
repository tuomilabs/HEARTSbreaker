package org.tuomilabs.heart.heart;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public static Card getTrickWinner(List<Card> cards) {
        Card highestCard = cards.get(0);

        for (int i = 1; i < cards.size(); i++) {
            Card currentCard = cards.get(i);

            // If the suit is different, then there's no way it can win the suit
            if (currentCard.getSuit() != highestCard.getSuit()) {
                continue;
            }

            if (currentCard.getValue() > highestCard.getValue()) {
                highestCard = currentCard;
            }
        }

        return highestCard;
    }

    public static boolean isStartingPlayer(List<Card> playerCards) {
        Card twoOfSpades = new Card(2, 's');

        for (Card c : playerCards) {
            if (c.equals(twoOfSpades)) {
                return true;
            }
        }

        return false;
    }
}
