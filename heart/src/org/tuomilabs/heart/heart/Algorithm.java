package org.tuomilabs.heart.heart;

import java.util.List;

public interface Algorithm {
    void setCoefficients(List<Double> coefficients);
    void dealCards(List<Card> dealtCards);
    Card playCard(List<Card> currentlyOnTable);
    void getFinalCards(List<Card> finalCards);
    void getWhoIsStartingNextTrick(int idOfStarting);
    void setID(int id);
    List<Card> getCards();
    List<Double> getCoefficients();
}
