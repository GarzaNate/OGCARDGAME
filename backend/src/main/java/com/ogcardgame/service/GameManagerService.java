package com.ogcardgame.service;

import com.ogcardgame.dto.GameStateDTO;
import com.ogcardgame.model.Card;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameManagerService {

    private final Map<String, GameManager> games = new HashMap<>();

    public String createGame() {
        String gameId = generateGameId();
        games.put(gameId, new GameManager(gameId));
        System.out.println("Created game with ID: " + gameId);
        return gameId;
    }

    private String generateGameId() {
        return "game-" + UUID.randomUUID().toString().substring(0,6);
    }

    public GameManager getOrCreateGame(String gameId) {
        return games.computeIfAbsent(gameId, id -> new GameManager(id));
    }

    public void addPlayer(String gameId, String playerId, String name) {
        GameManager game = getOrCreateGame(gameId);
        game.addPlayer(playerId, name);
    }

    public void startGame(String gameId, String playerId) {
        GameManager game = getOrCreateGame(gameId);
        game.startGame();
    }

    public void selectFaceUpCards(String gameId, String playerId, List<String> cardIds) {
        GameManager game = getOrCreateGame(gameId);
        game.selectFaceUpCards(playerId, cardIds);
    }

    public void playCards(String gameId, String playerId, List<String> cardIds) {
        GameManager game = getOrCreateGame(gameId);

        // Convert card IDs into Card objects
        List<Card> cardsToPlay = new ArrayList<>();
        for (String cardId : cardIds) {
            Card card = game.getPlayer(playerId).findCardById(cardId);
            if (card != null) {
                cardsToPlay.add(card);
            }
        }

        if (!cardsToPlay.isEmpty()) {
            game.playCards(playerId, cardsToPlay);
        }
    }

    public void playCard(String gameId, String playerId, String cardId) {
        playCards(gameId, playerId, Collections.singletonList(cardId));
    }

    public GameStateDTO getGameState(String gameId) {
        GameManager game = getOrCreateGame(gameId);
        return game.toGameStateDTO("dummy"); // You can pass the player ID if needed
    }
}
