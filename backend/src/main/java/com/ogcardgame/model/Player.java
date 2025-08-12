package com.ogcardgame.model;

import java.util.*;

public class Player {
    // ATTRIBUTES
    private final String id;
    private final String name;

    // COLLECTIONS
    private final List<Card> hand = new ArrayList<>();
    private final List<Card> faceDownCards = new ArrayList<>();
    private final List<Card> faceUpCards = new ArrayList<>();

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // ADDERS
    public void addToFaceDown(Card card) {
        if (faceDownCards.size() < 3) {
            faceDownCards.add(card);
        }    
    }

    public void addToFaceUp(Card card) {
        faceUpCards.add(card);
    }

    public void addToHand(Card card) {
        hand.add(card);
    }

    // GETTERS
    public List<Card> gethand() {
        return hand;
    }

    public List<Card> getFaceDownCards() {
        return faceDownCards;
    }

    public List<Card> getFaceUpCards() {
        return faceUpCards;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // GAME LOGIC HELPERS
    public boolean hasFaceDownCards() {
        return !faceDownCards.isEmpty();
    }

    public boolean hasFaceUpCards() {
        return !faceUpCards.isEmpty();
    }

    public boolean hasCardsInHand() {
        return !hand.isEmpty();
    }

    public boolean outOfCards() {
        return !hasFaceDownCards() && !hasFaceUpCards() && !hasCardsInHand();
    }
}
