import GUI.GameStagePage;
import com.sun.source.tree.UsesTree;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import javax.print.attribute.standard.Finishings;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.Random;
import UNO.*;

/**
 * A comprehensive test for RuleController class, Including
 * 1. validating each card in given contexts (including behavior of wildDraw4)
 * 2. updating the gameState (matchable color, number, symbol) when player plays a specific card
 * 3. Initialization of initial game state
 */

class RuleControllerTest {
    private static CardParser parser = new CardParser();

    @Test
    /**
     * 0. A magic player owning all cards cannot play wildDraw4 anyway
     * Test lots of combinations of color (r,g,b,y) & symbol (skip, reverse)
     */
    void test_Player_AllCards_CannotWildDraw4() throws Exception {
        Player player = playerAllCards();
        System.out.println(player.getCards());
        RuleController ruler = new RuleController();

        setCurrentState(ruler, "red", "none", "6", 0);
        assert(!ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "yellow", "reverse", "none", 0);
        assert(!ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "blue", "draw2", "none", 1); // previous blue draw2
        assert(!ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "yellow", "none", "none", 2); // previous wildDraw4, declared yellow
        assert(!ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "red", "skip", "none", 3);
        assert(!ruler.isValidPlay(player, 105, false));
    }


    @Test
    /**
     * 1. if current allowed color is red, player with no red cards can use wild Draw 4
     */
    void test_Player_Red_N_WildDraw4_Y_UseWildDraw4() throws Exception {
        Player player = player_Red_N_WildDraw4_Y();
        RuleController ruler = new RuleController();

        setCurrentState(ruler, "red", "none", "6", 0);
        assert(ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "red", "reverse", "none", 0);
        assert(ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "red", "draw2", "none", 1); // previous red draw2
        assert(ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "red", "none", "none", 2); // previous wildDraw4
        assert(ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "red", "skip", "none", 3);
        assert(!ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "blue", "reverse", "none", 0);
        assert(!ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "green", "draw2", "none", 0);
        assert(!ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "yellow", "skip", "none", 3);
        assert(!ruler.isValidPlay(player, 105, false));
     }

    @Test
    /**
     * 2. test player with all colors cannot use wildDraw4 anyway
     */

    void test_Player_AllColors_CannotUseWildDraw4() throws Exception {
        Player player = player_AllColor_Y_WildDraw4_Y();
        RuleController ruler = new RuleController();

        setCurrentState(ruler, "red", "none", "6", 0);
        assert(!ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "yellow", "reverse", "none", 0);
        assert(!ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "blue", "draw2", "none", 1); // previous blue draw2
        assert(!ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "yellow", "none", "none", 2); // previous wildDraw4, declared yellow
        assert(!ruler.isValidPlay(player, 105, false));

        setCurrentState(ruler, "red", "skip", "none", 3);
        assert(!ruler.isValidPlay(player, 105, false));
    }



    @Test
    /**
     * 3. test previous card: red 8
     */
    void testSituation_Red8() throws Exception {

        RuleController ruler = new RuleController();
        Player player = playerAllCards(); // a player who owns all cards

        setCurrentState(ruler,"red", "none", "8", 0);

        ArrayList<Integer> validByRuler = getAllLegalCardsByRuler(ruler, player);
        ArrayList<Integer> groundTruth = groundTruthGenerator(ruler, player,
                ruler.getMatchableColor(), ruler.getMatchableNumber(), ruler.getMatchableSymbol());

        System.out.println(validByRuler);
        System.out.println(groundTruth);
        assert(validByRuler.equals(groundTruth));

    }


    @Test
    /**
     * 4. test previous card: yellow 0
     */
    void testSituation_Yellow0() throws Exception {
        RuleController ruler = new RuleController();
        Player player = playerAllCards(); // a player who owns all cards

        setCurrentState(ruler, "yellow", "none", "0", 0);

        ArrayList<Integer> validByRuler = getAllLegalCardsByRuler(ruler, player);
        ArrayList<Integer> groundTruth = groundTruthGenerator(ruler, player,
                ruler.getMatchableColor(), ruler.getMatchableNumber(), ruler.getMatchableSymbol());

        System.out.println(validByRuler);
        System.out.println(groundTruth);
        assert(validByRuler.equals(groundTruth));
    }

