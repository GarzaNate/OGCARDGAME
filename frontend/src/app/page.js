"use client";

import { useState } from "react";
import { useUser } from "../context/UserContext";
import { useRouter } from "next/navigation";

export default function HomePage() {
  const { user, setUsername } = useUser();
  const [nameInput, setNameInput] = useState(user.username || "");
  const [joinId, setJoinId] = useState("");
  const router = useRouter();

  const handleSetName = () => {
    if (!nameInput.trim()) return;
    setUsername(nameInput.trim());
  };

  const handleCreateGame = () => {
    if (!user.username) {
      alert("Please set a username first!");
      return;
    }
    // Generate a short random gameId
    const gameId = Math.random().toString(36).substring(2, 8).toUpperCase();
    router.push(`/lobby?gameId=${gameId}`);
  };

  const handleJoinGame = () => {
    if (!user.username) {
      alert("Please set a username first!");
      return;
    }
    if (!joinId.trim()) {
      alert("Please enter a game ID to join.");
      return;
    }
    router.push(`/lobby?gameId=${joinId.trim().toUpperCase()}`);
  };

  return (
    <div className="flex items-center justify-center min-h-screen px-4">
      <div className="panel max-w-xl w-full">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h1 className="text-2xl font-bold">Shithead</h1>
            <p className="text-sm muted">A small online card game to play with friends</p>
          </div>
        </div>

        {/* Username input */}
        {!user.username && (
          <div className="space-y-3">
            <label className="text-sm muted">Choose a display name</label>
            <input
              type="text"
              placeholder="Enter your username"
              value={nameInput}
              onChange={(e) => setNameInput(e.target.value)}
              className="w-full p-3 rounded bg-white/5 border border-white/5"
            />

            <div className="flex justify-end">
              <button onClick={handleSetName} className="btn-primary">Save Username</button>
            </div>
          </div>
        )}

        {/* Show if username is set */}
        {user.username && (
          <div className="mt-3 space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <div className="text-lg">Hello, <strong>{user.username}</strong></div>
                <div className="text-xs muted">ID: {user.playerId}</div>
              </div>
              <div className="flex gap-2">
                <button onClick={handleCreateGame} className="btn-primary">Create Game</button>
              </div>
            </div>

            <div className="flex gap-2 items-center">
              <input
                type="text"
                placeholder="Enter game ID"
                value={joinId}
                onChange={(e) => setJoinId(e.target.value)}
                className="flex-1 p-2 rounded bg-white/5 border border-white/5"
              />
              <button onClick={handleJoinGame} className="btn-ghost">Join Game</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
