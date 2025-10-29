import Card from './Card';

export default function PlayerHand({ cards = [], isSelf = false }) {
    return (
        <div className="flex flex-wrap justify-center items-center gap-2">
            {cards.map((card, i) => (
                <div
                    key={i}
                    className="w-[4rem] h-[6rem] sm:w-[6rem] sm:h-[9rem] md:w-[8rem] md:h-[12rem]"
                >
                    <Card
                        suit={cards[i].suit}
                        rank={cards[i].rank}
                        faceDown={!isSelf}
                    />
                </div>
            ))}
        </div>
    );
}
