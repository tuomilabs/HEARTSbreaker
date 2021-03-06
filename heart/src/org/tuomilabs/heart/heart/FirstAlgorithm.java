package org.tuomilabs.heart.heart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstAlgorithm implements Algorithm {
    private List<Card> cardsPlayed;
    private List<Card> myCards;
    private boolean[] cardPlayed;
    private boolean[][] suitsEmpty;
    private boolean heartBroken;
    private int starting;
    private int id;
    public double pointsInPlay, CpointsInPlay, Cpoints, POOS, CPOOS, cardsPlayedOfSuit, CcardsPlayedOfSuit, VR, CVR, playingC, startingC;


    public FirstAlgorithm() {
        cardsPlayed = new ArrayList<>();
        heartBroken = false;

        suitsEmpty = new boolean[4][4];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                suitsEmpty[i][j] = false;
            }
        }
    }

    public FirstAlgorithm(List<Card> dealtCards, List<Double> inputs) {

        cardsPlayed = new ArrayList<>();
        heartBroken = false;
        myCards = new ArrayList<>(dealtCards);

        suitsEmpty = new boolean[4][4];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                suitsEmpty[i][j] = false;
            }
        }

        pointsInPlay = 0;
        CpointsInPlay = inputs.get(0);
        Cpoints = inputs.get(1);
        POOS = 0;
        CPOOS = inputs.get(2);
        cardsPlayedOfSuit = 0;
        CcardsPlayedOfSuit = inputs.get(3);
        VR = 0;
        CVR = inputs.get(4);
        playingC = inputs.get(5);
        startingC = inputs.get(6);

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


    private int getFirstCardOfSuit(char suit) {
        if (suit == ' ') {
            for (int i = 0; i < 13; i++) {
                if (!cardPlayed[i]) {
                    return i;
                }
            }
        }

        for (int i = 0; i < 13; i++) {
            if (myCards.get(i).getSuit() == suit && !cardPlayed[i]) {
                return i;
            }
        }
        return -1;
    }

    private int playcard_havesuit(List<Card> currentlyOnTable) {
        int cardplayed = 0;
        int minrisk = getFirstCardOfSuit(currentlyOnTable.get(0).getSuit());
        boolean runmin = true;
        int maxCard = maxCardOfSuit(currentlyOnTable);

        int maxvalue = -1;

        for (int i = 0; i < myCards.size(); i++) {
        	VR = getValueRatio(myCards.get(i), cardsPlayed, myCards);
            if (!this.cardPlayed[i] && myCards.get(i).getSuit() == currentlyOnTable.get(0).getSuit() && getRisk(myCards.get(i), maxCard) >= getRisk(myCards.get(cardplayed), maxCard) && getRisk(myCards.get(i), maxCard) < playingC) {

                runmin = false;
                if (myCards.get(i).getValue() > maxvalue) {
                    cardplayed = i;
                    maxvalue = myCards.get(i).getValue();
                }
            }
            if (getRisk(myCards.get(i), maxCard) < getRisk(myCards.get(minrisk), maxCard) && !this.cardPlayed[i] && myCards.get(i).getSuit() == currentlyOnTable.get(0).getSuit()) {
                minrisk = i;
            }
        }
        if (runmin) return minrisk;

        return cardplayed;
    }

    private int playcard_donthavesuit() {
        int cardIWantToPlayIndex = -1;
        double greatestRisk = Double.MIN_VALUE;


        int maxCard = -1; // Always -1 in this case, since we don't care

        // Iterate through all of the cards I have
        for (int i = 0; i < myCards.size(); i++) {
        	
            // If the card has already been played, skip it
            if (cardPlayed[i]) {
                continue;
            }
            VR = getValueRatio(myCards.get(i), cardsPlayed, myCards);

            double currentRisk = getRisk(myCards.get(i), maxCard);

//            System.out.println("Current risk: " + currentRisk);

            if (currentRisk > greatestRisk) {
                cardIWantToPlayIndex = i;
            }
        }

        return cardIWantToPlayIndex;
    }

    private int playcard_starting() {
    	if (cardsPlayed.size() == 0){
    		return myCards.indexOf(Cards.TWO_OF_CLUBS);
    	}
    	
    	
        int cardplayed = 0;
        int maxCard = -1;
        int minrisk = getFirstCardOfSuit(' ');
        boolean runmin = true;
        int maxvalue = -1;
        for (int s = 0; s < 4; s++) {
        	if(!heartBroken && s == 2) continue;
            for (Card c : cardsPlayed) {
                if (c.getSuit() == s) cardsPlayedOfSuit++;
            }

            for (int i = id + 1; i < starting; i++, i %= 4) {

                if (suitsEmpty[i][s]) POOS++;
            }
            for (int i = 0; i < myCards.size(); i++) {
            	VR = getValueRatio(myCards.get(i), cardsPlayed, myCards);
                if (!this.cardPlayed[i] && myCards.get(i).getSuit() == s && getRisk(myCards.get(i), maxCard) > getRisk(myCards.get(cardplayed), maxCard) && getRisk(myCards.get(i), maxCard) < startingC) {
                    if (myCards.get(i).getValue() > maxvalue) {
                        cardplayed = i;
                        maxvalue = myCards.get(i).getValue();
                    }
                    runmin = false;
                }
                if (getRisk(myCards.get(i), maxCard) < getRisk(myCards.get(minrisk), maxCard) && !this.cardPlayed[i]) {
                    minrisk = i;
                }
            }

        }
        if (runmin) {

            return minrisk;
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

    private double getRisk(Card card, int maxCard) {
        double risk = 0;

        if (maxCard == -1) {
            risk = (pointsInPlay * CpointsInPlay + 1) * (Game.getPointValue(card) * Cpoints + 1) * (POOS * CPOOS + 1) * (cardsPlayedOfSuit * CcardsPlayedOfSuit + 1) * (getValueRatio(card, cardsPlayed, myCards) * CVR + 1);
        } else {
            risk = (pointsInPlay * CpointsInPlay + 1) * (Game.getPointValue(card) * Cpoints + 1) * (POOS * CPOOS + 1) * (cardsPlayedOfSuit * CcardsPlayedOfSuit + 1) * (getValueRatio(card, cardsPlayed, myCards) * CVR + 1) * Math.min(1, Math.max(0, card.getValue() - maxCard));
        }

        if (Double.isNaN(risk)) {
            return 0.5;
        } else {
            return risk;
        }
    }

    private boolean havesuit(char suit) {
//        System.out.println("Currently, checking if we have a " + ((char) suit) + ".");

        boolean havesuit = false;
        for (Card cardIHave : myCards) {
            if (this.cardPlayed[myCards.indexOf(cardIHave)] == false && cardIHave.getSuit() == suit) {
                havesuit = true;
            }
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
//        System.out.print("The cards " + id + " has left are ");

//        for (int i = 0; i < myCards.size(); i++) {
//            if (!cardPlayed[i]) {
//                System.out.print(myCards.get(i) + " ");
//            }
//        }

//        System.out.println();


        cardsPlayedOfSuit = 0;
        pointsInPlay = 0;
        POOS = 0;


        // CASE 1: There are already cards on the table.
        if (currentlyOnTable.size() > 0) {
//            System.out.println("There are already cards on the table.");

            // Get the starting suit
            char startingSuit = currentlyOnTable.get(0).getSuit();
//            System.out.println("The starting suit is " + startingSuit);


            // Add the cards on the table to cards that the (remaining) players can't have
            for (Card c : currentlyOnTable) {
                cardsPlayed.add(c);
            }

            // Update POOS values

            for (Card c : cardsPlayed) {
                pointsInPlay += Game.getPointValue(c);
                if (c.getSuit() == startingSuit) {
                    cardsPlayedOfSuit++;
                }
            }

            for (int i = id + 1; i < starting; i++, i %= 4) {
                if (suitsEmpty[i][getSuitNumber(startingSuit)]) POOS++;
            }


            // Choose which card to play
            if (havesuit(currentlyOnTable.get(0).getSuit())) { // If I have the suit, run the haveSuit choosing function
                return myCards.get(playcard_havesuit(currentlyOnTable));
            } else { // If I don't have the suit, run the dontHaveSuit choosing function
                return myCards.get(playcard_donthavesuit());
            }
        } else

        // CASE 2: There are no cards already on the table.
        { // In this case, we are starting.
            return myCards.get(playcard_starting());
        }
    }

    @Override
    public void getFinalCards(List<Card> finalCards) {
        int startingSuit = finalCards.get(0).getSuit();

        for (Card cardInTrick : finalCards) {
            if (cardInTrick.getSuit() != startingSuit) {
                suitsEmpty[finalCards.indexOf(cardInTrick)][getSuitNumber(startingSuit)] = true;
            }
            if (cardInTrick.getSuit() == 'h') {
                heartBroken = true;
            }

            if (!cardsPlayed.contains(cardInTrick)) {
                cardsPlayed.add(cardInTrick);
            }

            if (myCards.contains(cardInTrick)) {
                int index = myCards.indexOf(cardInTrick);
                cardPlayed[index] = true;
            }
        }


//        System.out.println("ID: " + id + "; Getting the final cards: " + finalCards);

//        int removeIndex = -1;

//        int startingSuit = finalCards.get(0).getSuit();
//        for (int i = 0; i < 4; i++) {
//            Card currentCard = finalCards.get(i);
//            System.out.println("Currently looking at the card " + currentCard);

//            if (finalCards.get(i).getSuit() != startingSuit) {
//                suitsEmpty[i][getSuitNumber(startingSuit)] = true;
//            }
//
//            if (!cardsPlayed.contains(finalCards.get(i))) {
//                cardsPlayed.add(finalCards.get(i));
//            }

//            for (int i1 = 0; i1 < myCards.size(); i1++) {
//                Card myCard = myCards.get(i1);
//                if (myCard.equals(currentCard)) {
//                    removeIndex = i1;
//                }
//            }
//        }
//
//        myCards.remove(removeIndex);
    }


    @Override
    public void setCoefficients(List<Double> coefficients) {
        CpointsInPlay = coefficients.get(0);
        Cpoints = coefficients.get(1);
        CPOOS = coefficients.get(2);
        CcardsPlayedOfSuit = coefficients.get(3);
        CVR = coefficients.get(4);
        playingC = coefficients.get(5);
        startingC = coefficients.get(6);
    }


    @Override
    public void dealCards(List<Card> dealtCards) {
        this.myCards = dealtCards;

        this.cardPlayed = new boolean[13];
        Arrays.fill(this.cardPlayed, false);
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
        return Arrays.asList(CpointsInPlay, Cpoints, CPOOS, CcardsPlayedOfSuit, CVR, playingC, startingC);
    }


    @Override
    public void setID(int id) {

        this.id = id;

    }
}
