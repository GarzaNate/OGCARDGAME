"use client";
import Image from "next/image";

export default function Card({ suit, rank, onClick, clickable = false, faceDown = false, selected = false }) {
    const imageSrc = faceDown ? '/cards/back.png' : `/cards/${rank}_of_${suit}.svg`;

    return (
        <div
            className={`relative w-20 h-[120px] hover:scale-105 transition-transform ${clickable ? 'cursor-pointer' : ''}`}
            onClick={onClick}
            role={clickable ? 'button' : undefined}
            aria-pressed={selected}
        >
            <Image
                src={imageSrc}
                alt={`${rank} of ${suit}`}
                fill
                className={`object-contain rounded-lg shadow-md ${selected ? 'ring-4 ring-yellow-400' : ''}`}
                priority
            />
        </div>
    );

}