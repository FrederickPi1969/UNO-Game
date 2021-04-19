import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import UNO.*;

/**
 * The last part of test:
 * Test the behavior of player when they ACTUALLY PLAY A CARD
 * <p>
 * Dependency: Game, ruleController (all test done), cardManager, CmdUI, GUI
 * <p>
 * Notice ruleController only judge validity & do update in game state
 * and pass them as information for player. Thus whether player could behave
 * correctly with given information should be tested.
 */

class PlayerTest {

    @Test
    /**
     * Test 1
     * Test when a card is played, it goes to the discard pile
     * Run 100 times to cancel randomness
     */
    void TestUserPlayWillGoToDiscardPile() {
        for (int i = 0; i < 100; i++) {
            Game game = new Game(1,0);
            game.initializeGame();
            Player player = game.getPlayers().get(0);
            player.drawCards(50);

            ArrayList<Integer> legalCards = player.findLegalCard();
            int cardToPlay = legalCards.get(0);

            // make sure this play is legal
            assert (player.optionPlayOwnedCard(cardToPlay, true));

            // make sure the top of discard of pile is the card just played
            assert (game.getCardManager().getDiscardPile().get(0) == cardToPlay);

            //make sure the player does not own this card anymore
            assert (!player.getCards().contains(cardToPlay));
        }
    }


    /**
     * Test 2
     * Test the situation where player choose to draw a card
     * Run 100 times to cancel randomness
     */
    @Test
    void testOptionDrawAndPlay() {
        for (int i = 0; i < 100; i++) {
            Game game = new Game(1,0);
            game.initializeGame();
            Player player = game.getPlayers().get(0);
            int cardPlayerWillGet = game.getCardManager().getCardPile().get(0);
            boolean newCardIsValid = game.getRuler().isValidPlay(player, cardPlayerWillGet, false);
            boolean played = player.optionDrawCardAndPlay();

            if (newCardIsValid) {
                // player should immediately play the card
                assert (played);
                assert (game.getCardManager().getDiscardPile().get(0) == cardPlayerWillGet);
                assert (!player.getCards().contains(cardPlayerWillGet));

            } else {
                assert (!played);
                assert (player.getCards().contains(cardPlayerWillGet));
            }
        }
    }



    /**
     * Test 3
     * Test when a player plays a wild card and declare the matchable color as "red",
     * the game state (controlled by ruleController) is properly updated.
     * Notice the declaration is hard-coded in stage assignment-1.0
     */
    @Test
    void testPlayerDeclareColorWithWild() {
        Game game = null;
        Player player = null;
        int wildCardID = -1;
        boolean hasWild = false;
        while (!hasWild) {
            game = new Game(1,0);
            game.initializeGame();
            player = game.getPlayers().get(0);
            for (int cardID : player.getCards()) {
                if (cardID >= 101 && cardID <= 104) {
                    wildCardID = cardID;
                    hasWild = true;
                    break;  // the player has a wild card
                }
            }
        }
        player.optionPlayOwnedCard(wildCardID, true);
        assert(game.getRuler().getMatchableColor().equals("red"));
    }


    /**
     * Test 4
     * Test extra addition rule required in assignment-1.1
     */
    @Test
    void testAdditionRule() {
        Game game = new Game(1,0);
        game.initializeGame();
        Player player = game.getPlayers().get(0);
        player.drawCards(player.getGameController().getCardManager().numCardLeft()); // get all left cards
        setCurrentState(game.getRuler(), "red", "none", "8",0);
        assert(player.optionPlayTwoOwnedCard_Add(52, 56, true)); // blue 2 + blue 6 should succeed
        // check state updated correctly
        assert(checkStateUpdatedCorrectly(game.getRuler(), "blue", "none", "8", 0));

        setCurrentState(game.getRuler(), "red", "none", "8",0);
        assert(!player.optionPlayTwoOwnedCard_Add(61, 54, true)); // blue 2 + blue 4 should fail

        setCurrentState(game.getRuler(), "red", "none", "8",0);
        assert(!player.optionPlayTwoOwnedCard_Add(79, 84, true)); // yellow 4 + yellow 9 should fail

        setCurrentState(game.getRuler(), "red", "none", "8",0);
        assert(!player.optionPlayTwoOwnedCard_Add(76, 57, true)); // yellow 1 + blue 7 should fail

        setCurrentState(game.getRuler(), "red", "none", "8",0);
        assert(!player.optionPlayTwoOwnedCard_Add(71, 74, true)); // two non-number card should fail

        setCurrentState(game.getRuler(), "red", "none", "8",0);
        assert(!player.optionPlayTwoOwnedCard_Add(101, 106, true)); // pairs containing wild card should fail
    }


    /**
     * Test 5
     * Test extra subtraction rule required in assignment-1.1
     * notice order of subtraction parameters does not matter
     */
    @Test
    void testSubtractionRule() {
        Game game = new Game(1,0);
        game.initializeGame();
        Player player = game.getPlayers().get(0);
        player.drawCards(player.getGameController().getCardManager().numCardLeft()); // get all left cards
        setCurrentState(game.getRuler(), "red", "none", "3",0);
        assert(player.optionPlayTwoOwnedCard_Sub(53, 56, true)); // blue 3 & blue 6 should succeed
        // check state updated correctly
        assert(checkStateUpdatedCorrectly(game.getRuler(), "blue", "none", "3", 0));

        setCurrentState(game.getRuler(), "red", "none", "3",0);
        assert(!player.optionPlayTwoOwnedCard_Sub(61, 54, true)); // blue 2 & blue 4 should fail

        setCurrentState(game.getRuler(), "red", "none", "3",0);
        assert(!player.optionPlayTwoOwnedCard_Add(76, 54, true)); // yellow 1 & blue 4 should fail

        setCurrentState(game.getRuler(), "red", "none", "3",0);
        assert(!player.optionPlayTwoOwnedCard_Sub(71, 74, true)); // two non-number card should fail

        setCurrentState(game.getRuler(), "red", "none", "3",0);
        assert(!player.optionPlayTwoOwnedCard_Add(101, 106, true)); // pairs containing wild card should fail

        setCurrentState(game.getRuler(), "blue", "none", "1",0);
        assert(player.optionPlayTwoOwnedCard_Sub(59, 58, true)); // blue 9 & blue 8 should succeed

    }




    /**
     * helper function to set current game state based on previous player's move
     */
    private void setCurrentState(RuleController ruler,
                                 String matchableColor,
                                 String machableSymbol,
                                 String matchabeNumber,
                                 int level) {
        ruler.setMatchableColor(matchableColor);
        ruler.setMatchableSymbol(machableSymbol);
        ruler.setMatchableNumber(matchabeNumber);
        ruler.setNextPlayerSkiplevel(level);
    }

    /**
     * check all game states are correct based on passed ground truth
     */
    private boolean checkStateUpdatedCorrectly(RuleController ruler,
                                               String truthColor,
                                               String truthSymbol,
                                               String truthNumber,
                                               int truthLevel) {
        return truthColor.equals(ruler.getMatchableColor()) &&
                truthSymbol.equals(ruler.getMatchableSymbol()) &&
                truthNumber.equals(ruler.getMatchableNumber()) &&
                truthLevel == ruler.getNextPlayerSkiplevel();
    }

}

