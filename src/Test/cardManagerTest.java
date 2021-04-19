import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import UNO.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * These testcases test behavior of many functions, includes
 * 1. All functions in CardManager
 * 2. Interaction between Game and CardManager
 * 3. Player.{drawCards, playOneRound}
 */
class cardManagerTest {
    @Test
    /**
     * Test 0
     * Test basic funtionality of parser
     */
    void testParser() {
        CardParser parser = new CardParser();
        for (int i = 1; i <= 25; i++) {
            System.out.println(parser.parseCardID(i));
        }
        System.out.println("\n\n===================================================");
        for (int i = 51; i <= 75; i++) {
            System.out.println(parser.parseCardID(i));
        }

    }

    @Test
    /**
     * Test 1
     * make sure card manager can initialize 108 cards correctly
     */
    void testInitialization()  throws Exception {
        CardManager cardManager = new CardManager();
        assert(cardManager.getCardPile().size() == 108);
        for (int i = 1; i <= 108; i++) {
            assert (cardManager.getCardPile().contains(i));
        }
    }

    @Test
    /**
     * Test 2
     * Given n players, test behavior of initial deal, as well as behavior of draw pile
     * dependencies: Player.drawCards, CardManager.*
     */
    void testInitialDeal()  throws Exception {

        Game game1 = new Game(1,0);
        game1.initializeGame();
        for (Player p : game1.getPlayers()) {
            assert(p.getCards().size() == 7);
        }
        assert(game1.getCardManager().numCardLeft() == 101); // 108 - 7

        Game game2 = new Game(5,0);
        game2.initializeGame();
        for (Player p : game2.getPlayers()) {
            assert(p.getCards().size() == 7);
        }
        System.out.println(game2.getCardManager().numCardLeft());
        assert(game2.getCardManager().numCardLeft() == 73); // 108 - 35

        Game game3 = new Game(9, 0);
        game3.initializeGame();
        for (Player p : game3.getPlayers()) {
            assert(p.getCards().size() == 7);
        }
        assert(game3.getCardManager().numCardLeft() == 45); // 108 - 63
    }


    @Test
    /**
     * Test 3
     * Test the behavior of Player.drawCards
     * under the condition where draw pile is not enough to draw, should draw from discard pile instead
     * case 1: completely draw from discard pile
     */
    void testDrawFromDiscardWhenDrawNotEnough_Case1()  throws Exception {
        Game game = new Game(1,0);
        game.initializeGame();
        Player player = game.getPlayers().get(0);
        ArrayList<Integer> cards = player.getCards(); // direct reference to player's card
        player.drawCards(game.getCardManager().numCardLeft()); // get all cards in draw pile
        assert(game.getCardManager().numCardLeft() == 0);

        // move 30 cards from players' to discard
        for (int i = 0; i < 30; i++) {
            int cardID = cards.get(0);
            cards.remove(0);
            game.getCardManager().insertOneCardToDiscardPile(cardID);
        }
        assert(game.getCardManager().numLeftDiscardPile() == 30);

        // then drawCards
        player.drawCards(4);
        assert(game.getCardManager().numLeftDiscardPile() == 26);
    }

    @Test
    /**
     * Test 4
     * Test the behavior of Player.drawCards
     * under the condition where draw pile is not enough to draw, should draw from discard pile instead
     * case 2: partially from draw pile, partially from discard pile
     */
    void testDrawFromDiscardWhenDrawNotEnough_Case2() throws Exception{
        Game game = new Game(1,0);
        game.initializeGame();
        Player player = game.getPlayers().get(0);
        ArrayList<Integer> cards = player.getCards(); // direct reference to player's card
        player.drawCards(game.getCardManager().numCardLeft() - 2); // get 99 cards from draw pile
        assert(game.getCardManager().numCardLeft() == 2);

        // move 30 cards from players' to discard
        for (int i = 0; i < 30; i++) {
            int cardID = cards.get(0);
            cards.remove(0);
            game.getCardManager().insertOneCardToDiscardPile(cardID);
        }
        assert(game.getCardManager().numLeftDiscardPile() == 30);

        // 2 from discard, 2 from draw
        player.drawCards(4);
        assert(game.getCardManager().numCardLeft() == 0);
        assert(game.getCardManager().numLeftDiscardPile() == 28);
    }



}


