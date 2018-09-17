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

    public static int getIndexOfWinningCard(List<Card> cards) {
        Card winningCard = getTrickWinner(cards);

        return cards.indexOf(winningCard);
    }

    public static boolean isStartingPlayer(List<Card> playerCards) {
        for (Card c : playerCards) {
            if (c.equals(Cards.TWO_OF_CLUBS)) {
                return true;
            }
        }

        return false;
    }

    public static int calculatePoints(List<Card> takenCards) {
        int points = 0;

        for (Card c : takenCards) {
            points += getPointValue(c);
        }

        return points;
    }

    public static int getPointValue(Card c) {
        if (c.getSuit() == 'h') {
            return 1;
        } else if (c.equals(Cards.QUEEN_OF_SPADES)) {
            return 13;
        } else {
            return 0;
        }
    }
}
