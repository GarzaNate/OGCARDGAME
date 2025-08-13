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

    // PHASE 1: FACE UP SELECTION
    // --------------------------------
    public boolean selectFaceUpCards(String playerId, List<Card> selectedCards) {
        if (phase != GamePhase.SELECT_FACE_UP)
            return false;

        Player player = getPlayerById(playerId);
        if (!player.getHand().containsAll(selectedCards))
            return false;
        if (selectedCards.size() != 3)
            return false;

        player.removeFromHand(selectedCards);
        selectedCards.forEach(player::addToFaceUp);

        boolean allSelected = players.stream()
                .allMatch(p -> p.getFaceUp().size() == 3);

        if (allSelected)
            phase = GamePhase.PLAY_FROM_HAND;
        return true;
    }

    // PHASES 2-4: PLAY CARDS
    // --------------------------------

    public boolean playCards(String playerId, List<Card> cardsToPlay) {
        if (gameOver)
            return false;
        Player player = getPlayerById(playerId);

        switch (phase) {
            case PLAY_FROM_HAND:
                if (!player.getHand().containsAll(cardsToPlay))
                    return false;
                return playFromHand(player, cardsToPlay);

            case PLAY_FROM_FACE_UP:
                if (!player.getFaceUp().containsAll(cardsToPlay))
                    return false;
                return playFromFaceUp(player, cardsToPlay);

            case PLAY_FROM_FACE_DOWN:
                if (cardsToPlay.size() != 1)
                    return false;
                if (!player.getFaceDown().contains(cardsToPlay.get(0)))
                    return false;
                return playFromFaceDown(player, cardsToPlay.get(0));

            default:
                return false;
        }
    }

    private boolean playFromHand(Player player, List<Card> cards) {
        if (!isValidPlay(cards)) {
            player.addToHand(pile.clearPile());
            drawUpToFour(player);
            nextTurn();
            return false;
        }

        removeCardsFromPlayer(player, cards);
        pile.addCards(cards);
        if (isBomb(pile)) pile.clearPile();

        drawUpToFour(player);

        if (player.getHand().isEmpty() && deck.isEmpty()) {
            phase = GamePhase.PLAY_FROM_FACE_UP;
        }

        checkGameOver();
        nextTurn();
        return true;
    }

    private boolean playFromFaceUp(Player player, List<Card> cards) {
        if (!isValidPlay(cards)) {
            player.addToHand(pile.clearPile());
            drawUpToFour(player);
            nextTurn();
            return false;
        }

        removeCardsFromPlayer(player, cards);
        pile.addCards(cards);
        if (isBomb(pile)) pile.clearPile();

        drawUpToFour(player);

        if (player.getFaceUp().isEmpty() && deck.isEmpty()) {
            phase = GamePhase.PLAY_FROM_FACE_DOWN;
        }

        checkGameOver();
        nextTurn();
        return true;
    }

    private boolean playFromFaceDown(Player player, Card card) {
        // Blind play - must play card, then check if valid

        player.removeFromFaceDown(card);
        pile.addCards(List.of(card));

        if (!isValidPlay(List.of(card))) {
            player.addToHand(pile.clearPile());
            drawUpToFour(player);
        }
        checkGameOver();
        nextTurn();
        return true;
    }

    // HELPERS
    // ----------------

    private void drawUpToFour(Player player) {
        while (player.getHand().size() < 4 && !deck.isEmpty()) {
            player.addToHand(deck.drawCard());
        }
    }

    private boolean isValidPlay(List<Card> cardsToPlay) {
        if (cardsToPlay.isEmpty())
            return false;

        Rank playedRank = cardsToPlay.get(0).getRank();

        boolean allSameRank = cardsToPlay.stream()
                .allMatch(card -> card.getRank() == playedRank);

        if (!allSameRank)
            return false;
        if (pile.isEmpty())
            return true;

        Rank topRank = pile.getTopRank();

        if (playedRank == Rank.TWO)
            return true;
        if (playedRank == Rank.TEN)
            return true;

        return playedRank.getValue() >= topRank.getValue();
    }

    private boolean isBomb(Pile pile) {
        List<Card> pileCards = pile.getCards();
        if (pileCards.size() < 4)
            return false;
        Rank firstRank = pileCards.get(pileCards.size() - 1).getRank();

        long count = pileCards.stream()
                .filter(card -> card.getRank() == firstRank)
                .count();

        return count >= 4;
    }

    private void checkGameOver() {
        long activePlayers = players.stream()
                .filter(player -> !player.outOfCards())
                .count();

        if (activePlayers <= 1) {
            gameOver = true;
            winner = players.stream()
                    .filter(player -> !player.outOfCards())
                    .findFirst()
                    .orElse(null);
        }
        phase = GamePhase.END;
    }

    private void removeCardsFromPlayer(Player player, List<Card> cards) {
        player.removeFromHand(cards);
    }

    private Player getPlayerById(String playerId) {
        return players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
    }

    public GamePhase getPhase() {
        return phase;
    }

    public Player getCurrentPlayer() {
        return players.get(currPlayerIndex);
    }

    public void nextTurn() {
        currPlayerIndex = (currPlayerIndex + 1) % players.size();

        while (players.get(currPlayerIndex).outOfCards()) {
            currPlayerIndex = (currPlayerIndex + 1) % players.size();
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        return winner;
    }
}