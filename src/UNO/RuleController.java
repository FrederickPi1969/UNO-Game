package UNO;

import java.util.ArrayList;
import java.util.Random;

/**
 * !!!!!!!!!!!!!!!!!!!!!! Part of Model IN MVC!!!!!!!!!!!!!!!!!!!!!!!
 * A class handling the game rule and storing the game state (backend data).
 * It judges whether each play of card is valid, and can update game state correspondingly.
 * Game state is represented by currentMatchableColor, currentMatchableNumber,
 * currentMatchableSymbol, cumulativePenaltyDraw(stacked draw), and nextPlayerSkipLevel.
 * See the getter & setter for details for these variables.
 *
 * There will be no interaction with the Viewer (GUI) in this class!
 */
public class RuleController {
    public static CardParser parser = new CardParser();

    /** IMPORTANT
     * As long as one of the following three is matchable, the play will be considered as legal
     */
    private String currentMatchableColor;
    private String currentMatchableNumber;
    private String currentMatchableSymbol = "none"; // Card played in the first round can only match by either color or number
    private String previousCard;
    private String previousAction = "None";
    private int nextPlayerSkipLevel = 0;
    private int cumulativePenaltyDraw = 0;
    private boolean isClockWise = true;   // if clockwise, next player will be the element in player list


    public RuleController() {
        // initialize first matched color and number
        boolean chosen = false;
        while (!chosen) {
            Random rand = new Random();
            int cardID = rand.nextInt(100) + 1; // generate a number from 1 - 100 (ID of colored cards)
            String desc = parser.parseCardID(cardID);
            String[] result = parser.parseCardDescription(desc);
            String color = result[0];
            String type = result[1];
            String content = result[2];

            if (type.equals("num")) {
                previousCard = desc;
                currentMatchableColor = color;
                currentMatchableNumber = content;
                chosen = true;
            }
        }
    }

    /**
     * Judge whether a pending player should be skipped
     * If true, then the player takes all stacked penalty cards
     * @param player the pending player
     * @return whether the player was skipped and forced to draw
     */
    public boolean checkSkipAndDraw(Player player, boolean drawIfSkipped) {
        if (nextPlayerSkipLevel == 3) {
            assert cumulativePenaltyDraw == 0;
            nextPlayerSkipLevel = 0; // the next player should no longer be skipped
            return true;

        } else if (nextPlayerSkipLevel != 0) {
            // player will be skipped and forced to draw cards (if drawIfSkipped as true)
            // unless they have draw2/wildDraw4 cards
            assert cumulativePenaltyDraw != 0;
            boolean toSkip = player.findLegalCard().isEmpty();
            if (toSkip && drawIfSkipped) {
                drawStackedPenalty(player);
            }
            return toSkip;

        } else {
            return false;
        }
    }

    /**
     * For a player to clear all stacked penalty draw.
     * @param player The victim
     */
    public void drawStackedPenalty(Player player) {
        nextPlayerSkipLevel = 0;
        player.drawCards(cumulativePenaltyDraw);
        resetPenaltyDraw();
    }


    /**
     * Given a card played by the user, judge whether this play is valid
     * Then update the game state if param updateIfValid is set as true
     * @param player player attempting to play the card
     * @param cardID the ID (1-108) of card to be validated
     * @param updateIfValid  whether to update game state if the play is valid
     * @return whether the play is valid
     */
    public boolean isValidPlay(Player player, int cardID, boolean updateIfValid) {
            assert(player != null);
            String cardDescription = parser.parseCardID(cardID);
            String[] result = parser.parseCardDescription(cardDescription);
        String color = result[0];
        String type = result[1];
        String content = result[2];

        boolean valid;

        if (nextPlayerSkipLevel == 3) {
            valid = false;

        } else if (nextPlayerSkipLevel == 2) {
            if (content.equals("wildDraw4")) valid = checkDraw4IsLegal(player);
            else valid = false;

        } else if (nextPlayerSkipLevel == 1) {
            if (content.equals("wildDraw4")) valid = checkDraw4IsLegal(player);
            else valid = content.equals("draw2"); // draw 2 is always allowed in this case

        } else {
            valid = isValidSkip0(player, color, content);
        }
        if (valid && updateIfValid) updateRule(color, type, content); // do update

        return valid;
    }



