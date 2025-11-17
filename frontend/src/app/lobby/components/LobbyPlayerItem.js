export default function LobbyPlayerItem({ player }) {
    return (
        <li className="flex items-center justify-between p-2 border-b last:border-b-0">
            <div className="flex items-center space-x-2">
                {/* Simple avatar */}
                <div className="w-8 h-8 bg-gray-300 rounded-full flex items-center justify-center">
                    {player.name[0]}
                </div>
                {/* Player name */}
                <span>{player.name}</span>
                {/* Host badge */}
                {player.isHost && (
                    <span className="ml-2 px-1 text-xs bg-yellow-200 rounded">Host</span>
                )}
            </div>
            {/* Ready status */}
            <span className={`text-sm ${player.isReady ? "text-green-600" : "text-gray-400"}`}>
                {player.isReady ? "Ready" : "Not Ready"}
            </span>
        </li>
    );
}
