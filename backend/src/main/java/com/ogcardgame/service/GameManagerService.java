package com.ogcardgame.service;

import com.ogcardgame.dto.GameStateDTO;
import com.ogcardgame.model.Card;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameManagerService {

    private final Map<String, GameManager> games = new HashMap<>();

    private GameManager getOrCreateGame(String gameId) {
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
