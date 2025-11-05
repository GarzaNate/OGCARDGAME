import { useEffect, useRef, useState } from 'react';

export const useGameWebSocket = (playerId, gameId = null) => {
    const [messages, setMessages] = useState([]);
    const [connected, setConnected] = useState(false);
    const ws = useRef(null);

    useEffect(() => {
        const socketUrl = gameId
            ? `ws://localhost:8080/ws/game?playerId=${playerId}&gameId=${gameId}`
            : `ws://localhost:8080/ws/game?playerId=${playerId}`;
        ws.current = new WebSocket(socketUrl);

        ws.current.onopen = () => {
            setConnected(true);
            console.log('WebSocket connected');
        };

        ws.current.onmessage = (event) => {
            const message = JSON.parse(event.data);
            setMessages((prevMessages) => [...prevMessages, message]);
        };

        ws.current.onclose = () => {
            setConnected(false);
            console.log('WebSocket disconnected');
        };

        ws.current.onerror = (error) => {
            console.error('WebSocket error:', error);
            setConnected(false);
        };

        return () => {
            ws.current.close();
        };
    }, [playerId, gameId]);

    const sendMessage = (message) => {
        if (ws.current && connected) {
            ws.current.send(JSON.stringify(message));
        } else {
            console.error('WebSocket is not connected');
        }
    };

    return { messages, sendMessage, connected };
}