"use client";
import Image from "next/image";

export default function Card({ suit, rank, onClick, clickable = false, faceDown = false }) {
    const imageSrc = faceDown ? '/cards/back.png' : `/cards/${rank}_of_${suit}.svg`;

    return (
        <div
            className="relative w-[80px] h-[120px] hover:scale-105 transition-transform cursor-pointer"
            onClick={onClick}
        >
            <Image
                src={imageSrc}
                alt={`${rank} of ${suit}`}
                fill
                className="object-contain rounded-lg shadow-md"
                priority
            />
        </div>
    );

}