"use client";
import React from 'react';

export default function ConfirmPlayModal({ open, cards = [], onConfirm, onCancel }) {
    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg p-4 max-w-md w-full">
                <h3 className="text-lg font-semibold mb-2">Confirm Play</h3>
                <p className="text-sm text-gray-700 mb-3">You're about to play {cards.length} card(s):</p>
                <div className="flex gap-2 mb-4 overflow-auto">
                    {cards.map((c) => (
                        <div key={c.id} className="w-16 h-24 bg-gray-100 rounded flex items-center justify-center text-sm">
                            <div className="text-center">
                                <div className="font-bold">{c.rank}</div>
                                <div className="text-xs">{c.suit}</div>
                            </div>
                        </div>
                    ))}
                </div>

                <div className="flex justify-end gap-2">
                    <button onClick={onCancel} className="px-3 py-1 rounded border">Cancel</button>
                    <button onClick={onConfirm} className="px-3 py-1 bg-blue-600 text-white rounded">Play</button>
                </div>
            </div>
        </div>
    );
}
