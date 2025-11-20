export default function LobbyActions({ isHost, players, sendMessage, sendAction, playerId, playerName, gameId }) {
    const maxPlayers = 4;
    const canStart = isHost && players.length >= 2;

    const handleJoin = () => {
        if (sendAction) {
            sendAction("join", { gameId, playerId, name: playerName });
        } else {
            sendMessage({ action: "join", gameId, playerId, name: playerName });
        }
    };

    const handleStart = () => {
        if (sendAction) {
            sendAction("start", { gameId, playerId });
        } else {
            sendMessage({ action: "start", gameId, playerId });
        }
    };

    const handleReady = () => {
        // If your backend supports ready flag, you can send it here
        // For now we’ll just send join again as a placeholder
        if (sendAction) {
            sendAction("ready", { gameId, playerId, ready: true });
        } else {
            sendMessage({ action: "join", gameId, playerId, name: playerName, ready: true });
        }
    };

    return (
        <div className="w-full flex flex-col space-y-3">
            <button onClick={handleJoin} className="btn-primary">Join Lobby</button>

            {isHost && (
                <button onClick={handleStart} disabled={!canStart} className={`py-2 rounded ${canStart ? 'btn-primary' : 'bg-gray-600 text-gray-300 cursor-not-allowed'}`}>
                    Start Game
                </button>
            )}

            <button onClick={handleReady} className="btn-ghost">Ready</button>
        </div>
    );
}
