package com.ogcardgame.dto;

import com.ogcardgame.model.*;
import java.util.List;

public class GameStateDTO {
    private String gameID;
    private GamePhase phase;
    private String currentPlayerId;
    private List<PlayerDTO> players;
    private List<Card> pile;
    private boolean gameOver;
    private String winnerId;

    public GameStateDTO(String gameID, List<PlayerDTO> players, List<Card> pile, String currentPlayerId, GamePhase phase) {
        this.gameID = gameID;
        this.players = players;
        this.pile = pile;
        this.currentPlayerId = currentPlayerId;
        this.phase = phase;
        this.gameOver = false;
        this.winnerId = null;
    }

    // GETTERS
    public String getGameID() {
        return gameID;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public List<Card> getPile() {
        return pile;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getWinnerId() {
        return winnerId;
    }

    // SETTERS
    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public void setCurrentPlayerId(String currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public void setPlayers(List<PlayerDTO> players) {
        this.players = players;
    }

    public void setPile(List<Card> pile) {
        this.pile = pile;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;

    }
}