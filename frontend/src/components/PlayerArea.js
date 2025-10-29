import PlayerHand from "./PlayerHand";

export default function PlayerArea({ player, isSelf = false, position = "bottom" }) {
    // Rotation for left/right players
    let rotation = 0;
    if (position === "left") rotation = 90;
    if (position === "right") rotation = -90;
    if (position === "top") rotation = 180; // horizontal, face-down

    // Padding to keep hands away from center
    const paddingMap = {
        top: "pb-2 sm:pb-4",
        bottom: "pt-2 sm:pt-4",
        left: "pr-2 sm:pr-4",
        right: "pl-2 sm:pl-4",
    };

    return (
        <div className={`flex flex-col items-center justify-center ${paddingMap[position]}`}>
            {/* Name */}
            <h2 className="text-white text-sm sm:text-base md:text-lg mb-1 truncate max-w-[6rem] sm:max-w-[8rem] md:max-w-[10rem] text-center">
                {player?.name || "Unknown"}
            </h2>

            {/* Rotated hand */}
            <div className={`transform origin-center`} style={{ transform: `rotate(${rotation}deg)` }}>
                <PlayerHand cards={player?.hand || []} isSelf={isSelf} />
            </div>
        </div>
    );
}
