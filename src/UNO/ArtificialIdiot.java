package UNO;

import javax.print.attribute.standard.Finishings;
import java.util.*;

public class ArtificialIdiot extends AIPlayer {

    private ArrayList<Integer> legalCards;
    /**
     * Constructor for player objects
     *
     * @param ID   An unique integer identifier for a player
     * @param game An game controller object representing the game player being in
     */
    public ArtificialIdiot(int ID, Game game) {
        super(ID, game);
    }

    /**
     * This is how AI makes its action decision. This decision should always be legal.
     * @return 1 ("PlayOwned"), 2 ("Draw&Play"), 3 ("SKip")
     */
    public int makeActionDecision() {
        int currentSkipLevel = getGameController().getRuler().getNextPlayerSkiplevel(); // whether previous play is draw2, wild4, or skip
        if (currentSkipLevel == 3) { // "skip card"
            return 3; // skip his turn
        }
        legalCards = findLegalCard();
        if (currentSkipLevel != 0 && legalCards.size() == 0) {
            // there's pending stacked draw, and AI don't have legal card to play
            return 3; // skip his turn and takes stacked penalty
        }
        if (legalCards.size() == 0) {
            // there's no pending stacked draw, and AI don't have legal card to play
            return 2; // draw and play
        }
        return 1; // play owned cards
    }


    /**
     * Randomly choose a card to play. Notice AI will only make this decision when it has legal cards.
     * @return The card (as ID) that AI decided to play
     */
    public int playCard() {
        Random rand = new Random();
        int randIndex = rand.nextInt(legalCards.size());
        return legalCards.get(randIndex);
    }

    /**
     * AI will its preferred color  when played a wild cards up calling this function.
     * @return Color picked by AI
     */
    public String pickColor() {
        ArrayList<Integer> colors = new ArrayList<Integer>(Arrays.asList(1,2,3,4));
        Collections.shuffle(colors);
        return colorID2color(colors.get(0));
    }

}
