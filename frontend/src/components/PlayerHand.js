import Card from './Card';

export default function PlayerHand({ cards = [], isSelf = false }) {
    return (
        <div
            className={`flex gap-2 justify-center ${isSelf ? "mt-8" : "mb-8"
                }`}
        >
            {cards.map((card, i) => (
                <Card
                    key={i}
                    suit={card.suit}
                    rank={card.rank}
                    faceDown={!isSelf}
                />
            ))}
        </div>
    );
}