    @Test
    /**
     * 5. test previous card: blue skip
     */
    void testSituation_blueSkip() throws Exception {
        RuleController ruler = new RuleController();
        Player player = playerAllCards(); // a player who owns all cards

        setCurrentState(ruler, "blue", "skip", "none", 3);

        ArrayList<Integer> validByRuler = getAllLegalCardsByRuler(ruler, player);
        ArrayList<Integer> groundTruth = groundTruthGenerator(ruler, player,
                ruler.getMatchableColor(), ruler.getMatchableNumber(), ruler.getMatchableSymbol());

        System.out.println(validByRuler);
        System.out.println(groundTruth);
        assert(validByRuler.equals(groundTruth));
    }

    @Test
    /**
     * 6. test previous card: green reverse
     */
    void testSituation_greenReverse() throws Exception {
        RuleController ruler = new RuleController();
        Player player = playerAllCards(); // a player who owns all cards

        setCurrentState(ruler, "green", "reverse", "none", 0);

        ArrayList<Integer> validByRuler = getAllLegalCardsByRuler(ruler, player);
        ArrayList<Integer> groundTruth = groundTruthGenerator(ruler, player,
                ruler.getMatchableColor(), ruler.getMatchableNumber(), ruler.getMatchableSymbol());

        System.out.println(validByRuler);
        System.out.println(groundTruth);
        assert(validByRuler.equals(groundTruth));
    }

    @Test
    /**
     * 7. test previous card: red draw2
     */
    void testSituation_RedDraw2() throws Exception {
        RuleController ruler = new RuleController();
        Player player = playerAllCards(); // a player who owns all cards

        setCurrentState(ruler, "red", "draw2", "none", 1);

        ArrayList<Integer> validByRuler = getAllLegalCardsByRuler(ruler, player);
        ArrayList<Integer> groundTruth = groundTruthGenerator(ruler, player,
                ruler.getMatchableColor(), ruler.getMatchableNumber(), ruler.getMatchableSymbol());

        System.out.println(validByRuler);
        System.out.println(groundTruth);
        assert(validByRuler.equals(groundTruth));
    }


    @Test
    /**
     * 8. test previous card: wild with red as declared color
     */
    void testSituation_wildDeclaredRed() throws Exception {
        RuleController ruler = new RuleController();
        Player player = playerAllCards(); // a player who owns all cards

        setCurrentState(ruler, "red", "none", "none", 0);

        ArrayList<Integer> validByRuler = getAllLegalCardsByRuler(ruler, player);
        ArrayList<Integer> groundTruth = groundTruthGenerator(ruler, player,
                ruler.getMatchableColor(), ruler.getMatchableNumber(), ruler.getMatchableSymbol());

        System.out.println(validByRuler);
        System.out.println(groundTruth);
        assert(validByRuler.equals(groundTruth));
    }


    @Test
    /**
     * 9. test previous card: wildDraw4 with red as declared color
     */
    void testSituation_wildDraw4DeclaredRed() throws Exception {
        RuleController ruler = new RuleController();
        Player player = playerAllCards(); // a player who owns all cards

        setCurrentState(ruler, "red", "none", "none", 2);

        ArrayList<Integer> validByRuler = getAllLegalCardsByRuler(ruler, player);
        ArrayList<Integer> groundTruth = groundTruthGenerator(ruler, player,
                ruler.getMatchableColor(), ruler.getMatchableNumber(), ruler.getMatchableSymbol());

        System.out.println(validByRuler);
        System.out.println(groundTruth);
        assert(validByRuler.equals(groundTruth));
    }


