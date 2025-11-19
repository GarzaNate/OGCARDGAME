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
    <div className="flex flex-col items-center justify-center h-screen bg-gray-100 p-4">
      <h1 className="text-3xl font-bold mb-6">Welcome to Shithead</h1>

      {/* Username input */}
      {!user.username && (
        <div className="flex flex-col items-center mb-6 space-y-2">
          <input
            type="text"
            placeholder="Enter your username"
            value={nameInput}
            onChange={(e) => setNameInput(e.target.value)}
            className="px-4 py-2 rounded border w-64"
          />
          <button
            onClick={handleSetName}
            className="bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600"
          >
            Save Username
          </button>
        </div>
      )}

      {/* Show if username is set */}
      {user.username && (
        <div className="flex flex-col items-center space-y-4">
          <p className="text-lg">Hello, {user.username}!</p>
          <div className="flex space-x-4">
            <button
              onClick={handleCreateGame}
              className="bg-green-500 text-white py-2 px-4 rounded hover:bg-green-600"
            >
              Create Game
            </button>
            <input
              type="text"
              placeholder="Enter game ID"
              value={joinId}
              onChange={(e) => setJoinId(e.target.value)}
              className="px-4 py-2 rounded border w-32"
            />
            <button
              onClick={handleJoinGame}
              className="bg-yellow-400 text-white py-2 px-4 rounded hover:bg-yellow-500"
            >
              Join Game
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
