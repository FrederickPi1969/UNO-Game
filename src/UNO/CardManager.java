package UNO;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Card Manager class aiming to maintain draw pile and discard pile.
 */
public class CardManager {
    private ArrayList<Integer> cardPile;
    private final ArrayList<Integer> discardPile;
    private static CardParser parser;  // only for debug purpose...

    public CardManager() {
        parser = new CardParser();
        discardPile = new ArrayList<>();
        initializeCardPile();
    }

    /**
     * Initialize the cards and shuffle
     */
    private void initializeCardPile() {
        cardPile = new ArrayList<Integer>();
        for (int i = 1; i <= 108; i++) {
            cardPile.add(i);
        }
        Collections.shuffle(cardPile);
    }

    /**
     * Print all cards left on the draw Pile.
     */
//    public void printCardPile() {
//        if (!cardPile.isEmpty()) {
//            System.out.println("\n\n===================================================================");
//            System.out.println("Current Card Pile has " + cardPile.size() + " card left:");
//            for (int i = 0; i < cardPile.size(); i++) {
//                System.out.println(parser.parseCardID(cardPile.get(i)));
//            }
//            System.out.println("===================================================================\n\n");
//        } else {
//            System.out.println("============================================================");
//            System.out.println("There are no card left in the pile!");
//        }
//    }

    /**
     * Get the number of cards left in the draw pile.
     * @return number of cards left in the draw pile
     */
    public int numCardLeft() {
        return cardPile.size();
    }


    /**
     * Draw a certain number of cards from the draw (card) pile.
     * @param numToDraw total number to draw. Should be positive integer
     * @return an ArrayList<Integer> of card drawn from the pile, represented as their ID
     */
    public ArrayList<Integer> drawCards(int numToDraw) {
        assert(numToDraw >= 0);

        ArrayList<Integer> cardsDrawnFromDiscard = new ArrayList<>();
        if (numCardLeft() < numToDraw) {
            int numToDrawFromDiscard = numToDraw - cardPile.size();
            numToDraw -= numToDrawFromDiscard;
            cardsDrawnFromDiscard = drawCardsfromDiscardPile(numToDrawFromDiscard);
            if (numToDraw == 0) return cardsDrawnFromDiscard; // we don't need to check cardPile anymore
        }


        ArrayList<Integer> drawnCards = new ArrayList<>(cardPile.subList(0, numToDraw));
        drawnCards.addAll(cardsDrawnFromDiscard);
        cardPile.subList(0, numToDraw).clear(); // erase the drawn cards
        return drawnCards;
    }

    private ArrayList<Integer> drawCardsfromDiscardPile(int numToDraw) {
        Collections.shuffle(discardPile); // shuffle discard pile before drawing
        ArrayList<Integer> drawnCards = new ArrayList<>(discardPile.subList(0, numToDraw));
        discardPile.subList(0, numToDraw).clear(); // erase the drawn cards
        return drawnCards;

    }

    /**
     * When a player plays a legal card, insert it (represented by its ID) to discard pile.
     * @param cardID of the card to be inserted
     */
    public void insertOneCardToDiscardPile(int cardID) {
        discardPile.add(0, cardID);
    }

    /**
     * Get number of card left on discard pile.
     * @return  number of card left on discard pile
     */
    public int numLeftDiscardPile() { return discardPile.size(); }

    /**
     * Get all the cards currently in draw pile as a Array list.
     * @return a reference to the cards (as ArrayList<Integer>) in draw (card) pile.
     */
    public ArrayList<Integer> getCardPile() { return cardPile; }

    /**
     * Get all the cards currently in discard pile as a Array list.
     * @return a reference to the cards (as ArrayList<Integer>) in discard pile.
     */
    public ArrayList<Integer> getDiscardPile() { return discardPile; }


}



