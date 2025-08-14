package com.ogcardgame.controller;

import com.ogcardgame.service.GameManagerService;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.*;

public class GameWebSocketHandler extends TextWebSocketHandler {
    private final GameManagerService gameManagerService;
    private final Map<String, WebSocketSession> sessions = new HashMap<>();

    public GameWebSocketHandler(GameManagerService gameManagerService) {
        this.gameManagerService = gameManagerService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        System.out.println("New connection established: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received message from " + session.getId() + ": " + payload);

        if ("startGame".equals(payload)) {
            // Example action: start a game
            gameManagerService.getGameManager().startGame();
            session.sendMessage(new TextMessage("Game started"));
            broadcastGameState();
        }
    }

    private void broadcastGameState() throws Exception {
        var dto = gameManagerService.getGameManager().toGameStateDTO();
        String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto);
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(json));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        System.out.println("Connection closed: " + session.getId() + " with status: " + status);
    }
}
