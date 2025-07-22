package com.ogcardgame.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket

public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new com.ogcardgame.controller.GameWebSocketHandler(), "/ws/game")
                .setAllowedOrigins("*"); // Allow all origins for simplicity, adjust as needed
    }
}