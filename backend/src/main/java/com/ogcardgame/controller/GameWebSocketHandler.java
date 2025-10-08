package com.ogcardgame.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogcardgame.dto.GameStateDTO;
import com.ogcardgame.service.GameManagerService;
import com.ogcardgame.service.GameManager;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

public class GameWebSocketHandler extends TextWebSocketHandler {

    private final GameManagerService gameManagerService;
    private final Map<String, WebSocketSession> sessions = new HashMap<>();
    private final Map<String, String> sessionToPlayer = new HashMap<>();
    private final Map<String, String> sessionToGame = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameWebSocketHandler(GameManagerService gameManagerService) {
        this.gameManagerService = gameManagerService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        System.out.println("New connection established: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> data = objectMapper.readValue(message.getPayload(),
                new TypeReference<Map<String, Object>>() {
                });

        String action = (String) data.get("action");
        String gameId = (String) data.get("gameId");
        String playerId = (String) data.get("playerId");

        switch (action) {
            case "join":
                String name = (String) data.get("name");
                gameManagerService.addPlayer(gameId, playerId, name);
                sessionToPlayer.put(session.getId(), playerId);
                sessionToGame.put(session.getId(), gameId);
                break;

            case "start":
                handleStart(gameId, playerId);
                break;

            case "play":
                handlePlay(gameId, playerId, data);
                break;

            case "selectFaceUp":
                handleSelectFaceUp(gameId, playerId, data);
                break;

            default:
                System.out.println("Unknown action: " + action);
        }

        broadcastGameState(gameId);
    }

    public void sendError(WebSocketSession session, String error) throws IOException {
        session.sendMessage(new TextMessage("{\"error\":\"" + error + "\"}"));
    }

    private void handleStart(String gameId, String playerId) {
        gameManagerService.startGame(gameId, playerId);
    }

    private void handlePlay(String gameId, String playerId, Map<String, Object> data) {
        String cardId = (String) data.get("cardId");
        gameManagerService.playCard(gameId, playerId, cardId);
    }

    private void handleSelectFaceUp(String gameId, String playerId, Map<String, Object> data) {
        List<String> cardIds = objectMapper.convertValue(data.get("cardIds"),
                new TypeReference<List<String>>() {
                });
        gameManagerService.selectFaceUpCards(gameId, playerId, cardIds);
    }

    private void broadcastGameState(String gameId) throws Exception {
        GameStateDTO dto = gameManagerService.getGameState(gameId);
        String json = objectMapper.writeValueAsString(dto);

        for (Map.Entry<String, String> entry : sessionToGame.entrySet()) {
            if (entry.getValue().equals(gameId)) {
                WebSocketSession playerSession = sessions.get(entry.getKey());
                if (playerSession != null && playerSession.isOpen()) {
                    playerSession.sendMessage(new TextMessage(json));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        sessionToPlayer.remove(session.getId());
        sessionToGame.remove(session.getId());
        System.out.println("Connection closed: " + session.getId() + " with status: " + status);
    }
}
