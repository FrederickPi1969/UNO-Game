import UNO.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AIPlayerTest {

    /**
     * Test whether game could initialized properly with only AI
     */
    @Test
    public void testAIInitialization() {
        Game game = new Game(0, 9);
        game.initializeGame();
        for (Player player : game.getPlayers()) {
            assert(player.getCards().size() == 7);
            assert(!player.isHuman());
        }
    }


    /**
     * Test some basic heuristics of strategic AI to guarantee normal case functionality.
     */
    public void AIScenarioTest() {
        Game game = new Game(0, 0);
        game.setGapTime(0);
        game.setUseGUI(false);
        game.setManualSetup(true);

        ArrayList<Player> players = new ArrayList<>();
        AIPlayer player1 = new ArtificialIntelligence(0, game);
        players.add(player1);
        AIPlayer player2 = new ArtificialIntelligence(1, game);
        players.add(player2);

        game.setPlayerNumbers(0,2);
        game.initializeGame();

        // suppose an AI got lots of red cards (red 1-7), and few blue number cards (blue 1-3).
        player1.getCards().clear();
        ArrayList<Integer> newCards1 = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,51,52,53));
        player1.getCards().addAll(newCards1);
        assert(player1.pickColor().equals("red")); // when wild card is available AI should pick red
        assert(player1.playCard() <= 7); // the player won't prioritize color he own less.

        // suppose an AI got both red number cards (red 1-7), and red symbol cards (21-24);
        player1.getCards().clear();
        ArrayList<Integer> newCards2 = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,21,22,23,24));
        player1.getCards().addAll(newCards2);
        assert(player1.playCard() <= 7); // AI shouldn't prioritize symbol cards than number cards.

        // Suppose an AI got lots of symbol cards (<100 below), and some wild cards (>100), he should first play symbol cards.
        player1.getCards().clear();
        ArrayList<Integer> newCards3 = new ArrayList<>(Arrays.asList(21, 24, 47, 49, 74, 99, 101, 104, 107));
        player1.getCards().addAll(newCards3);
        assert(player1.playCard() <= 100); // AI shouldn't prioritize wild cards than symbol cards.
    }

    /**
     * Evaluate the quality of strategy through large-data test.
     * This is what is known as the "downstream oriented evaluation" in machine learning.
     * when totalRounds set to 100000, the strategic AI has winning rate of 58%,
     * which is enough to reject the null hypothesis.
     * Notice 100000 rounds will be enough to capture almost any corner cases so that
     * test on component functions won't be necessary.
     * @throws InterruptedException
     */
    @Test
    public void testStrategy() throws InterruptedException {
        float totalRounds = 100000;
        float intelligenceWin = 0;
        float idiotWin = 0;

        for (int i = 0; i < totalRounds; i++) {
            Game game = new Game(0, 0);
            game.setGapTime(0);
            game.setUseGUI(false);
            game.setManualSetup(true);

            ArrayList<Player> players = new ArrayList<>();
            Player player1 = new ArtificialIntelligence(0, game);
            players.add(player1);
            Player player2 = new ArtificialIntelligence(1, game);
            players.add(player2);
            Player player3 = new ArtificialIdiot(2, game);
            players.add(player3);
            Player player4 = new ArtificialIdiot(3, game);
            players.add(player4);
            game.setPlayers(players);

            game.setPlayerNumbers(0,4);
            game.initializeGame();
            game.gameStart();
            int winnerID = game.getWinnerID();
            if (winnerID == 0 || winnerID == 1) intelligenceWin += 1;
            else idiotWin += 1;
        }

        assert(intelligenceWin + idiotWin == totalRounds); // make sure not even a single round failed
        System.out.println("Artificial intelligence winning rate: " + (intelligenceWin / totalRounds) * 100 + "%");
        System.out.println("Artificial idiot winning rate: " + (idiotWin / totalRounds) * 100 + "%");
        assert( (intelligenceWin / totalRounds) * 100 > 50);
    }
}
