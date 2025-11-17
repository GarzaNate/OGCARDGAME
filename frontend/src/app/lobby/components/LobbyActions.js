export default function LobbyActions({ isHost, players, sendMessage, playerId, playerName, gameId }) {
    const maxPlayers = 4;
    const canStart = isHost && players.length >= 2;

    const handleJoin = () => {
        sendMessage({
            action: "join",
            gameId,
            playerId,
            name: playerName,
        });
    };

    const handleStart = () => {
        sendMessage({
            action: "start",
            gameId,
            playerId,
        });
    };

    const handleReady = () => {
        // If your backend supports ready flag, you can send it here
        // For now we’ll just send join again as a placeholder
        sendMessage({
            action: "join",
            gameId,
            playerId,
            name: playerName,
            ready: true, // optional
        });
    };

    return (
        <div className="w-full max-w-md flex flex-col space-y-2">
            <button
                onClick={handleJoin}
                className="bg-green-500 text-white py-2 rounded hover:bg-green-600"
            >
                Join Lobby
            </button>

            {isHost && (
                <button
                    onClick={handleStart}
                    disabled={!canStart}
                    className={`py-2 rounded ${canStart
                            ? "bg-purple-500 text-white hover:bg-purple-600"
                            : "bg-gray-300 text-gray-500 cursor-not-allowed"
                        }`}
                >
                    Start Game
                </button>
            )}

            <button
                onClick={handleReady}
                className="bg-yellow-400 text-white py-2 rounded hover:bg-yellow-500"
            >
                Ready
            </button>
        </div>
    );
}
