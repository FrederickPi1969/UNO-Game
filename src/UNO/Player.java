package UNO;

import java.util.*;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!! Part of  Model in MVC !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * Player object representing each player in the game.
 * There will be no interaction with the Viewer (GUI) in this class.
 */
public class Player {
    public static CardParser parser;
    private final ArrayList<Integer> cards;
    private Game gameController;
    private RuleController ruler;

    /**
     * Constructor for player objects
     *
     * @param ID An unique integer identifier for a player
     * @param game An game controller object representing the game player being in
     */
    public Player(int ID, Game game) {
        cards = new ArrayList<>();
        gameController = game;
        ruler = (game != null) ? game.getRuler() : null;  // null only for testing purpose
        parser = (game != null) ? RuleController.parser : null;
//        prompterCmd = useCmdAsUI ? new CmdUI(this) : null;
//        prompterGUI = useCmdAsUI ? null : new GUI(this);
    }

    /**
     * Player draw n cards from the draw pile.
     * If draw pile is not enough, will instead draw from discard Pile.
     * @param numToDraw number of cards to draw
     */
    public void drawCards(int numToDraw) {
        ArrayList<Integer> newCards = gameController.getCardManager().drawCards(numToDraw); // Initial draw
        cards.addAll(newCards);
    }


    /**
     * Iterate through cards to find all playable cards under the game state controlled by player.ruler
     * @return an array list of legal cardID
     */
    public ArrayList<Integer> findLegalCard() {
        ArrayList<Integer> legalCards = new ArrayList<>();
        for (int cardID : cards) {
            if (ruler.isValidPlay(this, cardID, false)) legalCards.add(cardID);
        }
        return legalCards;
    }

    /**
     * Getter for cards (private attribute for tracking all cards owned by a player)
     * @return the reference of cards (ArrayList<Integer>) of the player
     */
    public ArrayList<Integer> getCards() {
        return cards;
    }


    /**
     * For Test only. Add one card with Given card ID to the player's cards.
     * @param cardID cardID to be inserted
     */
    public void addOneCard(int cardID) {
        cards.add(cardID);
    }


    /**
     * For Assignment1.0 only
     * Implement the logic of player playing a card.
     * Upon successful playing, the game state will be updated
     * 1. First check whether a player should be skipped and do penalty draw
     * 2. Check whether a selected card is valid play
     * 3. Maintain discard pile and player cards
     * 4. Handle color declaration when player plays wild/wildDraw4 cards
     * IMPORTANT: choosing color is not implemented here as UI is not requirement for assignment1.0
     * however, CmdUI.getInputColor can do the job
     *
     * @Return whether this player successfully played the indicated card
     */
    public boolean optionPlayOwnedCard(int cardID, boolean updateIfPossible) {
        if (ruler.checkSkipAndDraw(this, false)) return false;
        assert(cards.contains(cardID));
        boolean isLegal = ruler.isValidPlay(this, cardID, updateIfPossible);
        if (isLegal && updateIfPossible) {
            /* maintain list-cards and discardPile */
            cards.remove(cards.indexOf(cardID));
            gameController.getCardManager().insertOneCardToDiscardPile(cardID);
            if (parser.isNonColorCard(cardID)) {
                /* instead of choose colors,
                we will forcefully set it to red for assignment 0 to make testing easier */
                ruler.setMatchableColor(parser.colorDict.get(1));
            }
            return true;
        }
        return isLegal;
    }