    /** Helper function for isValid handling the case where skiplevel==0
     */
    private boolean isValidSkip0(Player player, String color, String content) {
        // if not skipped, first consider wild card
        if (content.equals("wild")) {
            return true;  // can be used unconditionally
        }

        // then other colored cards
        if (checkAttrMatch(currentMatchableColor, color)) return true;
        if (checkAttrMatch(currentMatchableNumber, content) || checkAttrMatch(currentMatchableSymbol, content))
            return true;

        // check wildDraw4 at last
        if (content.equals("wildDraw4")) {
            return checkDraw4IsLegal(player);
        }
        return false;
    }





    /**
     * Helper function to update the game state.
     * @param color the color of card being played
     * @param type the type of card being played
     * @param content the content of card being played
     */
    private void updateRule(String color, String type, String content) {
        setMatchableColor(color); // wildcard "NA" - this will be updated later by player declaring the color
        setPreviousCard(color + " " + type + " " + content); // update previous card played
        if (type.equals("sym")) {
            switch (content) {
                case "skip" -> {
                    setNextPlayerSkiplevel(3);
                    currentMatchableSymbol = content;
                    currentMatchableNumber = "none";
                }
                case "draw2" -> {
                    setNextPlayerSkiplevel(1);
                    increasePenaltyDraw(2);
                    currentMatchableSymbol = content;
                    currentMatchableNumber = "none";
                }
                case "wildDraw4" -> {
                    setNextPlayerSkiplevel(2);
                    increasePenaltyDraw(4);
                    currentMatchableSymbol = "none";
                    currentMatchableNumber = "none";
                }
                case "reverse" -> {
                    changeGameOrder();
                    setNextPlayerSkiplevel(0);
                    currentMatchableSymbol = content;
                    currentMatchableNumber = "none";
                }
                case "wild" -> {
                    currentMatchableSymbol = "none";
                    currentMatchableNumber = "none";
                }
            }

        } else if (type.equals("num")) {
            // next round match by either COL or NUM
            setNextPlayerSkiplevel(0);
            currentMatchableNumber = content;
            currentMatchableSymbol = "none";
        }
    }

    private boolean checkAttrMatch(String legalString, String stringToCheck) {
        return legalString.equals("all") || stringToCheck.equals(legalString);
    }

    /**
     * Check whether a player have currently matchable **color** when attempting to play wild draw 4
     */
    private boolean checkDraw4IsLegal(Player player) {

        ArrayList<Integer> cards = player.getCards();
        for (int i = 0; i < cards.size(); i++) {
            int cardID = cards.get(i);
            String cardDescription = parser.parseCardID(cardID);
            String color = parser.parseCardDescription(cardDescription)[0]; // color of wild cards are NA
            if (color.equals(currentMatchableColor)) {
                return false;
            }
        }
        return true;
    }

    /**
     *  Getter for matchable number.
     *  currentMatchableNumber: As long as player played a card whose number matches this attribute,
     *  the play will be judged as legal.
     */

    public String getMatchableNumber() {
        return currentMatchableNumber;
    }

    public void setMatchableNumber(String number) {
        currentMatchableNumber = number;
    }

    /**
     * Getter for matchable symbol.
     * currentMatchableSymbol: As long as player played a card whose symbol matches this attribute,
     * the play will be judged as legal.
     * @return matchable symbol for current rond
     */

    public String getMatchableSymbol() {
        return currentMatchableSymbol;
    }

    /**
     * Setter for matchable symbol.
     * @param symbol the symbol to be set
     */
    public void setMatchableSymbol(String symbol) {
        currentMatchableSymbol = symbol;
    }


