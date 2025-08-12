package com.ogcardgame.model;

import java.util.*;

public class Player {
    // ATTRIBUTES
    private final String id;
    private final String name;

    // COLLECTIONS
    private final List<Card> hand = new ArrayList<>();
    private final List<Card> faceDown = new ArrayList<>();
    private final List<Card> faceUp = new ArrayList<>();

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // ADDERS
    public void addToFaceDown(Card card) {
        if (faceDown.size() < 3) {
            faceDown.add(card);
        }    
    }

    public void addToFaceUp(Card card) {
        faceUp.add(card);
    }

    public void addToHand(Card card) {
        hand.add(card);
    }

    // GETTERS
    public List<Card> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public List<Card> getFaceDown() {
        return  Collections.unmodifiableList(faceDown);
    }

    public List<Card> getFaceUp() {
        return Collections.unmodifiableList(faceUp);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // GAME LOGIC HELPERS
    public boolean hasFaceDown() {
        return !faceDown.isEmpty();
    }

    public boolean hasFaceUp() {
        return !faceUp.isEmpty();
    }

    public boolean hasCardsInHand() {
        return !hand.isEmpty();
    }

    public boolean outOfCards() {
        return !hasFaceDown() && !hasFaceUp() && !hasCardsInHand();
    }
}
