import Card from './Card';
import { useState } from 'react';

export default function PlayerHand({ cards = [], isSelf = false, onPlayCard, onPlayCards }) {
    const [selectedIds, setSelectedIds] = useState(new Set());

    const toggleSelect = (cardId) => {
        setSelectedIds((prev) => {
            const next = new Set(prev);
            if (next.has(cardId)) next.delete(cardId);
            else next.add(cardId);
            return next;
        });
    };

    const clearSelection = () => setSelectedIds(new Set());

    const playSelected = () => {
        if (!onPlayCards) return;
        const ids = Array.from(selectedIds);
        if (ids.length === 0) return;
        onPlayCards(ids);
        clearSelection();
    };

    return (
        <div>
            <div className="flex flex-wrap justify-center items-center gap-2">
                {cards.map((card) => (
                    <div
                        key={card.id}
                        className="w-16 h-24 sm:w-36 sm:h-36 md:w-32 md:h-48"
                    >
                        <Card
                            suit={card.suit}
                            rank={card.rank}
                            faceDown={!isSelf}
                            clickable={isSelf}
                            selected={selectedIds.has(card.id)}
                            onClick={() => {
                                if (!isSelf) return;
                                // If the consumer provided a single-card onPlayCard, ignore selection
                                if (onPlayCard && !onPlayCards) return onPlayCard(card);
                                // Toggle selection for multi-play
                                toggleSelect(card.id);
                            }}
                        />
                    </div>
                ))}
            </div>

            {isSelf && (
                <div className="mt-2 flex gap-2 justify-center">
                    <button
                        className="px-3 py-1 bg-green-500 text-white rounded hover:bg-green-600"
                        onClick={playSelected}
                        disabled={selectedIds.size === 0}
                    >
                        Play Selected ({selectedIds.size})
                    </button>
                    <button
                        className="px-3 py-1 bg-gray-300 rounded hover:bg-gray-400"
                        onClick={clearSelection}
                        disabled={selectedIds.size === 0}
                    >
                        Clear
                    </button>
                </div>
            )}
        </div>
    );
}
