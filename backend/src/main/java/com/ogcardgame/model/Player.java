package com.ogcardgame.model;

import java.util.*;

public class Player {
    private final String id;
    private final String name;

    private final List<Card> hand = new ArrayList<>();
    private final List<Card> faceDownCards = new ArrayList<>();
    private final List<Card> faceUpCards = new ArrayList<>();

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Players should only have 3 FaceDown cards 
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

}
