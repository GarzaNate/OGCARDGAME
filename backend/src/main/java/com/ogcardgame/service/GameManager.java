package com.ogcardgame.service;

import com.ogcardgame.model.*;
import com.ogcardgame.dto.*;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
    private final String id;
    private final List<Player> players = new ArrayList<>();
    private final Deck deck = new Deck();
    private final Pile pile = new Pile();
    private int currentPlayerIndex = 0;
    private GamePhase phase = GamePhase.DEAL;

    public GameManager(String id) {
        this.id = id;
    }

    public void addPlayer(String playerId, String name) {
        if (phase != GamePhase.DEAL) throw new IllegalStateException("Cannot join after deal.");
        players.add(new Player(playerId, "Player " + name + (players.size() + 1)));
    }

    public void startGame() {
        if (phase != GamePhase.DEAL) throw new IllegalStateException("Game already started.");
        if (players.size() < 2) throw new IllegalStateException("Need at least 2 players.");

        deck.shuffle();
        for (Player player : players) {
            for (int i = 0; i < 3; i++) player.getFaceDown().add(deck.draw());
            for (int i = 0; i < 6; i++) player.getHand().add(deck.draw());
        }
        phase = GamePhase.SELECT_FACE_UP;
    }

    public boolean selectFaceUpCards(String playerId, List<String> cardIds) {
        if (phase != GamePhase.SELECT_FACE_UP) return false;

        Player player = getPlayer(playerId);
        if (cardIds.size() != 3) return false;

        List<Card> toSelect = player.getHand().stream()
                .filter(c -> cardIds.contains(c.getId()))
                .collect(Collectors.toList());

        if (toSelect.size() != 3) return false;

        player.getFaceUp().addAll(toSelect);
        player.getHand().removeAll(toSelect);

        if (players.stream().allMatch(p -> p.getFaceUp().size() == 3)) {
            phase = GamePhase.PLAY_FROM_HAND;
        }
        return true;
    }

    public boolean playCard(String playerId, Card card) {
        if (phase != GamePhase.PLAY_FROM_HAND && phase != GamePhase.PLAY_FROM_FACE_UP && phase != GamePhase.PLAY_FROM_FACE_DOWN)
            return false;

        Player player = getPlayer(playerId);

        // Only current player can act
        if (!getCurrentPlayer().equals(player)) return false;

        // Must own the card
        if (!player.getHand().contains(card) &&
            !player.getFaceUp().contains(card) &&
            !player.getFaceDown().contains(card)) {
            return false;
        }

        // Check validity
        if (!RuleEngine.isValidPlay(card, pile)) return false;

        // Place card
        pile.addCards(Collections.singletonList(card));
        player.getHand().remove(card);
        player.getFaceUp().remove(card);
        player.getFaceDown().remove(card);

        // Handle special cards
        if (card.getRank() == Rank.TWO) {
            // Reset pile but same player goes again
            pile.clearPile();
            return true; // don’t advance turn
        }
        if (card.getRank() == Rank.TEN) {
            // Burn pile and same player goes again
            pile.clearPile();
            return true;
        }
        if (RuleEngine.isBomb(pile)) {
            pile.clearPile();
            return true; // same player goes again
        }

        // Normal turn advancement
        drawUpToFour(player);
        advanceTurn();

        return true;
    }

    private void drawUpToFour(Player player) {
        while (player.getHand().size() < 4 && !deck.isEmpty()) {
            player.getHand().add(deck.draw());
        }
    }

    private void advanceTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        checkGameOver();
    }

    private void checkGameOver() {
        Optional<Player> winner = players.stream()
                .filter(p -> p.getHand().isEmpty()
                        && p.getFaceUp().isEmpty()
                        && p.getFaceDown().isEmpty())
                .findFirst();
        if (winner.isPresent()) {
            phase = GamePhase.END;
        }
    }

    private Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Player getPlayer(String playerId) {
        return players.stream()
                .filter(p -> p.getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow();
    }

    // --- DTOs ---
    public GameStateDTO toGameStateDTO(String requestingPlayerId) {
        List<PlayerDTO> playerDTOs = players.stream()
                .map(p -> new PlayerDTO(p, p.getPlayerId().equals(requestingPlayerId)))
                .collect(Collectors.toList());

        return new GameStateDTO(
                id,
                playerDTOs,
                pile.getCards(),
                getCurrentPlayer().getPlayerId(),
                phase
        );
    }
}
