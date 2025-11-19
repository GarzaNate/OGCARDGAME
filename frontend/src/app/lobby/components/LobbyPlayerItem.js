export default function LobbyPlayerItem({ player }) {
    const displayName = player?.name || player?.username || "Unknown";
    const avatarChar = (displayName && displayName.length > 0) ? displayName[0].toUpperCase() : "U";

    return (
        <li className="flex items-center justify-between p-2 border-b last:border-b-0">
            <div className="flex items-center space-x-2">
                {/* Simple avatar */}
                <div className="w-8 h-8 bg-gray-300 rounded-full flex items-center justify-center">
                    {avatarChar}
                </div>
                {/* Player name */}
                <span>{displayName}</span>
                {/* Host badge */}
                {player.isHost && (
                    <span className="ml-2 px-1 text-xs bg-yellow-200 rounded">Host</span>
                )}
                {/* Optional stats */}
                {typeof player.gamesPlayed !== 'undefined' && (
                    <span className="ml-2 text-xs text-gray-500">🎮 {player.gamesPlayed} • 🏆 {player.gamesWon || 0}</span>
                )}
            </div>
            {/* Ready status */}
            <span className={`text-sm ${player.isReady ? "text-green-600" : "text-gray-400"}`}>
                {player.isReady ? "Ready" : "Not Ready"}
            </span>
        </li>
    );
}
