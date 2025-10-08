package com.ogcardgame.model;

public class Card {
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public String getId() {
        return rank.toString() + "-" + suit.toString();
    }
    
    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
