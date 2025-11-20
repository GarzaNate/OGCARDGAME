import LobbyPlayerItem from "./LobbyPlayerItem";

export default function LobbyPlayerList({ players }) {
    return (
        <div className="">
            <h2 className="text-lg font-semibold mb-2">Players in Lobby</h2>
            <div className="panel">
                <ul className="space-y-1">
                    {players.map((player) => (
                        <LobbyPlayerItem key={player.id} player={player} />
                    ))}
                </ul>
            </div>
        </div>
    );
}
