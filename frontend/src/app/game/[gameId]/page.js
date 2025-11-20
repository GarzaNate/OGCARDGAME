"use client";
import { useEffect, useRef, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { useUser } from "@/context/UserContext";
import useGameWebSocket from "@/hooks/useGameWebSocket";
import GameBoard from "@/components/GameBoard";
import ConfirmPlayModal from '@/components/ConfirmPlayModal';

export default function GameRoutePage() {
    const params = useParams();
    const router = useRouter();
    const gameId = params?.gameId || "game-unknown";

    const { user } = useUser();
    const playerName = user?.username || "Guest";

    // persistent playerId
    const playerIdRef = useRef(
        () => {
            if (typeof window === "undefined") return "player-" + Math.floor(Math.random() * 100000);
            const existing = localStorage.getItem("playerId");
            if (existing) return existing;
            const id = "player-" + Math.floor(Math.random() * 100000);
            localStorage.setItem("playerId", id);
            return id;
        }
    );
    // ensure ref.current is initialized
    if (typeof playerIdRef.current === "function") playerIdRef.current = playerIdRef.current();

    const playerId = playerIdRef.current;

    const { connected, messages, sendAction } = useGameWebSocket(playerId, gameId);

    const [players, setPlayers] = useState([]);
    const [drawCount, setDrawCount] = useState(0);
    const [pile, setPile] = useState([]);

    // Confirmation modal & optimistic UI state
    const [confirmOpen, setConfirmOpen] = useState(false);
    const [confirmCards, setConfirmCards] = useState([]);
    const [pendingPlays, setPendingPlays] = useState([]); // { ids:[], backupHand:[] }
    const pendingCardIdsRef = useRef(null);

    // auto-join when connected
    const hasJoinedRef = useRef(false);
    useEffect(() => {
        if (connected && !hasJoinedRef.current) {
            sendAction("join", { gameId, playerId, name: playerName });
            hasJoinedRef.current = true;
        }
    }, [connected, gameId, playerId, playerName, sendAction]);

    // parse incoming messages for a simple gameState and errors
    useEffect(() => {
        if (!messages || messages.length === 0) return;
        const last = messages[messages.length - 1];
        if (!last) return;

        // Handle error messages (restore optimistic plays if any)
        if (last.error) {
            // restore the most recent pending play if present
            setPendingPlays((pending) => {
                if (pending.length === 0) return pending;
                const latest = pending[pending.length - 1];
                setPlayers((prev) => {
                    return prev.map((p) => {
                        if (p.playerId === playerId || p.id === playerId) {
                            return { ...p, hand: latest.backupHand };
                        }
                        return p;
                    });
                });
                return pending.slice(0, -1);
            });
            return;
        }

        // If backend sends a `type` field, prefer structured messages
        if (last.type === "gameState" || last.players) {
            const state = last.payload || last;
            if (state.players) setPlayers(state.players);
            if (state.pile) setPile(state.pile);
            if (typeof state.drawCount !== "undefined") setDrawCount(state.drawCount);

            // server responded with authoritative state: clear pending plays
            setPendingPlays([]);
        }
    }, [messages]);

    if (!gameId) return <div>Missing gameId</div>;

    return (
        <div className="min-h-screen bg-gray-900 text-white">
            <div className="p-4 flex justify-between items-center">
                <div>
                    <h1 className="text-2xl font-bold">Game: {gameId}</h1>
                    <div className="text-sm text-gray-300">Player: {playerName} ({playerId})</div>
                </div>
                <div>
                    <button className="px-3 py-1 bg-red-500 rounded" onClick={() => router.push('/')}>Exit</button>
                </div>
            </div>

            <GameBoard
                players={players}
                selfPlayerId={playerId}
                drawPileCount={drawCount}
                discardPileCards={pile}
                onDraw={() => sendAction("draw", { gameId, playerId })}
                onPlayCard={(card) => {
                    // send a single-card play; server also supports cardIds for multi-play
                    if (!card || !card.id) return;
                    sendAction("play", { gameId, playerId, cardIds: [card.id] });
                }}
                onPlayCards={(cardIds) => {
                    if (!Array.isArray(cardIds) || cardIds.length === 0) return;

                    // Build card objects for confirmation display
                    const me = players.find((p) => p.playerId === playerId || p.id === playerId) || {};
                    const myHand = me.hand || [];
                    const cardsToPlay = cardIds.map((id) => myHand.find((c) => c.id === id)).filter(Boolean);

                    // open confirmation modal
                    setConfirmCards(cardsToPlay);
                    setConfirmOpen(true);
                    // stash selected ids in a ref until confirmed
                    pendingCardIdsRef.current = cardIds;
                }}
            />

            <ConfirmPlayModal
                open={confirmOpen}
                cards={confirmCards}
                onCancel={() => {
                    setConfirmOpen(false);
                    setConfirmCards([]);
                    pendingCardIdsRef.current = null;
                }}
                onConfirm={() => {
                    setConfirmOpen(false);
                    const cardIds = pendingCardIdsRef.current || [];
                    if (!cardIds.length) return;

                    // Optimistic update: remove cards from local player's hand and save backup
                    setPlayers((prev) => {
                        return prev.map((p) => {
                            if (p.playerId === playerId || p.id === playerId) {
                                const backupHand = p.hand ? [...p.hand] : [];
                                const newHand = (p.hand || []).filter((c) => !cardIds.includes(c.id));
                                // push pending play with backup
                                setPendingPlays((old) => [...old, { ids: cardIds, backupHand }]);
                                return { ...p, hand: newHand };
                            }
                            return p;
                        });
                    });

                    // send play action to server
                    sendAction("play", { gameId, playerId, cardIds });
                    // clear local temp
                    pendingCardIdsRef.current = null;
                    setConfirmCards([]);
                }}
            />
        </div>
    );
}
