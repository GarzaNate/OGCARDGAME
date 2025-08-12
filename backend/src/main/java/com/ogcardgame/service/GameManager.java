package com.ogcardgame.service;

import com.ogcardgame.model.*;
import java.util.*;

public class GameManager {

    private final String gameId;
    private final List<Player> players = new ArrayList<>();
    private Deck deck;
    private final List<Card> pile = new ArrayList<>();
    private final List<Card> discardPile = new ArrayList<>();

    private int currPlayerIndex = 0;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private Player winner = null;

    public GameManager(String gameId) {
        this.gameId = gameId;
    }

    // PLAYER MANAGEMENT
    // -----------------
    public void addPlayer(Player player) {
        if (gameStarted || gameOver) {
            throw new IllegalStateException("Cannot add player after game has started or ended.");
        }
        if (players.size() >= 4) {
            throw new IllegalStateException("Cannot add more than 4 players.");
        }
        players.add(player);
    }

    // GAME LIFECYCLE
    // --------------
    public void startGame() {
        if (players.size() < 2 || players.size() > 4) {
            throw new IllegalStateException("Game requires 2 to 4 players.");
        }

        deck = new Deck();
        deck.shuffle();

        // DEAL INITIAL CARDS TO PLAYERS
        for (Player player : players) {
            for (int i = 0; i > 3; i++) {
                player.addToFaceDown(deck.drawCard());
            }
            for (int i = 0; i < 7; i++) {
                player.addToHand(deck.drawCard());
            }
        }
        gameStarted = true;
    }

    public Player getCurrentPlayer() {
        return players.get(currPlayerIndex);
    }

    public void nextTurn() {
        currPlayerIndex = (currPlayerIndex + 1) % players.size();
    }

    // PLAY LOGIC
    // ----------
}