package com.ogcardgame.service;

import com.ogcardgame.model.*;
import java.util.*;

public class GameManager {

    private final String gameId;
    private final List<Player> players = new ArrayList<>();
    private Deck deck;
    private final Pile pile = new Pile();
    
    private int currPlayerIndex = 0;
    private GamePhase phase = GamePhase.DEAL;

    private boolean gameOver = false;
    private Player winner = null;

    public GameManager(String gameId) {
        this.gameId = gameId;
    }

    // PLAYER MANAGEMENT
    // -----------------
    public void addPlayer(Player player) {
        if (phase != GamePhase.DEAL) {
            throw new IllegalStateException("Cannot add players after the game has started.");
        }
        if (players.size() >= 4) {
            throw new IllegalStateException("Cannot add more than 4 players.");
        }
        players.add(player);
    }

    // GAME START
    // --------------
    public void startGame() {
        if (players.size() < 2 || players.size() > 4) {
            throw new IllegalStateException("Game requires 2 to 4 players.");
        }

        deck = new Deck();
        deck.shuffle();

        // DEAL INITIAL CARDS TO PLAYERS
        for (Player player : players) {
            for (int i = 0; i < 3; i++) {
                player.addToFaceDown(deck.drawCard());
            }
            for (int i = 0; i < 7; i++) {
                player.addToHand(deck.drawCard());
            }
        }
        phase = GamePhase.SELECT_FACE_UP;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public Player getCurrentPlayer() {
        return players.get(currPlayerIndex);
    }

    public void nextTurn() {
        currPlayerIndex = (currPlayerIndex + 1) % players.size();

        while(players.get(currPlayerIndex).outOfCards()) {
            currPlayerIndex = (currPlayerIndex + 1) % players.size();
        }
    }

    // PHASE 1: FACE UP SELECTION
    // --------------------------------
    public boolean selectFaceUpCards(String playerId, List<Card> selectedCards) {
        if (phase != GamePhase.SELECT_FACE_UP) return false;

        Player player = getPlayerById(playerId);
        if (!player.getHand().containsAll(selectedCards)) return false;
        if (selectedCards.size() != 3) return false;

        

        return true;
    }

    // HELPERS
    private Player getPlayerById(String playerId) {
        return players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        return winner;
    }
}