"use client";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import useGameWebSocket from "@/hooks/useGameWebSocket";
import LobbyPlayerList from "./components/LobbyPlayerList";
import LobbyActions from "./components/LobbyActions";

export default function LobbyPage({ playerId, playerName, gameId }) {
    const { connected, messages, sendMessage, sendAction } = useGameWebSocket(playerId, gameId);
    const [players, setPlayers] = useState([]);
    const [isHost, setIsHost] = useState(false);

    const router = useRouter();

    // Parse backend messages
    useEffect(() => {
        messages.forEach((msg) => {
            const payload = msg.payload || msg;
            if (!payload.players) return;

            const normalized = payload.players.map((p) => ({ ...p, id: p.id || p.playerId || p.playerIdString }));
            setPlayers(normalized);

            const currentPlayer = normalized.find((p) => p.id === playerId);
            setIsHost(currentPlayer?.isHost || false);
        });
        // detect game start
        if (messages.length > 0) {
            const latest = messages[messages.length - 1];
            const payload = latest.payload || latest;
            const phase = payload.phase || (payload.payload && payload.payload.phase);
            const startedPhases = new Set(["DEAL", "PLAY_FROM_HAND", "PLAY_FROM_FACE_UP", "PLAY_FROM_FACE_DOWN"]);
            if (phase && startedPhases.has(phase) && gameId) {
                router.push(`/game/${gameId}`);
            }
        }
    }, [messages, playerId]);

    return (
        <div className="flex items-center justify-center min-h-screen px-4">
            <div className="panel w-full max-w-4xl">
                <div className="flex items-center justify-between mb-4">
                    <h1 className="text-2xl font-bold">Lobby {gameId || ''}</h1>
                    <div className="text-sm muted">{connected ? '🟢 Connected' : '🔴 Connecting...'}</div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div className="md:col-span-2">
                        <div className="panel">
                            <h2 className="text-lg font-semibold mb-2">Players</h2>
                            <LobbyPlayerList players={players} />
                        </div>
                    </div>

                    <aside className="md:col-span-1">
                        <div className="panel">
                            <h2 className="text-lg font-semibold mb-2">Actions</h2>
                            <LobbyActions
                                isHost={isHost}
                                players={players}
                                sendMessage={sendMessage}
                                sendAction={sendAction}
                                playerId={playerId}
                                playerName={playerName}
                                gameId={gameId}
                            />
                        </div>
                    </aside>
                </div>
            </div>
        </div>
    );
}
