package org.tuomilabs.heart.heart;

import java.util.List;

public interface Algorithm {
    Card playCard(List<Card> currentlyOnTable);
    void getFinalCards(List<Card> finalCards);
}