    /**
     * For Assignment1.0 only
     * Implement the logic when player choose to draw a card and play it
     * Upon successful playing, the game state will be updated
     * 1. First check whether a player should be skipped and do penalty draw
     * 2. Draw a card from draw pile
     * 3. If playable, play the card, insert to discard pile and update game state, else keep the card in hand
     * 4. Handle color declaration when player plays wild/wildDraw4 cards
     * IMPORTANT: choosing color is not implemented here as UI is not requirement for assignment1.0
     * however, CmdUI.getInputColor can do the job
     *
     * @Return whether this player successfully played the newly drawn card
     */
    public boolean optionDrawCardAndPlay() {
        if (ruler.checkSkipAndDraw(this, false)) return false;

        drawCards(1);
        int chosenCardIndex = getCards().size() - 1;
        int newCard = cards.get(chosenCardIndex);
        if (ruler.isValidPlay(this, newCard, true)) {
            cards.remove(chosenCardIndex);
            gameController.getCardManager().insertOneCardToDiscardPile(newCard);
            if (parser.isNonColorCard(newCard)) {
                /* instead of choose colors,
                 we will forcefully set it to red for assignment 0 to make testing easier */
                ruler.setMatchableColor(parser.colorDict.get(1));
            }
            return true;
        }
        return false; // played will be skipped
    }


    /**
     * Implement the option where player choose to skip his round.
     */
    public void optionSkip() {
        CardManager cardManager= gameController.getCardManager();
        int totalCardsLeft = cardManager.numCardLeft() + cardManager.numLeftDiscardPile();
        /* If the card left on the draw pile is not enough for stacked draw, draw as many as possible. */
        if (totalCardsLeft < ruler.getPenaltyDraw()) {
            ruler.setNextPlayerSkiplevel(0);
            drawCards(totalCardsLeft);
            ruler.resetPenaltyDraw();
        } else {
            ruler.drawStackedPenalty(this);  // else just take all stacked penalty draw.
        }
    }

    /**
     * Implement extra rule - addition by assignment-1.1.
     *
     * Players can pick two number cards with same color,
     * and use them as a single card whose color is the same,
     * and number is the summation of two chosen cards.
     *
     * Upon successful playing, the game state will be updated,
     * the two cards will be removed from hand & inserted to discard pile.
     * @Return whether this player successfully played the two indicated card
     */
    public boolean optionPlayTwoOwnedCard_Add(int cardID1, int cardID2, boolean updateIfPossible) {
        if (ruler.checkSkipAndDraw(this, false)) return false;
        assert(cards.contains(cardID1) && cards.contains(cardID2)); // checks player owns the card
        int equiCardID = addTwoCard(cardID1, cardID2); // the equivalent card ID after edition
        if (equiCardID == -1) return false;
        boolean isLegal = ruler.isValidPlay(this, equiCardID, updateIfPossible);
        if (isLegal && updateIfPossible) {
            // maintain list-cards and discardPile
            cards.remove(cards.indexOf(cardID1));
            cards.remove(cards.indexOf(cardID2));
            gameController.getCardManager().insertOneCardToDiscardPile(cardID1);
            gameController.getCardManager().insertOneCardToDiscardPile(cardID2);
            return true;
        }

        return isLegal;
    }

    /**
     * Implement extra rule - subtraction by assignment-1.1.
     *
     * Players can pick two number cards with same color,
     * and use them as a single card whose color is the same,
     * and number is the subtraction of two chosen cards (larger - smaller).
     *
     * Upon successful playing, the game state will be updated,
     * the two cards will be removed from hand & inserted to discard pile.
     * @Return whether this player successfully played the two indicated card
     */
    public boolean optionPlayTwoOwnedCard_Sub(int cardID1, int cardID2, boolean updateIfPossible) {
        if (ruler.checkSkipAndDraw(this, false)) return false;
        int equiCardID = subTwoCard(cardID1, cardID2); // the equivalent card ID after subtraction
        if (equiCardID == -1) return false;
        boolean isLegal = ruler.isValidPlay(this, equiCardID, updateIfPossible);
        if (isLegal && updateIfPossible) {
            // maintain list-cards and discardPile
            cards.remove(cards.indexOf(cardID1));
            cards.remove(cards.indexOf(cardID2));
            gameController.getCardManager().insertOneCardToDiscardPile(cardID1);
            gameController.getCardManager().insertOneCardToDiscardPile(cardID2);
            return true;
        }
        return isLegal;
    }

    /**
     * A player wins when he does not own any card
     *
     * @return whether the player wins
     */
    public boolean playerWin() {
        return cards.isEmpty();
    }

