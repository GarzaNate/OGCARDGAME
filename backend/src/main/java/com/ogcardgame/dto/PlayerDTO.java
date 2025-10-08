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
        this.handSize = player.getHand().size();

        if (isSelf) {
            this.hand = player.getHand();
        } else {
            this.hand = null; // Hide hand details for other players
        }
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
