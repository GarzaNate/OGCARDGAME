package com.ogcardgame.model;

import java.util.*;

public class Pile {
    private List<Card> cards;

    public Pile() {
        this.cards = new ArrayList<>();
    }

    public void addCards(List<Card> newCards) {
        if (newCards == null || newCards.isEmpty()) {
            throw new IllegalArgumentException("Cannot add null or empty list of cards.");
        }
        cards.addAll(newCards);
    }
    
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public List<Card> clearPile() {
        List<Card> pileCopy = new ArrayList<>(cards);
        cards.clear();
        return pileCopy;
    }

    public Rank getTopRank() {
        if (cards.isEmpty())
            return null;
        return cards.get(cards.size() - 1).getRank();
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    @Override
    public String toString() {
        return cards.toString();
    }
}
