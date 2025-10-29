import Pile from "./Pile";
import PlayerArea from "./PlayerArea";

export default function GameBoard({ players, selfPlayerId, drawPileCount, discardPileCards, onDraw }) {
    const selfPlayer = players.find(p => p.id === selfPlayerId);
    const otherPlayers = players.filter(p => p.id !== selfPlayerId);

    return (
        <div className="w-full h-screen bg-black flex flex-col justify-between items-center p-4">
            {/* Top Area: Other players */}
            <div className="flex flex-wrap justify-around w-full gap-4">
                {otherPlayers.map((player) => (
                    <PlayerArea key={player.id} player={player} isSelf={false} />
                ))}
            </div>

            {/* Center Area: Draw & Discard Piles */}
            <div className="flex flex-wrap gap-8 justify-center items-center mt-16 mb-16">
                <Pile type="draw" remaining={drawPileCount} onClick={onDraw} />
                <Pile type="discard" cards={discardPileCards} />
            </div>

            {/* Bottom Area: Self player */}
            <div className="w-full flex justify-center">
                <PlayerArea player={selfPlayer} isSelf={true} />
            </div>
        </div>
    );
}
