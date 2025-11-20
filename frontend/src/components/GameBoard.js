import Pile from "./Pile";
import PlayerArea from "./PlayerArea";

export default function GameBoard({
    players = [],
    selfPlayerId,
    drawPileCount = 0,
    discardPileCards = [],
    onDraw,
    onPlayCard,
    onPlayCards
}) {
    const selfPlayer = players.find(p => p.id === selfPlayerId);
    const others = players.filter(p => p.id !== selfPlayerId);

    const topPlayer = others[0] || null;
    const rightPlayer = others[1] || null;
    const leftPlayer = others[2] || null;

    return (
        <div className="w-screen h-screen bg-black grid grid-rows-[1fr_auto_1fr] grid-cols-[1fr_auto_1fr] gap-2">
            {/* Top Player */}
            <div className="flex justify-center items-center row-start-1 col-start-2">
                {topPlayer && <PlayerArea player={topPlayer} position="top" />}
            </div>

            {/* Left Player */}
            <div className="flex justify-center items-center row-start-2 col-start-1">
                {leftPlayer && <PlayerArea player={leftPlayer} position="left" />}
            </div>

            {/* Center Piles */}
            <div className="flex justify-center items-center row-start-2 col-start-2 gap-4">
                <Pile type="draw" remaining={drawPileCount} onClick={onDraw} />
                <Pile type="discard" cards={discardPileCards} />
            </div>

            {/* Right Player */}
            <div className="flex justify-center items-center row-start-2 col-start-3">
                {rightPlayer && <PlayerArea player={rightPlayer} position="right" />}
            </div>

            {/* Bottom Player (self) */}
            <div className="flex justify-center items-center row-start-3 col-start-2">
                {selfPlayer && <PlayerArea player={selfPlayer} isSelf={true} position="bottom" onPlayCard={onPlayCard} onPlayCards={onPlayCards} />}
            </div>
        </div>
    );
}
