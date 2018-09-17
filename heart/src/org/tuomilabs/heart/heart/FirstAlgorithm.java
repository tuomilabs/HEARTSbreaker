package org.tuomilabs.heart.heart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstAlgorithm implements Algorithm {
    private List<Card> cardsPlayed;
    private List<Card> myCards;
    private boolean[][] suitsEmpty;
    private int starting;
    private int id;
    public double pointsInPlay, CpointsInPlay, Cpoints, POOS, CPOOS, cardsPlayedOfSuit, CcardsPlayedOfSuit, VR, CVR, playingC, startingC;


    public FirstAlgorithm() {
        cardsPlayed = new ArrayList<>();

        suitsEmpty = new boolean[4][4];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                suitsEmpty[i][j] = false;
            }
        }
    }

    public FirstAlgorithm(List<Card> dealtCards, List<Double> inputs) {

        cardsPlayed = new ArrayList<>();

        myCards = new ArrayList<>(dealtCards);

        suitsEmpty = new boolean[4][4];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                suitsEmpty[i][j] = false;
            }
        }

        pointsInPlay = inputs.get(0);
        CpointsInPlay = inputs.get(1);
        Cpoints = inputs.get(2);
        POOS = inputs.get(3);
        CPOOS = inputs.get(4);
        cardsPlayedOfSuit = inputs.get(5);
        CcardsPlayedOfSuit = inputs.get(6);
        VR = inputs.get(7);
        CVR = inputs.get(8);
        playingC = inputs.get(9);
        startingC = inputs.get(10);

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

        double stepSize = 1 / (double) remainingCards.size();
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

    private int playcard_havesuit(List<Card> currentlyOnTable) {
        int cardplayed = 0;
        int maxCard = maxCardOfSuit(currentlyOnTable);
        for (int i = 0; i < myCards.size(); i++) {
            if (myCards.get(i).getSuit() == currentlyOnTable.get(0).getSuit() && getValue(myCards.get(i), maxCard) > getValue(myCards.get(cardplayed), maxCard) && getValue(myCards.get(i), maxCard) < playingC) {
                cardplayed = i;
            }
        }


        return cardplayed;
    }

    private int playcard_donthavesuit() {
        int cardplayed = 0;
        int maxCard = -1;
        for (int i = 0; i < myCards.size(); i++) {
            if (getValue(myCards.get(i), maxCard) > getValue(myCards.get(cardplayed), maxCard)) {
                cardplayed = i;
            }
        }


        return cardplayed;
    }

    private int playcard_starting() {
        int cardplayed = 0;
        int maxCard = -1;
        for (int s = 0; s < 4; s++) {
            for (Card c : cardsPlayed) {
                if (c.getSuit() == s) cardsPlayedOfSuit++;
            }

            for (int i = id + 1; i < starting; i++, i %= 4) {
                if (suitsEmpty[i][getSuitNumber(s)]) POOS++;
            }
            for (int i = 0; i < myCards.size(); i++) {
                if (myCards.get(i).getSuit() == s && getValue(myCards.get(i), maxCard) > getValue(myCards.get(cardplayed), maxCard) && getValue(myCards.get(i), maxCard) < startingC) {
                    cardplayed = i;
                }
            }
        }

        return cardplayed;
    }

    private int getSuitNumber(int suitInt) {
        switch (suitInt) {
            case (int) 'c':
                return 0;
            case (int) 'd':
                return 1;
            case (int) 'h':
                return 2;
            case (int) 's':
                return 3;
            default:
                return -1;
        }
    }

    private double getValue(Card card, int maxCard) {

        if (maxCard == -1)
            return (pointsInPlay * CpointsInPlay + 1) * (Game.getPointValue(card) * Cpoints + 1) * (POOS * CPOOS + 1) * (cardsPlayedOfSuit * CcardsPlayedOfSuit + 1) * (getValueRatio(card, cardsPlayed, myCards) * CVR + 1);


        else
            return (pointsInPlay * CpointsInPlay + 1) * (Game.getPointValue(card) * Cpoints + 1) * (POOS * CPOOS + 1) * (cardsPlayedOfSuit * CcardsPlayedOfSuit + 1) * (getValueRatio(card, cardsPlayed, myCards) * CVR + 1) * Math.min(1, Math.max(0, card.getValue() - maxCard));

    }

    private boolean havesuit(int suit) {
        boolean havesuit = false;
        for (int i = 0; i < myCards.size(); i++) {
            if (myCards.get(i).getSuit() == getSuitNumber(suit)) havesuit = true;
        }
        return havesuit;
    }

    private int maxCardOfSuit(List<Card> currentlyOnTable) {
        char suit = currentlyOnTable.get(0).getSuit();
        int maxValue = currentlyOnTable.get(0).getValue();

        for (Card cardOnTable : currentlyOnTable) {
            if (cardOnTable.getValue() > maxValue && cardOnTable.getSuit() == suit) {
                maxValue = cardOnTable.getValue();
            }
        }

        return maxValue;
    }


    @Override
    public Card playCard(List<Card> currentlyOnTable) {
        cardsPlayedOfSuit = 0;
        pointsInPlay = 0;
        POOS = 0;

        int startingSuit;
        if (currentlyOnTable.size() > 0) {
            startingSuit = currentlyOnTable.get(0).getSuit();
            for (Card c : currentlyOnTable) {
                cardsPlayed.add(c);
            }

            for (Card c : cardsPlayed) {
                pointsInPlay += Game.getPointValue(c);
                if (c.getSuit() == startingSuit) cardsPlayedOfSuit++;
            }

            for (int i = id + 1; i < starting; i++, i %= 4) {
                if (suitsEmpty[i][getSuitNumber(startingSuit)]) POOS++;
            }
        }


        if (currentlyOnTable.size() == 0) {
            return myCards.get(playcard_starting());
        } else {
            if (havesuit(currentlyOnTable.get(0).getSuit())) return myCards.get(playcard_havesuit(currentlyOnTable));

            else return myCards.get(playcard_donthavesuit());

        }
    }

    @Override
    public void getFinalCards(List<Card> finalCards) {
        int startingSuit = finalCards.get(0).getSuit();
        for (int i = 0; i < 4; i++) {
            if (finalCards.get(i).getSuit() != startingSuit) suitsEmpty[i][getSuitNumber(startingSuit)] = true;
            if (cardsPlayed.contains(finalCards.get(i)) == false) cardsPlayed.add(finalCards.get(i));
            if (myCards.contains(finalCards.get(i))) myCards.remove(finalCards.get(i));
        }


    }


    @Override
    public void setCoefficients(List<Double> coefficients) {

        pointsInPlay = coefficients.get(0);
        CpointsInPlay = coefficients.get(1);
        Cpoints = coefficients.get(2);
        POOS = coefficients.get(3);
        CPOOS = coefficients.get(4);
        cardsPlayedOfSuit = coefficients.get(5);
        CcardsPlayedOfSuit = coefficients.get(6);
        VR = coefficients.get(7);
        CVR = coefficients.get(8);
        playingC = coefficients.get(9);
        startingC = coefficients.get(10);

    }


    @Override
    public void dealCards(List<Card> dealtCards) {

        this.myCards = dealtCards;

    }


    @Override
    public void getWhoIsStartingNextTrick(int starting) {

        this.starting = starting;

    }


    @Override
    public List<Card> getCards() {

        return myCards;
    }


    @Override
    public List<Double> getCoefficients() {
        return Arrays.asList(pointsInPlay, CpointsInPlay, Cpoints, POOS, CPOOS, cardsPlayedOfSuit, CcardsPlayedOfSuit, VR, CVR, playingC, startingC);
    }


    @Override
    public void setID(int id) {

        this.id = id;

    }
}
