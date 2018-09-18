package org.tuomilabs.heart.heart;

public class Card {
    private final int value;
    private final char suit;

    @Override
    public String toString() {
        return "" + (this.value < 11 ? this.value + "" : this.value == 11 ? "J" : this.value == 12 ? "Q" : this.value == 13 ? "K" : "A") + "" + (this.suit == 's' ? "♠" : this.suit == 'c' ? "♣" : this.suit == 'd' ? "♦" : "♥") + "";
    }

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
