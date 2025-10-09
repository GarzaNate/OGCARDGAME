package com.ogcardgame.dto;

import com.ogcardgame.model.*;
import java.util.List;

public class PlayerDTO {

    private String playerId;
    private String name;
    private List<Card> hand;
    private int handSize;
    private List<Card> faceUp;
    private int faceDownCount;
    private boolean isCurrentTurn;

    public PlayerDTO(Player player, boolean isSelf) {
        this.playerId = player.getPlayerId();
        this.name = player.getName();
        this.handSize = player.getHand().size();
        this.faceUp = player.getFaceUp();
        this.faceDownCount = player.getFaceDown().size();
        this.isCurrentTurn = false; // This should be set based on game state
        this.hand = isSelf ? player.getHand() : null;
    }

    // GETTERS
    public String getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getHandSize() {
        return handSize;
    }

    public List<Card> getFaceUp() {
        return faceUp;
    }

    public int getFaceDownCount() {
        return faceDownCount;
    }

    public boolean isCurrentTurn() {
        return isCurrentTurn;
    }

    // SETTERS
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public void setHandSize(int handSize) {
        this.handSize = handSize;
    }

    public void setFaceUp(List<Card> faceUp) {
        this.faceUp = faceUp;
    }

    public void setFaceDownCount(int faceDownCount) {
        this.faceDownCount = faceDownCount;
    }

    public void setCurrentTurn(boolean isCurrentTurn) {
        this.isCurrentTurn = isCurrentTurn;
    }
}
