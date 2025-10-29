import PlayerHand from "./PlayerHand";

export default function PlayerArea({ player, isSelf }) {
    return (
        <div className="flex flex-col items-center w-auto max-w-[150px] sm:max-w-[200px] md:max-w-[250px]">
            <h2 className="text-white text-sm sm:text-base md:text-lg mb-2 sm:mb-4 text-center truncate">
                {player.name}
            </h2>
            <PlayerHand cards={player.hand} isSelf={isSelf} />
        </div>
    );
}
