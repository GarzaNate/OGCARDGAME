package com.ogcardgame.service;

import com.ogcardgame.model.Card;
import com.ogcardgame.model.Pile;
import com.ogcardgame.model.Rank;

public class RuleEngine {
    public static boolean isValidPlay(Card card, Pile pile) {
        if (pile.isEmpty()) return true;
        Card top = pile.peek();
        if (card.getRank() == Rank.TWO) return true;
        if (card.getRank() == Rank.TEN) return true;
        return card.getRank().getValue() >= top.getRank().getValue();
    }

    public static boolean isBomb(Pile pile) {
        if (pile.size() < 4) return false;
        Rank lastRank = pile.peek().getRank();
        return pile.getCards().stream()
            .skip(pile.size() - 4)
            .allMatch(c -> c.getRank() == lastRank);
    }
}
