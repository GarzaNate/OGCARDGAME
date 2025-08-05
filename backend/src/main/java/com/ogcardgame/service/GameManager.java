package com.ogcardgame.service;

import com.ogcardgame.model.*;
import java.util.*;

public class GameManager {
    
    private final List<Player> players = new ArrayList<>();
    private final Deck deck = new Deck();
    private final List<Card> pile = new ArrayList<>();

    private int currentPlayerIndex = 0;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private Player winner = null;

    public void addPlayer(Player player) {
        if( gameStarted || gameOver) {
            throw new IllegalStateException("Cannot add players after the game has started or ended.");
        }
        if (players.size() < 4) {
            players.add(player);
        } else {
            throw new IllegalStateException("Cannot add more than 4 players.");
        }
    }

    public void startGame() {
        if (players.size() < 2) {
            throw new IllegalStateException("At least 2 players are required to start the game.");
        }

        deck.shuffle();
    }
}
