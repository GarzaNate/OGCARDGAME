package com.ogcardgame.model;

import java.util.*;

public class Player {
    private final String name;
    private final List<Card> faceDownCards;
    private final List<Card> handCards;
    private final List<Card> playedCards;

    public Player(String name) {
        this.name = name;
        this.faceDownCards = new ArrayList<>();
        this.handCards = new ArrayList<>();
        this.playedCards = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Card> getFaceDownCards() {
        return Collections.unmodifiableList(faceDownCards);
    }

    public List<Card> getHandCards() {
        return Collections.unmodifiableList(handCards);
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

    public void dealHandCards(List<Card> cards) {
        handCards.clear();
        handCards.addAll(cards);
    }

    public void dealFaceDownCards(List<Card> cards) {
        faceDownCards.clear();
        faceDownCards.addAll(cards);
    }

    public boolean hasCardsLeft() {
        return !faceDownCards.isEmpty() || !handCards.isEmpty();
    }

    public boolean playCard(Card card, List<Card> pile) {
        if (!handCards.contains(card)) {
            return false; // No card in hand
        }

       Card topCard = pile.isEmpty() ? null : pile.get(pile.size() - 1);
        
       if (canPlayCard(card, topCard)) {
            handCards.remove(card);
            playedCards.add(card);
            pile.add(card);
            return true;
        }
        return false; // Card cannot be played
    }

    private boolean canPlayCard(Card card, Card topCard) {
        if (topCard == null) {
            return true; // First card played
        }

        int cardValue = card.getValue();
        int topValue = topCard.getValue();

        return cardValue >= topValue || cardValue == 2 || cardValue == 10;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", faceDownCards=" + faceDownCards +
                ", handCards=" + handCards +
                ", playedCards=" + playedCards +
                '}';
    }
}