    @Test
    /**
     * 10. Test if ruler is updated correctly if last round was red 8
     * suppose the user cannot play wildDraw4
     * All possible plays are tested - no need to do more other than wildDraw4
     */
    void testUpdate_Red8() {

        RuleController ruler = new RuleController();
        Player player = playerAllCards(); // a player who owns all cards

        // current player plays red 0
        setCurrentState(ruler, "red", "none", "8", 0);
        ruler.isValidPlay(player, 25, true);
        assert(checkStateUpdatedCorrectly(ruler, "red", "none", "0", 0));


        // current player plays red skip card
        setCurrentState(ruler, "red", "none", "8", 0);
        ruler.isValidPlay(player, 20,true);
        assert(checkStateUpdatedCorrectly(ruler, "red", "skip", "none", 3));

        // current player plays red reverse card
        setCurrentState(ruler, "red", "none", "8", 0);
        ruler.isValidPlay(player, 22,true);
        assert(checkStateUpdatedCorrectly(ruler, "red", "reverse", "none", 0));
        assert(!ruler.getIsClockwise());

        // current player plays red draw 2
        ruler.changeGameOrder();
        setCurrentState(ruler, "red", "none", "8", 0);
        ruler.isValidPlay(player, 24,true);
        assert(checkStateUpdatedCorrectly(ruler, "red", "draw2", "none", 1));
        assert(ruler.getPenaltyDraw() == 2); // next player should draw 2


        // current player plays blue 8
        setCurrentState(ruler, "red", "none", "8", 0);
        ruler.isValidPlay(player, 58, true);
        assert(checkStateUpdatedCorrectly(ruler, "blue", "none", "8", 0));

        // current player plays wild card
        setCurrentState(ruler, "red", "none", "8", 0);
        ruler.isValidPlay(player, 102, true); // wild card, color should be NA, a special state
        assert(checkStateUpdatedCorrectly(ruler, "NA", "none", "none", 0));
    }

    /**
     * 11. test behavior of wildDraw4
     * given player has no red card, but has a wildDraw4 card
     */
    @Test
    void testUpdate_WildDraw4() {
        RuleController ruler = new RuleController();
        Player player = player_Red_N_WildDraw4_Y();

        // previous play red 8
        ruler.resetPenaltyDraw();
        setCurrentState(ruler, "red", "none", "8", 0);
        ruler.isValidPlay(player, 108, true);
        assert(checkStateUpdatedCorrectly(ruler, "NA", "none", "none", 2));
        assert(ruler.getPenaltyDraw() == 4);

        // previous play red draw 2  -- penalty draw should be 6 after wildDraw4 played
        ruler.resetPenaltyDraw();
        setCurrentState(ruler, "red", "draw2", "none", 1);
        ruler.increasePenaltyDraw(2);
        ruler.isValidPlay(player, 108, true);
        assert(checkStateUpdatedCorrectly(ruler, "NA", "none", "none", 2));
        assert(ruler.getPenaltyDraw() == 6);


        // previous play wildDraw 4, and declared red as matchable color
        ruler.resetPenaltyDraw();
        setCurrentState(ruler, "red", "none", "none", 2);
        ruler.increasePenaltyDraw(4);
        System.out.println(ruler.isValidPlay(player, 108, true));
        assert(checkStateUpdatedCorrectly(ruler, "NA", "none", "none", 2));
        assert(ruler.getPenaltyDraw() == 8);

        // previous play red draw 2  -- penalty draw should be 4 after blue skip 2 played
        ruler.resetPenaltyDraw();
        setCurrentState(ruler, "red", "draw2", "none", 1);
        ruler.increasePenaltyDraw(2);
        ruler.isValidPlay(player, 48, true); // green draw 2
        assert(checkStateUpdatedCorrectly(ruler, "green", "draw2", "none", 1));
        assert(ruler.getPenaltyDraw() == 4);

    }


    /**
     *  12. Test initialization of beginning game state is done properly
     *  As this is a random process, it should be repeated many times using for loop
     */
    @Test
    void testInitialization() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("red");
        colors.add("green");
        colors.add("blue");
        colors.add("yellow");

        ArrayList<String> numbers = new ArrayList<>();
        for(int i = 0; i <= 9; i++) {
            numbers.add((Integer.toString(i)));
        }

