package UNO;

import javax.print.attribute.standard.Finishings;
import java.util.*;

public class ArtificialIntelligence extends AIPlayer {

    private ArrayList<Integer> legalCards;

    /**
     * Constructor for player objects
     *
     * @param ID   An unique integer identifier for a player
     * @param game An game controller object representing the game player being in
     */
    public ArtificialIntelligence(int ID, Game game) {
        super(ID, game);
    }

    /**
     * This is how AI makes its action decision. This decision should always be legal.
     * @return 1 ("PlayOwned"), 2 ("Draw&Play"), 3 ("SKip")
     */
    public int makeActionDecision() {
        int currentSkipLevel = getGameController().getRuler().getNextPlayerSkiplevel();
        if (currentSkipLevel == 3) {
            return 3; // skip
        }
        legalCards = findLegalCard();
        if (currentSkipLevel != 0 && legalCards.size() == 0) {
            // there's pending stacked draw, and AI don't have legal card to play
            return 3; // skip
        }

        if (legalCards.size() == 0) {
            // there's no pending stacked draw, and AI don't have legal card to play
            return 2; // draw and play
        }
        return 1; // play owned cards
    }

    /**
     * Strategically choose a card to play. Notice AI will only make this decision when it has legal cards.
     * Also, AI will only play one card at a time.
     * @return The card (as ID) that AI decided to play
     */
    public int playCard() {
        int nextPlayerID = getGameController().getNextPlayerID();
        if (getGameController().getPlayerCardNumber(nextPlayerID) <= 2) {
            return findBestCard_CaseNextPlayerLessThan2();
        } else {
            return findBestCard_CommonCase();
        }
    }


    /**
     * AI will its preferred color when played a wild cards up calling this function.
     * @return Color picked by AI
     */
    public String pickColor() {
        ArrayList<Integer> colorRanks = findBestColors(getCards());
        int bestColorID = colorRanks.get(0);
        if (bestColorID == 0) { // AI owns A LOT OF wild cards. Extremely low probability.
            return colorID2color(colorRanks.get(1));
        }
        return colorID2color(bestColorID);
    }

    private int findBestCard_CommonCase() {
        ArrayList<Integer> priority = calcPriority_CommonCase(legalCards);
        int maxScore = Collections.max(priority);
        int maxIndex = priority.indexOf(maxScore);
        return legalCards.get(maxIndex);
    }

    /**
     * Priority Heuristics in common case:
     * (1) Worst color number > best color skip / draw2 / reserve.
     * (2) Play 0 first if possible.
     * (3) Always reserve wild/wildDraw4.
     * (4) reserve > skip > draw2
     * @param cards candidate cards.
     * @return the priority of each card.
     */
    private ArrayList<Integer> calcPriority_CommonCase(ArrayList<Integer> cards) {
        ArrayList<Integer> priority = new ArrayList<>();
        ArrayList<Integer> colorRanks = findBestColors(getCards()); // best color in all cards
        for (int i = 0; i < cards.size(); i++) {
            int cardID = cards.get(i);
            String cardDesc = parser.parseCardID(cardID);
            String color = parser.parseCardDescription(cardDesc)[0];
            int colorID = color2colorID(color);
            int rank = colorRanks.indexOf(colorID); // the ranking of color on the card
            switch (rank) { // weighing based on color of card
                case 0 -> priority.add(8); // best color
                case 1 -> priority.add(6);
                case 2 -> priority.add(4);
                case 3 -> priority.add(2);
                case 4 -> priority.add(1); // worst color
            }
            String content = parser.parseCardDescription(cardDesc)[2];
            switch (content) { // weighing based on card content
                case "skip" -> priority.set(i, priority.get(i) + 3);
                case "draw2" -> priority.set(i, priority.get(i) + 2);
                case "reverse" -> priority.set(i, priority.get(i) + 4);
                case "0" -> priority.set(i, priority.get(i) + 15);
                case "wild", "wildDraw4" -> priority.set(i, priority.get(i) + 1);
                default -> priority.set(i, priority.get(i) + 12); // 1-9
            }
        }
        return priority;
    }


