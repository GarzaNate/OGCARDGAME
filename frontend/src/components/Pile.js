"use client";
import Card from "./Card";

export default function Pile({ type = "draw", cards = [], remaining = 0, onClick }) {
    const isDrawPile = type === "draw";
    const topCard = cards.length > 0 ? cards[cards.length - 1] : null;

    return (
        <div
            className="relative w-24 h-36 sm:w-32 sm:h-48 md:w-40 md:h-60 bg-green-800 rounded-2xl border-2 border-green-600 flex items-center justify-center cursor-pointer hover:scale-105 transition-transform"
            onClick={onClick}
        >
            {isDrawPile ? (
                remaining > 0 ? (
                    <div className="text-white font-bold text-lg sm:text-xl">{remaining}</div>
                ) : (
                    <div className="text-gray-400 italic text-sm sm:text-base">Empty</div>
                )
            ) : topCard ? (
                <Card suit={topCard.suit} rank={topCard.rank} />
            ) : (
                <div className="text-gray-400 italic text-sm sm:text-base">Empty</div>
            )}
        </div>
    );
}
