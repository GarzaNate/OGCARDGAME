import { useEffect, useRef, useState } from "react";

export default function useGameWebSocket(playerId) {
    console.log("useGameWebSocket HOOK INIT for player:", playerId);

    const wsRef = useRef(null);
    const reconnectTimer = useRef(null);
    const [connected, setConnected] = useState(false);
    const [messages, setMessages] = useState([]);

    useEffect(() => {
        let isUnmounted = false;

        const connect = () => {
            if (wsRef.current || isUnmounted) return;

            console.log("Opening WS for player:", playerId);

            const socket = new WebSocket(
                `ws://localhost:8080/ws/game?playerId=${playerId}`
            );

            wsRef.current = socket;

            socket.onopen = () => {
                if (isUnmounted) return;
                console.log("WebSocket connected");
                setConnected(true);
            };

            socket.onmessage = (evt) => {
                if (isUnmounted) return;
                try {
                    const data = JSON.parse(evt.data);
                    setMessages((prev) => [...prev, data]);
                } catch (err) {
                    console.warn("Non-JSON WS message:", evt.data);
                }
            };

            socket.onclose = (ev) => {
                if (isUnmounted) return;
                console.warn("WebSocket closed", ev);

                setConnected(false);
                wsRef.current = null;

                // Reconnect only if NOT a normal close
                if (ev.code !== 1000) {
                    reconnectTimer.current = setTimeout(() => {
                        connect();
                    }, 1000);
                }
            };

            socket.onerror = (err) => {
                console.error("WebSocket error", err);
            };
        };

        connect();

        return () => {
            isUnmounted = true;
            if (reconnectTimer.current)
                clearTimeout(reconnectTimer.current);

            if (wsRef.current) {
                wsRef.current.close(1000, "Client disconnect");
                wsRef.current = null;
            }
        };
    }, [playerId]); // playerId is now stable

    const sendMessage = (msg) => {
        const socket = wsRef.current;
        if (!socket) {
            console.warn("WS not ready. Skipping message:", msg);
            return;
        }
        const attempt = () => {
            if (socket.readyState === WebSocket.OPEN) {
                socket.send(JSON.stringify(msg));
            } else if (socket.readyState === WebSocket.CONNECTING) {
                setTimeout(attempt, 50);
            } else {
                console.warn("WS not open. Dropping:", msg);
            }
        };
        attempt();
    };

    return { connected, messages, sendMessage };
}
