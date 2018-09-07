package org.tuomilabs.heart.heart;

public class Card {
    private int value;
    private char suit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (value != card.value) return false;
        return suit == card.suit;
    }

    @Override
    public int hashCode() {
        int result = value;
        result = 31 * result + (int) suit;
        return result;
    }

    public Card(int value, char suit) {
        this.value = value;
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }

    public char getSuit() {
        return suit;
    }
}