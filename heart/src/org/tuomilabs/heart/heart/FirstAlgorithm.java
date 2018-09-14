package org.tuomilabs.heart.heart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstAlgorithm implements Algorithm {
    private List<Card> cardsPlayed;
    private List<Card> myCards;
    private boolean[][] suitsEmpty;


    public FirstAlgorithm(List<Card> dealtCards) {
        cardsPlayed = new ArrayList<>();
        myCards = new ArrayList<>(dealtCards);

        suitsEmpty = new boolean[3][4];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                suitsEmpty[i][j] = false;
            }
        }
    }

    private List<Card> getAllRemainingCardsInSuitThatOtherPlayersPossess(List<Card> cardsPlayed, List<Card> myCards) {
        List<Card> knownCardsInSuit = new ArrayList<>();
        List<Card> remainingCards = DeckFactory.generateDeck();

        knownCardsInSuit.addAll(cardsPlayed);
        knownCardsInSuit.addAll(myCards);

        remainingCards.removeAll(knownCardsInSuit);

        return remainingCards;
    }

    private double getValueRatio(Card card, List<Card> cardsPlayed, List<Card> myCards) {
        List<Card> remainingCards = getAllRemainingCardsInSuitThatOtherPlayersPossess(cardsPlayed, myCards);

        int opponentsCardsBelowMine = getCardsBelow(card, remainingCards);

        double stepSize = 1 / (double)remainingCards.size();
        double valueRatio = (opponentsCardsBelowMine * stepSize);

        /*
         * Example case: your card is an 8; opponents' cards are 3, 5, 10, J, K.
         *
         * The step size is 1/5=0.2. The value ratio of the 8 is 2*0.2=0.4.
         *
         * This agrees with what it should be:
         *      3 - 0
         *      5 - 0.2
         *      8 - 0.4
         *      10 - 0.6
         *      J - 0.8
         *      K - 1.0
         */

        return valueRatio;
    }

    private int getCardsBelow(Card card, List<Card> compareTo) {
        int lower = 0;

        for (Card c : compareTo) {
            if (c.getValue() < card.getValue()) {
                lower++;
            }
        }

        return lower;
    }

    private int getCardsAbove(Card card, List<Card> compareTo) {
        int lower = 0;

        for (Card c : compareTo) {
            if (c.getValue() > card.getValue()) {
                lower++;
            }
        }

        return lower;
    }


    private List<Integer> calculateAbsoluteRisk(List<Card> myCards, List<Card> cardsPlayed, List<Card> currentlyOnTable, boolean[][] suitsEmpty) {
        List<Integer> risks = new ArrayList<>();

        for (Card c : myCards) {
            double valueRatio = getValueRatio(c, cardsPlayed, myCards);


        }

        return null;
    }


    private List<Integer> calculateRelativeRisk(List<Card> myCards, List<Card> cardsPlayed, List<Card> currentlyOnTable, boolean[][] suitsEmpty) {
        // You implement this

        return null;
    }


    @Override
    public Card playCard(List<Card> currentlyOnTable) {
        return myCards.get(0);
    }

    @Override
    public void getFinalCards(List<Card> finalCards) {
        
    }
}
