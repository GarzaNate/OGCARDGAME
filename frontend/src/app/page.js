"use client";
import Card from "@/components/Card";
import GameBoard from "@/components/GameBoard";

export default function Home() {

  const mockPlayers = [
    { id: 1, name: "Nate", hand: [{ suit: "hearts", rank: "ace" }, { suit: "spades", rank: "10" }] },
    { id: 2, name: "Bob", hand: [{ suit: "clubs", rank: "3" }, { suit: "diamonds", rank: "K" }] },
    { id: 3, name: "Charlie", hand: [{ suit: "hearts", rank: "7" }, { suit: "clubs", rank: "J" }] },
    { id: 4, name: "Martin", hand: [{ suit: "hearts", rank: "7" }, { suit: "clubs", rank: "J" }] },
];

const mockDiscard = [
    { suit: "hearts", rank: "5" }
];
  return (
    <GameBoard players={mockPlayers} selfPlayerId={1} drawPileCount={20} discardPile={mockDiscard} onDraw={() => console.log("Draw card clicked")} />
  );
}
