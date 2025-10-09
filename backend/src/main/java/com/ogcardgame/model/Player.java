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

    // HAND
    public void addToHand(Card card) {
        hand.add(card);
    }

    public void addToHand(Collection<Card> cards) {
        if (cards != null && !cards.isEmpty()) {
            hand.addAll(cards);
        }
    }

    public void removeFromHand(Card card) {
        hand.remove(card);
    }

     public void removeFromHand(Collection<Card> cards) {
        hand.removeAll(cards);
    }

    // FACE UP
    public void addToFaceUp(Card card) {
        faceUp.add(card);
    }
    
    public void addToFaceUp(Collection<Card> cards) {
        if (cards != null && !cards.isEmpty()) {
            faceUp.addAll(cards);
        }
    }

    public void removeFromFaceUp(Card card) {
        faceUp.remove(card);
    }

    public void removeFromFaceUp(Collection<Card> cards) {
        faceUp.removeAll(cards);
    }

    // FACE DOWN
    public void addToFaceDown(Card card) {
        if (faceDown.size() < 3) {
            faceDown.add(card);
        }    
    }

    public void addToFaceDown(Collection<Card> cards) {
        if (cards != null && !cards.isEmpty()) {
            for (Card card : cards) {
                addToFaceDown(card);
            }
        }
    }

    public void removeFromFaceDown(Card card) {
        faceDown.remove(card);
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

    public String getPlayerId() {
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

    public void addCardsToHand(Collection<Card> cards) {
        addToHand(cards);
    }

    public Card findCardById(String cardId) {
        for (Card card : getHand()) {
            if (card.getId().equals(cardId)) {
                return card;
            }
        }
        for (Card card : getFaceUp()) {
            if (card.getId().equals(cardId)) {
                return card;
            }
        }
        for (Card card : getFaceDown()) {
            if (card.getId().equals(cardId)) {
                return card;
            }
        }
        return null;
    }
}
