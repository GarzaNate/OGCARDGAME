package com.ogcardgame.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogcardgame.dto.GameStateDTO;
import com.ogcardgame.service.GameManagerService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Component
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
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("Transport error in session " + session.getId() + ": " + exception.getMessage());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received message from session " + session.getId() + ": " + payload);

        Map<String, Object> data;
        try {
            data = objectMapper.readValue(message.getPayload(),
                    new TypeReference<Map<String, Object>>() {
                    });
        } catch (Exception e) {
            // Bad JSON — notify sender and return
            sendError(session, "Invalid JSON payload");
            return;
        }

        // Accept either "action" or "type" from clients.
        String action = null;
        if (data.get("action") instanceof String) {
            action = (String) data.get("action");
        } else if (data.get("type") instanceof String) {
            action = (String) data.get("type");
        }

        String gameId = data.get("gameId") instanceof String ? (String) data.get("gameId") : null;
        String playerId = data.get("playerId") instanceof String ? (String) data.get("playerId") : null;

        if (action == null) {
            sendError(session, "Missing action/type field");
            return;
        }

        try {
            switch (action) {
                case "join":
                    // 'name' should be provided by client
                    String name = data.get("name") instanceof String ? (String) data.get("name") : null;
                    if (gameId == null || playerId == null || name == null) {
                        sendError(session, "join requires gameId, playerId, and name");
                    } else {
                        gameManagerService.addPlayer(gameId, playerId, name);
                        sessionToPlayer.put(session.getId(), playerId);
                        sessionToGame.put(session.getId(), gameId);
                    }
                    break;

                case "start":
                    if (gameId == null || playerId == null) {
                        sendError(session, "start requires gameId and playerId");
                    } else {
                        handleStart(gameId, playerId);
                    }
                    break;

                case "play":
                    if (gameId == null || playerId == null) {
                        sendError(session, "play requires gameId and playerId");
                    } else {
                        handlePlay(gameId, playerId, data);
                    }
                    break;

                case "selectFaceUp":
                    if (gameId == null || playerId == null) {
                        sendError(session, "selectFaceUp requires gameId and playerId");
                    } else {
                        handleSelectFaceUp(gameId, playerId, data);
                    }
                    break;

                default:
                    sendError(session, "Unknown action: " + action);
                    break;
            }
        } catch (Exception e) {
            // catch exceptions from gameManagerService and send them back to the calling
            // client
            System.err.println("Error handling action " + action + ": " + e.getMessage());
            try {
                sendErrorIfSessionKnown(playerId, e.getMessage() != null ? e.getMessage() : "Server error");
            } catch (IOException ignored) {
            }
        }

        // Only broadcast if we have a gameId (and something changed)
        if (gameId != null) {
            try {
                broadcastGameState(gameId);
            } catch (Exception e) {
                System.err.println("Failed to broadcast game state: " + e.getMessage());
            }
        }
    }

    public void sendError(WebSocketSession session, String error) throws IOException {
        session.sendMessage(new TextMessage("{\"error\":\"" + error + "\"}"));
    }

    private void handleStart(String gameId, String playerId) {
        gameManagerService.startGame(gameId, playerId);
    }

    private void handlePlay(String gameId, String playerId, Map<String, Object> data) {
        // Prefer cardIds (multi-play). If not present, fall back to single cardId.
        Object maybeIds = data.get("cardIds");
        if (maybeIds != null) {
            List<String> cardIds = objectMapper.convertValue(maybeIds,
                    new TypeReference<List<String>>() {
                    });
            try {
                gameManagerService.playCards(gameId, playerId, cardIds);
            } catch (Exception e) {
                // sendError to the session that initiated this action
                try {
                    sendErrorIfSessionKnown(playerId, e.getMessage());
                } catch (IOException ignored) {
                }
            }
        } else {
            String cardId = (String) data.get("cardId");
            try {
                gameManagerService.playCard(gameId, playerId, cardId); // wrapper calls playCards(...)
            } catch (Exception e) {
                try {
                    sendErrorIfSessionKnown(playerId, e.getMessage());
                } catch (IOException ignored) {
                }
            }
        }
    }

    // Helper to send error to the originating player's session
    private void sendErrorIfSessionKnown(String playerId, String message) throws IOException {
        String sessionId = sessionToPlayer.entrySet().stream()
                .filter(e -> e.getValue().equals(playerId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        if (sessionId != null) {
            WebSocketSession s = sessions.get(sessionId);
            if (s != null && s.isOpen())
                sendError(s, message);
        }
    }

    private void handleSelectFaceUp(String gameId, String playerId, Map<String, Object> data) {
        List<String> cardIds = objectMapper.convertValue(data.get("cardIds"),
                new TypeReference<List<String>>() {
                });
        gameManagerService.selectFaceUpCards(gameId, playerId, cardIds);
    }

    private void broadcastGameState(String gameId) throws Exception {
        // Send a tailored game state to each connected session in this game.
        for (Map.Entry<String, String> entry : sessionToGame.entrySet()) {
            if (!entry.getValue().equals(gameId)) continue;

            String sessionId = entry.getKey();
            WebSocketSession playerSession = sessions.get(sessionId);
            if (playerSession == null || !playerSession.isOpen()) continue;

            // Find the playerId associated with this session (may be null)
            String playerId = sessionToPlayer.get(sessionId);

            GameStateDTO dto = gameManagerService.getGameState(gameId, playerId == null ? "" : playerId);
            String json = objectMapper.writeValueAsString(dto);

            playerSession.sendMessage(new TextMessage(json));
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
