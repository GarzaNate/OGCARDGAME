package com.ogcardgame.service;

import org.springframework.stereotype.Service;

@Service
public class GameManagerService {
    private final GameManager gameManager = new GameManager("defaultGame");

    public GameManager getGameManager() {
        return gameManager;
    }
}
