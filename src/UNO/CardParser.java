package UNO;

import java.util.*;

/**
 * In this project, there is no such "Card" class
 * Every card is represented with an unique ID from 1 - 108
 * ID 1 - 100 are colored cards and 101-108 are non-color wild/wildDraw4 cards
 * Construction rule: for each color group of cards (25 total),
 * 1-18 will be number cards (1-9), 25 will be 0, inclusively
 * 19-20 skip cards, 21-22 reverse, 23-24 draw4.
 * There will be (r,g,b,y) four color groups
 */

public class CardParser {
    private final int NUMBER_PER_COLOR = 25; // there are 25 cards per color
    private final int TOTAL_COLORS = 4; // R G B Y
    private final int COLORED_CARD_NUM = NUMBER_PER_COLOR * TOTAL_COLORS;
    public HashMap<Integer, String> colorDict;

    public CardParser() {
        colorDict = new HashMap<Integer, String>();

        // constructing the mapping from Integer to color
        colorDict.put(0, "none");
        colorDict.put(1, "red");
        colorDict.put(2, "green");
        colorDict.put(3, "blue");
        colorDict.put(4, "yellow");
    }


    /**
     * Given ID of the card, return its content as a string as "color type content"
     * color : {"red", "green", "blue", "yellow", "NA"}
     * type : {"sym", "num"}
     * content: {[1-9], "reverse", "skip", "draw2", "wild", "wildDraw4"} (integer are converted to string)
     * e.g. ID-1: "red num 1"; ID-108: "NA sym wildDraw4"
     * @param cardID a integer from 1 - 108, unique id for each card
     * @return "{color} {type} {content}" as string
     */
    public String parseCardID(int cardID) {
        if (cardID < 0) {
            return "NA NA NA"; // should not be reached
        }

        if (cardID > COLORED_CARD_NUM) {
            return parseWildCard(cardID);
        } else {
            return parseColoredCard(cardID);
        }
    }

    /**
     * helper function for parse for cards without colors
     */
    private String parseWildCard(int cardID) {
        if (cardID > COLORED_CARD_NUM && cardID <= COLORED_CARD_NUM + 4) {
            return "NA sym wild";

        }  else if (cardID > COLORED_CARD_NUM + 4 && cardID <= COLORED_CARD_NUM + 8) {
            return "NA sym wildDraw4";
        }
        return "NA NA NA"; // should not be reached
    }

    /**
     * helper function for parse for cards with colors
     */
    private String parseColoredCard(int cardID) {
        int colorID = (cardID - 1) / NUMBER_PER_COLOR + 1;
        String color = colorDict.get(colorID);
        Integer cardTypeID = cardID  % NUMBER_PER_COLOR;

        String type;
        String content = "";
        if (cardTypeID == 0) {
            // case 0  <- 25, 50, 75, 100
            type = "num";
            content = "0";

        } else if (cardTypeID <= 18) {
            // case 1 - 9
            type = "num";
            cardTypeID = cardTypeID <= 9 ? cardTypeID : (cardTypeID - 9);
            content = cardTypeID.toString();

        } else if (cardTypeID <= 20) {
            // case skip card
            type = "sym";
            content = "skip";

        } else if (cardTypeID <= 22) {
            // case reverse card
            type = "sym";
            content = "reverse";

        } else {
            // case draw 2
            type = "sym";
            content = "draw2";
        }

        return constructCardDescription(color, type, content);
    }


    private String constructCardDescription(String color, String type, String value) {
        return color + " " + type + " " + value;
    }

    /**
     * Given the description of one card, parse its color type and content
     * Descriptions should be in the format of return value from parseCardID
     * @return a string array (String[]) : [color, type, content]
     */
    public String[] parseCardDescription(String description) {
        String[] results = description.split(" ", 3);
        String color = results[0];
        String type = results[1];
        String content = results[2];

        String[] output = {color, type, content};
        return output;
    }

    /**
     * Judge whether a card is a wild/wildDraw4 card
     * @param cardID id of card
     */
    public boolean isNonColorCard(int cardID) {
        return cardID > COLORED_CARD_NUM;
    }
}

