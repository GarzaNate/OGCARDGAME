"use client";
import { useEffect, useRef, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { useUser } from "@/context/UserContext";
import useGameWebSocket from "@/hooks/useGameWebSocket";
import GameBoard from "@/components/GameBoard";

export default function GameRoutePage() {
    const params = useParams();
    const router = useRouter();
    const gameId = params?.gameId || "game-unknown";

    const { user } = useUser();
    const playerName = user?.username || "Guest";

    // persistent playerId
    const playerIdRef = useRef(
        () => {
            if (typeof window === "undefined") return "player-" + Math.floor(Math.random() * 100000);
            const existing = localStorage.getItem("playerId");
            if (existing) return existing;
            const id = "player-" + Math.floor(Math.random() * 100000);
            localStorage.setItem("playerId", id);
            return id;
        }
    );
    // ensure ref.current is initialized
    if (typeof playerIdRef.current === "function") playerIdRef.current = playerIdRef.current();

    const playerId = playerIdRef.current;

    const { connected, messages, sendAction } = useGameWebSocket(playerId, gameId);

    const [players, setPlayers] = useState([]);
    const [drawCount, setDrawCount] = useState(0);
    const [pile, setPile] = useState([]);

    // auto-join when connected
    const hasJoinedRef = useRef(false);
    useEffect(() => {
        if (connected && !hasJoinedRef.current) {
            sendAction("join", { gameId, playerId, name: playerName });
            hasJoinedRef.current = true;
        }
    }, [connected, gameId, playerId, playerName, sendAction]);

    // parse incoming messages for a simple gameState
    useEffect(() => {
        if (!messages || messages.length === 0) return;
        const last = [...messages].reverse().find(Boolean);
        if (!last) return;

        // If backend sends a `type` field, prefer structured messages
        if (last.type === "gameState" || last.players) {
            const state = last.payload || last;
            if (state.players) setPlayers(state.players);
            if (state.pile) setPile(state.pile);
            if (typeof state.drawCount !== "undefined") setDrawCount(state.drawCount);
        }
    }, [messages]);

    if (!gameId) return <div>Missing gameId</div>;

    return (
        <div className="min-h-screen bg-gray-900 text-white">
            <div className="p-4 flex justify-between items-center">
                <div>
                    <h1 className="text-2xl font-bold">Game: {gameId}</h1>
                    <div className="text-sm text-gray-300">Player: {playerName} ({playerId})</div>
                </div>
                <div>
                    <button className="px-3 py-1 bg-red-500 rounded" onClick={() => router.push('/')}>Exit</button>
                </div>
            </div>

            <GameBoard
                players={players}
                selfPlayerId={playerId}
                drawPileCount={drawCount}
                discardPileCards={pile}
                onDraw={() => sendAction("draw", { gameId, playerId })}
            />
        </div>
    );
}
