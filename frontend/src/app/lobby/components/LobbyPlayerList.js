import LobbyPlayerItem from "./LobbyPlayerItem";

export default function LobbyPlayerList({ players }) {
    return (
        <div className="w-full max-w-md bg-white shadow-md rounded-lg p-4 mb-6">
            <h2 className="text-lg font-semibold mb-2">Players in Lobby:</h2>
            <ul>
                {players.map((player) => (
                    <LobbyPlayerItem key={player.id} player={player} />
                ))}
            </ul>
        </div>
    );
}
