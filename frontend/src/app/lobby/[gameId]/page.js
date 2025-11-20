"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import useGameWebSocket from "@/hooks/useGameWebSocket";
import { useUser } from "@/context/UserContext";
import LobbyPlayerList from "../components/LobbyPlayerList";
import LobbyActions from "../components/LobbyActions";

export default function LobbyPage() {
  const { gameId } = useParams();
  const router = useRouter();

  const { user, setUsername } = useUser();
  const [nameInput, setNameInput] = useState(user?.username || "");

  const playerId = user?.playerId;

  const { sendMessage, sendAction, connected, messages } = useGameWebSocket(playerId, gameId);

  const [players, setPlayers] = useState([]);

  // If user hasn't set a username yet, show input to set it
  const handleSaveName = () => {
    if (!nameInput.trim()) return;
    setUsername(nameInput.trim());
  };

  // Auto-join when connected and we have username/playerId
  useEffect(() => {
    if (!connected) return;
    if (!gameId || !playerId || !user?.username) return;

    // Prefer using sendAction helper
    if (sendAction) {
      sendAction("join", { gameId, playerId, name: user.username });
    } else {
      sendMessage({ action: "join", gameId, playerId, name: user.username });
    }
  }, [connected, gameId, playerId, user?.username, sendMessage, sendAction]);

  // Parse messages into players state (normalize playerId -> id)
  useEffect(() => {
    if (!messages || messages.length === 0) return;
    const latest = messages[messages.length - 1];
    const payload = latest.payload || latest;
    if (!payload) return;

    if (payload.players && Array.isArray(payload.players)) {
      const normalized = payload.players.map((p) => ({ ...p, id: p.id || p.playerId || p.playerIdString }));
      setPlayers(normalized);
    }
    // If backend sent a game state phase that indicates the game started, navigate to the game page
    const phase = payload.phase || (payload.payload && payload.payload.phase);
    const startedPhases = new Set(["DEAL", "PLAY_FROM_HAND", "PLAY_FROM_FACE_UP", "PLAY_FROM_FACE_DOWN"]);
    if (phase && startedPhases.has(phase)) {
      router.push(`/game/${gameId}`);
    }
  }, [messages]);

  return (
    <div className="flex items-center justify-center min-h-screen px-4">
      <div className="panel w-full max-w-4xl">
        <div className="flex items-center justify-between mb-4">
          <h1 className="text-2xl font-bold">Lobby {gameId}</h1>
          <div className="flex gap-2">
            <button className="btn-ghost" onClick={() => router.push('/')}>Back</button>
            <div className="text-sm muted">{connected ? '🟢 Connected' : '🔴 Connecting...'}</div>
          </div>
        </div>

        {!user?.username ? (
          <div className="panel max-w-md">
            <h2 className="text-lg font-semibold mb-2">Set your display name</h2>
            <input
              value={nameInput}
              onChange={(e) => setNameInput(e.target.value)}
              className="w-full p-2 rounded bg-white/5 border border-white/5 mb-2"
              placeholder="Your name"
            />
            <div className="flex gap-2">
              <button onClick={handleSaveName} className="btn-primary">Save</button>
              <button onClick={() => router.push('/')} className="btn-ghost">Cancel</button>
            </div>
          </div>
        ) : (
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
                  isHost={players.find((p) => p.id === playerId)?.isHost}
                  players={players}
                  sendMessage={sendMessage}
                  sendAction={sendAction}
                  playerId={playerId}
                  playerName={user.username}
                  gameId={gameId}
                />
              </div>
            </aside>
          </div>
        )}
      </div>
    </div>
  );
}