    /**
     * Getter for current matchable color.
     * currentMatchableColor: As long as player played a card whose color matches this attribute,
     * the play will be judged as legal.
     * @return current matchable color
     */

    public String getMatchableColor() {
        return currentMatchableColor;
    }

    /**
     * Setter for matchable color.
     * @param color color to be set
     */
    public void setMatchableColor(String color) {
        currentMatchableColor = color;
    }

    /**
     * Getter for nextPlayerIsSkipped。
     * There are four levels of "nextPlayerIsSkipped":
     * level 0: next player won't be skipped;
     * level 1: next player can play a either a draw2 card or a wildDraw4 card to avoid being skipped;
     * level 2: next player can only play a wildDraw4 card of any color to avoid being skipped;
     * level 3: next player will be skipped anyway.
     * @return current skip level
     */

    public int getNextPlayerSkiplevel() { return nextPlayerSkipLevel; }

    /**
     * Setter for nextPlayerSkipLevel.
     * @param level the level to be set (check getter for details)
     */
    public void setNextPlayerSkiplevel(int level) { nextPlayerSkipLevel = level; }

    /**
     * Get the total number of stacked card to be drawn (cumulativePenaltyDraw).
     * @return cumulativePenaltyDraw
     */

    public int getPenaltyDraw() { return cumulativePenaltyDraw; }

    /**
     * Reset next player penalty draw to 0.
     * Applied when a played takes penalty draw in previous round
     */
    public void resetPenaltyDraw() { cumulativePenaltyDraw = 0; }

    /**
     * Increase number of stacked penalty draw by num.
     * @param num number to increase
     */
    public void increasePenaltyDraw(int num) { cumulativePenaltyDraw += num; }

    /**
     * Get the current game order.
     * @return whether current game order is clockwise
     */
    public boolean getIsClockwise() { return isClockWise; }

    /**
     * Flip the order of game.
     * clockwise goes to counter-clockwise, vice versa
     */
    public void changeGameOrder() { isClockWise = !isClockWise; }


    /**
     * Setter for previous card played.
     * @param cardDescription A string of description of previous card
     */
    public void setPreviousCard(String cardDescription) {
        previousCard = cardDescription;
    }

    /**
     * Getter for previous card played.
     * @return card Description of previous card
     */
    public String getPreviousCard() {
        return previousCard;
    }


    public void setPreviousAction(String action) {
        previousAction = action;
    }
    public String getPreviousAction() {
        return previousAction;
    }


    /**
     * Update game state information (previous card & matchable color ) when wild cards are played.
     * @param color A color string of declared color.
     */
    public void updateGameStateBasedOnPickedColor(String color) {
        CardParser parser = new CardParser();
        setMatchableColor(color);
        String prevCard = getPreviousCard();
        String type = parser.parseCardDescription(prevCard)[1];
        String content = parser.parseCardDescription(prevCard)[2];
        setPreviousCard(color + " "+ type + " " + content);   // e.g. "newColor sym wild"
    }


//    /**
//     * Print current game state to terminal
//     * @param gameController the game object representing current game.
//     */
//    public void reportCurrentState(Game gameController) {
//        System.out.println("============================= Game State Report ========================================");
//        System.out.println("Current matchable color : " + getMatchableColor());
//        System.out.println("Current matchable number : " + getMatchableNumber());
//        System.out.println("Current matchable symbol : " + getMatchableSymbol());
//        System.out.println("Game order : " + (getIsClockwise() ? "clockwise" : "counterclockwise"));
//        System.out.println("Player skip level is " + descSkipLevel());
//        System.out.println("Player will be forced to draw " + cumulativePenaltyDraw + " cards");
//        System.out.println("There are " + gameController.getCardManager().numCardLeft() + " cards in the card pile.");
//        System.out.println("There are " + gameController.getCardManager().numLeftDiscardPile() + " cards in the discard pile.");
//    }
//



}







