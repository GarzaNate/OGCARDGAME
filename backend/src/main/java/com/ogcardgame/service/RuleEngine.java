package com.ogcardgame.service;

import com.ogcardgame.model.Card;
import com.ogcardgame.model.Pile;
import com.ogcardgame.model.Rank;

public class RuleEngine {
    public static boolean isValidPlay(Card card, Pile pile) {
        if (pile.isEmpty())
            return true;
        Card top = pile.peek();
        if (card.getRank() == Rank.TWO)
            return true;
        if (card.getRank() == Rank.TEN)
            return true;
        return card.getRank().getValue() >= top.getRank().getValue();
    }

    public static boolean isValidPlay(java.util.List<Card> cards, Pile pile) {
        if (cards == null || cards.isEmpty())
            return false;

        // All cards must be same rank
        Rank firstRank = cards.get(0).getRank();
        boolean allSameRank = cards.stream().allMatch(c -> c.getRank() == firstRank);
        if (!allSameRank)
            return false;

        // If any of them are special, always allowed
        if (firstRank == Rank.TWO || firstRank == Rank.TEN)
            return true;

        // Empty pile -> always valid
        if (pile.isEmpty())
            return true;

        // Otherwise, compare to top card
        Card top = pile.peek();
        return firstRank.getValue() >= top.getRank().getValue();
    }

    public static boolean isBomb(Pile pile) {
        if (pile.size() < 4)
            return false;
        Rank lastRank = pile.peek().getRank();
        return pile.getCards().stream()
                .skip(pile.size() - 4)
                .allMatch(c -> c.getRank() == lastRank);
    }
}

/*
 * Need a method to check if the card or cards played are valid
 * SELECT_FROM_FACEDOWN phase must allow for only one card to be played at a
 * time
 * Players should have the option to play multiple cards of the same rank at
 * once
 * Maybe find a way to only check the cards the player is interested in
 */