"use client";

import { createContext, useContext, useState, useEffect } from "react";
import { v4 as uuidv4 } from "uuid";

const UserContext = createContext();

export function UserProvider({ children }) {
    const [user, setUser] = useState({
        username: "",
        playerId: "",
        gamesPlayed: 0,
        gamesWon: 0,
    });

    // Load user from localStorage
    useEffect(() => {
        const saved = localStorage.getItem("user");
        if (saved) {
            setUser(JSON.parse(saved));
        }
    }, []);

    // Save user to localStorage whenever it changes
    useEffect(() => {
        localStorage.setItem("user", JSON.stringify(user));
    }, [user]);

    const setUsername = (username) => {
        setUser((prev) => ({
            ...prev,
            username,
            playerId: prev.playerId || uuidv4(), // generate playerId if not exists
        }));
    };

    const incrementGamesPlayed = () => {
        setUser((prev) => ({ ...prev, gamesPlayed: prev.gamesPlayed + 1 }));
    };

    const incrementGamesWon = () => {
        setUser((prev) => ({ ...prev, gamesWon: prev.gamesWon + 1 }));
    };

    return (
        <UserContext.Provider
            value={{ user, setUsername, incrementGamesPlayed, incrementGamesWon }}
        >
            {children}
        </UserContext.Provider>
    );
}

export function useUser() {
    return useContext(UserContext);
}