    /**
     * Helper function for adding two cards
     * Cards must be number cards and with same color to be able to added together
     * Return a equivalent cardID for the convenience of judging legality
     * @param cardID1 first card ID to be added
     * @param cardID2 second card ID to be added
     * @return (int) The cardID with equivalent effect of the addition, -1 if addition not legal or not found
     */
    private int addTwoCard(int cardID1, int cardID2) {
        int[] parseResult = parseNumber(cardID1, cardID2);
        if (parseResult == null) return -1;      // different color, or two cards are not addable
        int num1 = parseResult[0];
        int num2 = parseResult[1];
        int numResult = num1 + num2;
        String equiDesc = equiDescConstructor(cardID1, numResult);
        for (int i = 1; i <= 108; i++) {
            String iterDesc = parser.parseCardID(i);
            if (iterDesc.equals(equiDesc)) return i;
        }
        return -1;  // not able to find corresponding card - e.g. red 8 + red 5 = red 13 not a legal card
    }


    /**
     * Helper function for subtracting two cards
     * Cards must be number cards and with same color to be able to subtracted
     * Notice order of cards in this function does not matter
     *
     * @param cardID1 first card ID
     * @param cardID2 second card ID
     * @return (int) The cardID with equivalent effect of the subtraction, -1 if addition not legal or not found
     */
    private int subTwoCard(int cardID1, int cardID2) {
        int[] parseResult = parseNumber(cardID1, cardID2);
        if (parseResult == null) return -1;      // different color, or two cards are not addable
        int num1 = parseResult[0];
        int num2 = parseResult[1];
        int numResult = num1 > num2 ? (num1 - num2) : (num2 - num1); // make sure result is always positive
        String equiDesc = equiDescConstructor(cardID1, numResult);
        for (int i = 1; i <= 108; i++) {
            String iterDesc = parser.parseCardID(i);
            if (iterDesc.equals(equiDesc)) return i;
        }
        return -1;  // should not be reached
    }

    /**
     * Construct equivalent description for composition of two cards
     */
    private String equiDescConstructor(int cardID, int numResult) {
        String cardDescription1 = parser.parseCardID(cardID);
        String[] result1 = parser.parseCardDescription(cardDescription1);
        String color1 = result1[0];
        String equiDesc = color1 + " " + "num " + numResult;  // "color num numResult" as equivalent description
        return equiDesc;
    }

    /**
     * Helper function for subTwo and addTwo for parsing card numbers
     * @Return an array of length two representing the number on two cards
     * return null if either card are not number, or have different color
     */
    private int[] parseNumber(int cardID1, int cardID2) {
        String cardDescription1 = parser.parseCardID(cardID1);         // parse ID 1
        String[] result1 = parser.parseCardDescription(cardDescription1);
        String color1 = result1[0];
        String type1 = result1[1];
        String content1 = result1[2];

        String cardDescription2 = parser.parseCardID(cardID2);         // parse ID 2
        String[] result2 = parser.parseCardDescription(cardDescription2);
        String color2 = result2[0];
        String type2 = result2[1];
        String content2 = result2[2];

        // color different or either one not number card, return null
        if (!color1.equals(color2) || !type1.equals("num") || !type2.equals("num")) return null;
        int num1 = Integer.parseInt(content1);
        int num2 = Integer.parseInt(content2);
        return new int[]{num1, num2};
    }



    /**
     * Setter for player ID
     * @param id id to be set
     */
    public void setPlayerID(int id) {
    }


    /**
     * Get information of what game is the player currently in
     * @return Game, the game player being in
     */
    public Game getGameController() {
        return gameController;
    }

    /**
     * Set information of what game is the player currently in
     */
    public void setGameController(Game game) {
        gameController = game;
    }


    /**
     * @param r the ruler controller for the player
     * Set information of what game is the player currently in
     */
    public void setRuler(RuleController r) {
        ruler = r;
    }

    /**
     * @Return true (Player class objects are human)
     */
    public boolean isHuman() {
        return true;
    }



}