package com.ogcardgame.controller;

import com.ogcardgame.dto.GameStateDTO;
import com.ogcardgame.service.GameManagerService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "http://localhost:3000") // ✅ Allow React dev frontend
public class GameController {

    private final GameManagerService gameManagerService;

    public GameController(GameManagerService gameManagerService) {
        this.gameManagerService = gameManagerService;
    }

    // ✅ Create a new game
    @PostMapping("/create")
    public String createGame() {
        return gameManagerService.createGame();
    }

    // ✅ Join an existing game by ID
    @PostMapping("/join/{gameId}")
    public String joinGame(@PathVariable String gameId, @RequestParam String playerName) {
        String playerId = UUID.randomUUID().toString();
        gameManagerService.addPlayer(gameId, playerId, playerName);
        return playerId;
    }

    // ✅ (Optional) Get current game state
    @GetMapping("/{gameId}")
    public GameStateDTO getGameState(@PathVariable String gameId) {
        return gameManagerService.getGameState(gameId);
    }
}