    private int findBestCard_CaseNextPlayerLessThan2() {
        ArrayList<Integer> priority = calcPriority_CaseNextPlayerLessThan2(legalCards);
        int maxScore = Collections.max(priority);
        int maxIndex = priority.indexOf(maxScore);
        return legalCards.get(maxIndex);
    }

    /**
     * Priority Heuristics when next player has less than 2 cards left,:
     * (1) draw2 > reverse > skip > wildDraw4 > wild > 0 > 1-9
     * (2) worst color draw2/reverse/skip > best number
     *
     * @param cards candidate cards.
     * @return the priority of each card.
     */

    private ArrayList<Integer> calcPriority_CaseNextPlayerLessThan2(ArrayList<Integer> cards) {
        ArrayList<Integer> priority = new ArrayList<>();
        ArrayList<Integer> colorRanks = findBestColors(getCards()); // best color in all cards
        for (int i = 0; i < cards.size(); i++) {
            int cardID = cards.get(i);
            String cardDesc = parser.parseCardID(cardID);
            String color = parser.parseCardDescription(cardDesc)[0];
            int colorID = color2colorID(color);
            int rank = colorRanks.indexOf(colorID); // the ranking of color on the card
            switch (rank) { // weighing based on color of card
                case 0 -> priority.add(8); // best color
                case 1 -> priority.add(6);
                case 2 -> priority.add(4);
                case 3 -> priority.add(2);
                case 4 -> priority.add(1); // worst color
            }
            String content = parser.parseCardDescription(cardDesc)[2];
            switch (content) {  // weighing based on card content
                case "skip" -> priority.set(i, priority.get(i) + 3);
                case "draw2" -> priority.set(i, priority.get(i) + 15);
                case "reverse" -> priority.set(i, priority.get(i) + 12);
                case "0" -> priority.set(i, priority.get(i) + 2);
                case "wild", "wildDraw4" -> priority.set(i, priority.get(i) + 8);
                default -> priority.set(i, priority.get(i) + 1); // 1-9
            }
        }
        return priority;
    }


    /**
     * Find the best color that AI currently own most.
     *
     * @param cards candidate cards to be considered.
     *              0 NA, 1 red , 2 green, 3 blue, 4 yellow
     * @return ranking of owned color as ArrayList
     */

    private ArrayList<Integer> findBestColors(ArrayList<Integer> cards) {
        ArrayList<Integer> weights = new ArrayList<>();
        for (int i = 0; i < 5; i++) weights.add(0);  // 0 0 0 0 0

        // adding weight by iterating through hand cards
        for (Integer card : cards) {
            String cardDesc = parser.parseCardID(card);
            String color = parser.parseCardDescription(cardDesc)[0];
            String content = parser.parseCardDescription(cardDesc)[2];
            int colorID = color2colorID(color);
            switch (content) { // different cards should have different weight contribution
                case "skip" -> weights.set(colorID, weights.get(colorID) + 5);
                case "draw2" -> weights.set(colorID, weights.get(colorID) + 6);
                case "reverse" -> weights.set(colorID, weights.get(colorID) + 4);
                case "0" -> weights.set(colorID, weights.get(colorID) + 2);
                case "wild", "wildDraw4" -> weights.set(colorID, weights.get(colorID) + 1);//reserve wild cards
                default -> weights.set(colorID, weights.get(colorID) + 4);
            }
        }
        return argsort(weights.toArray(), false);
    }


    /**
     * Return sorted indices of given arraylist. (descending order)
     * e.g. given array [4,1,2,3] -> return [0,3,2,1]
     * Notice this version only support Integer!
     * Equivalent to argsort in python.
     */
    private ArrayList<Integer> argsort(final Object[] arrayToSort, boolean ascending) {
        Integer[] indices = new Integer[arrayToSort.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, new Comparator<Integer>() {
            @Override
            public int compare(final Integer i1, final Integer i2) {
                return (ascending ? 1 : -1) * Integer.compare((Integer) arrayToSort[i1], (Integer) arrayToSort[i2]);
            }
        });
        return new ArrayList<>(Arrays.asList(indices));
    }
}