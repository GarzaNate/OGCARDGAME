package com.ogcardgame.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
public class GameManagerService {
    private final Map<String, GameManager> games = new HashMap<>();

    public GameManager getGame(String gameId) {
        return games.computeIfAbsent(gameId, id -> new GameManager(id));
    }
}
