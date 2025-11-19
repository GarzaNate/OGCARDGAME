"use client";
import React, { useEffect, useRef } from "react";
import useGameWebSocket from "@/hooks/useGameWebSocket";

export default function GamePage() {
    console.log("GamePage rendered");

    const playerIdRef = useRef("player-" + Math.floor(Math.random() * 100000));
    const playerId = playerIdRef.current;

    const gameId = "game-1234";

    const { connected, messages, sendMessage, sendAction } =
        useGameWebSocket(playerId, gameId);

    const hasJoinedRef = useRef(false);

    useEffect(() => {
        if (connected && !hasJoinedRef.current) {
            if (sendAction) sendAction("join", { playerId, gameId, name: "Nate" });
            else sendMessage({ action: "join", playerId, gameId, name: "Nate" });
            hasJoinedRef.current = true;
        }
    }, [connected]);

    return (
        <div className="flex flex-col items-center justify-center min-h-screen text-white">
            <h1 className="text-3xl font-bold mb-4">Card Game</h1>
            <p>Status: {connected ? "🟢 Connected" : "🔴 Disconnected"}</p>

            <div className="mt-4 bg-gray-800 p-4 rounded w-2/3">
                <h2 className="text-xl mb-2">Messages</h2>
                <ul className="max-h-64 overflow-auto text-sm">
                    {messages.map((msg, i) => (
                        <li key={i} className="border-b border-gray-700 py-1">
                            {JSON.stringify(msg)}
                        </li>
                    ))}
                </ul>
            </div>

            <button
                className="mt-4 px-4 py-2 bg-blue-500 rounded hover:bg-blue-600"
                onClick={() =>
                    sendAction
                        ? sendAction("join", { playerId, gameId: "some-existing-id", name: "Nate" })
                        : sendMessage({ action: "join", playerId, gameId: "some-existing-id", name: "Nate" })
                }
            >
                Join Game
            </button>
        </div>
    );
}
