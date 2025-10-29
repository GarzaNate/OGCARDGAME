import Card from './Card';

export default function PlayerHand({ cards = [], isSelf = false }) {
    return (
        <div
            className={`
                flex flex-wrap justify-center gap-2
                ${isSelf ? "mt-4 sm:mt-8" : "mb-4 sm:mb-8"}
            `}
        >
            {cards.map((card, i) => (
                <div key={i} className="w-[4rem] h-[6rem] sm:w-[6rem] sm:h-[9rem] md:w-[8rem] md:h-[12rem]">
                    <Card
                        suit={card.suit}
                        rank={card.rank}
                        faceDown={!isSelf}
                    />
                </div>
            ))}
        </div>
    );
}
