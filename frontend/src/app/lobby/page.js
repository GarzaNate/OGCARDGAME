"use client";
import { useState, useEffect, use } from "react";
import useGameWebSocket from "../../hooks/useGameWebSocket";
import LobbyPlayerList from "./components/LobbyPlayerList";
import LobbyActions from "./components/LobbyActions";

export default function LobbyPage({ playerId, playerName, gameId }) {
    const { connected, messages, sendMessage } = useGameWebSocket(playerId);
    const [players, setPlayers] = useState([]);
    const [isHost, setIsHost] = useState(false);

    // Parse backend messages
    useEffect(() => {
        messages.forEach((msg) => {
            // Ignore non-game-state messages
            if (!msg.players) return;

            setPlayers(msg.players);

            const currentPlayer = msg.players.find((p) => p.id === playerId);
            setIsHost(currentPlayer?.isHost || false);
        });
    }, [messages, playerId]);

    return (
        <div className="flex flex-col items-center justify-center h-screen bg-gray-100 p-4">
            <h1 className="text-2xl font-bold mb-6">Game Lobby</h1>

            {/* Player List */}
            <LobbyPlayerList players={players} />

            {/* Lobby Actions */}
            <LobbyActions
                isHost={isHost}
                players={players}
                sendMessage={sendMessage}
                playerId={playerId}
                playerName={playerName}
                gameId={gameId}
            />

            <p className="mt-4 text-sm text-gray-500">
                {connected ? "Connected" : "Connecting..."}
            </p>
        </div>
    );
}