        for (int i = 0; i < 500; i++) {
            RuleController ruler = new RuleController();
            assert(colors.contains(ruler.getMatchableColor()));
            assert(numbers.contains(ruler.getMatchableNumber()));
            assert(ruler.getMatchableSymbol().equals("none"));
        }

    }

    /**
     * 13. Test checkSkipAndDraw
     * If a player should be skipped this round, he should then draw the cumulative penalty cards
     * Behavior of card Manager is also checked in this test case
     */
    @Test
    void testCheckSkipAndDraw() {
        Game game = new Game(1,0);
        game.initializeGame();
        Player player = player_AllColor_Y_WildDraw4_Y(); // he cannot play wildDraw4 anyway
        player.setPlayerID(2);
        player.setGameController(game);
        player.setRuler(game.getRuler());

        // case 1: Red skip - player should not be skipped
        int playerNumCheckPoint1 = player.getCards().size();
        int pileNumCheckPoint1 = game.getCardManager().numCardLeft();
        setCurrentState(game.getRuler(), "red", "skip", "none", 3);
        game.getNextPlayerID();
        assert(game.getRuler().checkSkipAndDraw(player, true));
        assert(player.getCards().size() == playerNumCheckPoint1);
        assert(game.getCardManager().numCardLeft() == pileNumCheckPoint1);

        // case2: Blue draw2 - player should be skipped and draw 2
        int playerNumCheckPoint2 = player.getCards().size();
        int pileNumCheckPoint2 = game.getCardManager().numCardLeft();
        setCurrentState(game.getRuler(), "blue", "draw2", "none", 1);
        game.getRuler().increasePenaltyDraw(2);
        assert(game.getRuler().checkSkipAndDraw(player, true));
        assert(player.getCards().size() == playerNumCheckPoint2 + 2);
        assert(game.getCardManager().numCardLeft() == pileNumCheckPoint2 - 2);
        game.getRuler().resetPenaltyDraw();


        // case3: wildDraw4 - player should be skipped and draw 4
        int playerNumCheckPoint3 = player.getCards().size();
        int pileNumCheckPoint3 = game.getCardManager().numCardLeft();
        setCurrentState(game.getRuler(), "blue", "draw2", "none", 2);
        game.getRuler().increasePenaltyDraw(4);
        assert(game.getRuler().checkSkipAndDraw(player, true));
        assert(player.getCards().size() == playerNumCheckPoint3 + 4);
        assert (game.getCardManager().numCardLeft() == pileNumCheckPoint3 - 4);
        game.getRuler().resetPenaltyDraw();

    }

    /**
     * 14. Miscellaneous test cases (getters/setters) for Game Controller for coverage concerns...
     * It also test the whether game could handle AI behaviors without any bugs.
     */
    @Test
    public void testGameControlGeneralCaseAI() throws InterruptedException {
        Game game = new Game(0,0);
        RuleController ruler = game.getRuler();
        game.setPlayerNumbers(0,2);
        game.setStartNewGame();
        assert(game.toStartNewGame());

        game.setGapTime(10);
        game.setUseGUI(false);
        game.initializeGame();
        game.setManualSetup(false); // we do not want to initialize players manually
        game.setSetupDone();

        // test whether the game has been set up as expected
        assert(game.isSetupDone());
        assert(game.getPlayers().size() == 2);
        assert(game.getRounds() == 1);
        assert(ruler.getPenaltyDraw() == 0);
        assert(game.getPlayerCardNumber(0) == 7);
        assert(!ruler.getPreviousCard().equals("none"));
        assert(ruler.getPenaltyDraw() == 0);

        ruler.updateGameStateBasedOnPickedColor("red");
        assert(ruler.getPreviousCard().startsWith("red"));
        int currentPlayerID = game.getCurrentPlayerID();
        Player currentPlayer = game.getPlayers().get(currentPlayerID);
        assert(game.getNextPlayerID() == (currentPlayerID + 1) % 2);
        game.handleAIBehavior((AIPlayer) currentPlayer);
    }

    /**
     * 15. Test whether game controller could hanel human behavior without any bugs
     */
    @Test
    public void testGameControlCaseHuman() throws InterruptedException {
        Game game = new Game(0,0);
        game.setPlayerNumbers(2,0);
        game.initializeGame();
        int currentPlayerID = game.getCurrentPlayerID();
        Player currentPlayer = game.getPlayers().get(currentPlayerID);
        game.handleHumanBehavior(currentPlayer);
    }


    /**
     *  helper function:
     *  extract all the cards that the ruler think as legal
     */
    private ArrayList<Integer> getAllLegalCardsByRuler(RuleController ruler, Player player) {
        ArrayList<Integer> validList = new ArrayList<>();
        for (int cardID : player.getCards()) {
            if (ruler.isValidPlay(player, cardID, false)) {
                validList.add(cardID);
            }
        }
        return validList;
    }

    /**
     * extract all the cards that are indeed legal (ground true generator)
     */
    private ArrayList<Integer> groundTruthGenerator(RuleController ruler,
                                                    Player player,
                                                    String colorTruth,
                                                    String numberTruth,
                                                    String symbolTruth) {
        ArrayList<Integer> validList = new ArrayList<>();
        for (int cardID : player.getCards()) {
            String cardDescription = parser.parseCardID(cardID);
            String[] result = parser.parseCardDescription(cardDescription);
            String cardCol = result[0];
            String cardContent = result[2];

            if (ruler.getNextPlayerSkiplevel() == 3) {
                continue; // skip card played. any card will be illegal

            } else if (ruler.getNextPlayerSkiplevel() == 2) {
                // only wildDraw4 is allowed
                if (cardContent.equals("wildDraw4") && ruler.isValidPlay(player, cardID, false)) {
                     // this call is valid because wildDraw4 behavior has passed the test.
                    validList.add(cardID);
                }

            } else if (ruler.getNextPlayerSkiplevel() == 1) {
                // both draw 2 and wild draw 4 are allowed
                if (cardContent.equals("wildDraw4") && ruler.isValidPlay(player, cardID, false)) {
                    validList.add(cardID);
                } else if (cardContent.equals("draw2")) {
                    validList.add(cardID);
                }

            } else {
                // cards with any one of color, number or symbol matched are playable
                if (cardContent.equals("wild")) {
                    validList.add(cardID);
                } else if (cardContent.equals("wildDraw4") && ruler.isValidPlay(player, cardID, false)) {
                    validList.add(cardID);
                } else {
                    if (cardCol.equals(colorTruth)) {
                        validList.add(cardID);
                    } else if (cardContent.equals(numberTruth)) {
                        validList.add(cardID);
                    } else if (cardContent.equals(symbolTruth)) {
                        validList.add(cardID);
                    }
                }
            }
        }

        return validList;
    }

    /**
     * construct a player who has no red cards, but a wildDraw4 card
     * This player should be able to play wildDraw4 card if current allowed color is red
     * @return A player with no red cards but a wildDraw4 card.
     */
    private Player player_Red_N_WildDraw4_Y() {
        Player player = new Player(0, null);
        player.addOneCard(26); // green 1
        player.addOneCard(51); // blue 1
        player.addOneCard(76); // yellow 1
        player.addOneCard(101); // wild
        player.addOneCard(105); // wildDraw 4
        return player;
    }

    /**
     * Construct a player who owns all colors, and also a wildDraw4 card
     * This player should not be able to play wildDraw4 card.
     * @return
     */
    private Player player_AllColor_Y_WildDraw4_Y() {
        Player player = new Player(0, null);
        player.addOneCard(1); // red 1
        player.addOneCard(26); // green 1
        player.addOneCard(51); // blue 1
        player.addOneCard(76); // yellow 1
        player.addOneCard(101); // wild
        player.addOneCard(105); // wildDraw 4
        return player;
    }

    /**
     * Construct a magic player who own ALL CARDS
     * This player should not be able to play wildDraw4 card
     */
    private Player playerAllCards() {
        Player player = new Player(0, null);
        for (int i = 1; i <= 108; i++) {
            player.addOneCard(i);
        }
        return player;
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

