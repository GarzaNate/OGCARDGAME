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
        if (phase != GamePhase.DEAL)
            throw new IllegalStateException("Cannot join after deal.");
        players.add(new Player(playerId, "Player " + name + (players.size() + 1)));
    }

    public void startGame() {
        if (phase != GamePhase.DEAL)
            throw new IllegalStateException("Game already started.");
        if (players.size() < 2)
            throw new IllegalStateException("Need at least 2 players.");

        deck.shuffle();
        for (Player player : players) {
            for (int i = 0; i < 3; i++)
                player.getFaceDown().add(deck.draw());
            for (int i = 0; i < 7; i++)
                player.getHand().add(deck.draw());
        }
        phase = GamePhase.SELECT_FACE_UP;
    }

    public boolean selectFaceUpCards(String playerId, List<String> cardIds) {
        if (phase != GamePhase.SELECT_FACE_UP)
            return false;

        Player player = getPlayer(playerId);
        if (cardIds.size() != 3)
            return false;

        List<Card> toSelect = player.getHand().stream()
                .filter(c -> cardIds.contains(c.getId()))
                .collect(Collectors.toList());

        if (toSelect.size() != 3)
            return false;

        player.getFaceUp().addAll(toSelect);
        player.getHand().removeAll(toSelect);

        if (players.stream().allMatch(p -> p.getFaceUp().size() == 3)) {
            phase = GamePhase.PLAY_FROM_HAND;
        }
        return true;
    }

    public boolean playCards(String playerId, java.util.List<Card> cardsToPlay) {
        if (phase != GamePhase.PLAY_FROM_HAND && phase != GamePhase.PLAY_FROM_FACE_UP
                && phase != GamePhase.PLAY_FROM_FACE_DOWN)
            return false;

        Player player = getPlayer(playerId);

        // Only current player can act (modify if slap rules apply)
        if (!getCurrentPlayer().equals(player))
            return false;

        // --- Phase-based restriction ---
        if (phase == GamePhase.PLAY_FROM_FACE_DOWN && cardsToPlay.size() > 1) {
            // Can only play one card when face-down
            return false;
        }

        // Ensure all cards are actually owned by the player
        boolean ownsAll = cardsToPlay.stream().allMatch(c -> player.getHand().contains(c) ||
                player.getFaceUp().contains(c) ||
                player.getFaceDown().contains(c));
        if (!ownsAll)
            return false;

        // Validate the selection
        if (!RuleEngine.isValidPlay(cardsToPlay, pile)) {
            // If invalid and no valid plays exist, player picks up pile
            if (!hasAnyValidPlay(player)) {
                player.addToHand(pile.getCards());
                pile.clearPile();
                return true;
            }
            return false;
        }

        // --- Play cards ---
        for (Card card : cardsToPlay) {
            player.getHand().remove(card);
            player.getFaceUp().remove(card);
            player.getFaceDown().remove(card);
        }
        pile.addCards(cardsToPlay);

        Rank playedRank = cardsToPlay.get(0).getRank();

        // --- Handle special cards ---
        if (playedRank == Rank.TWO) {
            pile.clearPile(); // reset pile, same player continues
            return true;
        }
        if (playedRank == Rank.TEN) {
            pile.clearPile(); // burn pile, same player continues
            return true;
        }
        if (RuleEngine.isBomb(pile)) {
            pile.clearPile(); // 4 of same rank bomb
            return true;
        }

        // --- Normal turn advancement ---
        drawUpToFour(player);
        advanceTurn();

        return true;
    }

    private boolean hasAnyValidPlay(Player player) {
        java.util.List<Card> allCards = new java.util.ArrayList<>();
        allCards.addAll(player.getHand());
        allCards.addAll(player.getFaceUp());
        allCards.addAll(player.getFaceDown());

        // Check all possible single-card plays
        for (Card card : allCards) {
            if (RuleEngine.isValidPlay(java.util.Collections.singletonList(card), pile)) {
                return true;
            }
        }
        return false;
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
                .map(p -> {
                    PlayerDTO dto = new PlayerDTO(p, p.getPlayerId().equals(requestingPlayerId));
                    dto.setCurrentTurn(p.equals(getCurrentPlayer()));
                    return dto;
                })
                .collect(Collectors.toList());

        return new GameStateDTO(
                id,
                playerDTOs,
                pile.getCards(),
                getCurrentPlayer().getPlayerId(),
                phase);
    }
}
