package UNO;

import java.util.*;
import java.lang.Thread;
import GUI.*;
import java.io.*;

/**
 * !!!!!!!!!!!!!!!!!!!!!! Controller IN MVC !!!!!!!!!!!!!!!!!!!!!!!
 * The game controller object representing a UNO game.
 * Organize the interactions of all other classes in the UNO package.
 */

public class Game {
    //    private static CardParser parser = new CardParser();
    private static final int INIT_DRAW = 7; // every player get 7 cards at beginning

    private int rounds = 1;
    private RuleController ruler;
    private CardManager gameCardManager;
    private ArrayList<Player> players;
    private boolean setupDone = false;
    private int winnerID = -1;
    private int currentPlayerID; // playerID starts from 0 !!!!!
    private int humanNum;
    private int AINum;
    private boolean startNewGame = false;
    private int gapTime = 5000; // as micro-seconds
    private boolean useGUI = true;
    private boolean manualSetup = false;

    /**
     * The action current player choose. Will only be modified by GameStagePage when
     * current player make a legal move. (i.e. play is legal)
     */
    private int currentPlayerAction;

    /**
     * Whether a color has been declared in ChooseColorPage. Will only be modified by ChooseColorPage when
     * current player plays a wild card and click one of the four color buttons.
     */
    private boolean colorIsPicked;

    /**
     * Default constructor for Game object
     * @param humanPlayerNum a number between 2 and 9 - number of human players for the game (1 for debugging only)
     * @param AIPlayerNum a number between 2 and 9 - number of AI players for the game (1 for debugging only)
     * Notice that humanPlayerNum + AIPlayerNum should be in [2,10) in formal games.
     */
    public Game(int humanPlayerNum, int AIPlayerNum) {
        humanNum = humanPlayerNum;
        AINum = AIPlayerNum;
        gameCardManager = new CardManager();
        ruler = new RuleController();
    }


    /**
     * Initialize the game. This function should only be called when number of players have been decided.
     */
    public void initializeGame() {
        int playerNum = humanNum + AINum;
        decideFirstPlayerID(playerNum);
        if (!manualSetup) initializePlayers();
        for (Player player : players) {
            player.drawCards(INIT_DRAW);
        }
    }

    private void initializePlayers() {
        int playerNum = humanNum + AINum;
        players = new ArrayList<>();
        ArrayList<Boolean> isHuman = new ArrayList<>();
        for (int i = 0; i < humanNum; i++) isHuman.add(true);
        for (int i = 0; i < AINum; i++) isHuman.add(false);
        Collections.shuffle(isHuman);

        /* playerID starts from 0 !!!!! */
        for (int i = 0; i < playerNum; i++) {
            Player player = isHuman.get(i) ? new Player(i, this) : new ArtificialIntelligence(i, this);
            players.add(player);
        }
    }

    /**
     * Randomly decide the start position of this game
     *
     * @param playerNum total number of players
     */
    private void decideFirstPlayerID(int playerNum) {
        Random rand = new Random();
        currentPlayerID = rand.nextInt(playerNum);
    }

    /**
     * Find the next player in the sequence, regardless whether he or not should be skipped
     * then update it to currentPlayerID
     */
    private void updateNextPlayerID() {
        if (ruler.getIsClockwise()) {
            currentPlayerID = (currentPlayerID + 1) % players.size();
        } else {
            currentPlayerID = (currentPlayerID - 1 + players.size()) % players.size();
        }
    }

    /**
     * This is where the game loop starts.
     */
    public void gameStart() throws InterruptedException {
        while (true) {
            colorIsPicked = false; // be ready for each human player request of picking colors.
            Player currentPlayer = players.get(currentPlayerID);
            if (currentPlayer.isHuman()) {
                handleHumanBehavior(currentPlayer);
            } else {
                handleAIBehavior((AIPlayer) currentPlayer);
            }

            if (winnerID != -1) break;
            updateNextPlayerID();
            rounds++;
        }
    }

    /**
     * This function creates the GUI for user to choose action, view hand cards, etc.
     * Servers as the "controller" for human in MVC.
     * It updates the game states stored in the RuleController class (which serves as the "Model"),
     * when players make a legal action. (shares part of the functionality of main loop)
     *
     * TEST MANUALLY
     */
    public void handleHumanBehavior(Player currentPlayer) {
        GameStagePage gamePage = new GameStagePage(this);
        currentPlayerAction = -1;

        PrintStream original = System.out;
        System.setOut(new PrintStream(new OutputStream() {public void write(int b) {}})); // we don't want garbage print-out information
        while (currentPlayerAction == -1) { // see the comment for this variable on top for details
            System.out.println("looping..."); // do not delete this line or code won't run!
        }
        System.setOut(original);

        /* Actions arriving here is guaranteed to be legal. */
        handleCurrentPlayerAction(gamePage, currentPlayer);

        if (currentPlayer.playerWin()) { // a player played out all hand cards
            winnerID = currentPlayerID;
            new PlayerWinPage(this);
        }
        gamePage.dispose();
    }

    /**
     * handle the decision made by human player. (share part of the functionality of handleHumanBehavior)
     * TESTED MANUALLY
     */
    private void handleCurrentPlayerAction(GameStagePage gamePage, Player currentPlayer) {
        switch (currentPlayerAction) {
            case 1 -> { // play owned cards - case where user plays one owned card
                ArrayList<Integer> selectedCards = gamePage.getSelectedCards();
                humanPlayOwned(currentPlayer, selectedCards);
            }
            case 2 -> { // draw & play
                /* draw pile and discard pile are both empty, we simply skip */
                if (gameCardManager.numCardLeft() + gameCardManager.numLeftDiscardPile() == 0) {
                    currentPlayer.optionSkip();
                    return;
                }
                currentPlayer.drawCards(1);
                int newCardID = currentPlayer.getCards().get(getPlayerCardNumber(currentPlayerID) - 1);
                boolean legal = currentPlayer.optionPlayOwnedCard(newCardID, true);
                String prevCardColor = ruler.getPreviousCard().split(" ")[0];
                String action = legal ? "Draw & Play (OK)" : "Draw & Play (FAIL)";
                ruler.setPreviousAction(action);
                if (prevCardColor.equals("NA")) promptPlayerChooseColor(); // wild card, choose color
            }
            case 3 -> {
                ruler.setPreviousAction("Skip");
                currentPlayer.optionSkip(); // skip
            }
        }
    }

    /**
     * Helper function for handling a human player's valid play.
     * @param currentPlayer the player being in the round
     * @param selectedCards arraylist of cards selected by the player (obtained from Viewer)
     */
    private void humanPlayOwned(Player currentPlayer, ArrayList<Integer> selectedCards) {
        if (selectedCards.size() == 1) { // player selected one card to play
            int cardID = selectedCards.get(0);
            currentPlayer.optionPlayOwnedCard(cardID, true);
            String prevCardColor = ruler.getPreviousCard().split(" ")[0];
            ruler.setPreviousAction("Play Owned (1)");
            if (prevCardColor.equals("NA")) promptPlayerChooseColor();

        } else {  // play owned cards - case where player selected two cards to play
            int cardID1 = selectedCards.get(0);
            int cardID2 = selectedCards.get(1);
            ruler.setPreviousAction("Play Owned (2)");
            /* only one of following two will succeed */
            if (!currentPlayer.optionPlayTwoOwnedCard_Add(cardID1, cardID2, true))
                currentPlayer.optionPlayTwoOwnedCard_Sub(cardID1, cardID2, true);
        }
    }

    /**
     * This function provides visualization (GUI) for rounds of AI in the game loop.
     * Servers as the "controller" for AI in MVC.
     * It updates the game states stored in the RuleController class (which serves as the "Model"),
     * when AIs make a legal action. (shares part of the functionality of main loop)
     * This function could not be combined with handleBehavior.
     */
    public void handleAIBehavior(AIPlayer currentPlayer) {
        GameStagePage gamePage = useGUI ? new GameStagePage(this) : null;
        int decision = currentPlayer.makeActionDecision();
        switch (decision) {
            case 1 -> { // play owned cards. Notice AI won't play 2 cards at one time
                AIPlayOwned(currentPlayer);
            }
            case 2 -> { // draw & play
                AIDrawAndPlay(currentPlayer);
            }
            case 3 -> { // skip this round
                ruler.setPreviousAction("Skip");
                currentPlayer.optionSkip(); // skip
            }
        }

        try { Thread.sleep(gapTime);}
        catch (Exception e) { System.out.println("Interrupted"); }  // should not be reached
        if (currentPlayer.playerWin()) { // a player played out all hand cards
            winnerID = currentPlayerID;
            if (useGUI) new PlayerWinPage(this);
        }
        if (gamePage != null) gamePage.dispose();
    }


    /**
     * Helper function for AI drawing a card and play.
     * @param currentPlayer the current AI player in the round
     */
    private void AIDrawAndPlay(AIPlayer currentPlayer) {
        currentPlayer.drawCards(1);
        int newCardID = currentPlayer.getCards().get(getPlayerCardNumber(currentPlayerID) - 1);
        boolean legal = currentPlayer.optionPlayOwnedCard(newCardID, true);
        String prevCardColor = ruler.getPreviousCard().split(" ")[0];
        if (prevCardColor.equals("NA")) { // a wild card is played by AI
            String colorDecision = currentPlayer.pickColor();
            ruler.updateGameStateBasedOnPickedColor(colorDecision);
        }
        String action = legal ? "Draw & Play (OK)" : "Draw & Play (FAIL)";
        ruler.setPreviousAction(action);
    }

    /**
     * Helper function for AI playing his owned cards.
     * @param currentPlayer the current AI player in the round
     */
    private void AIPlayOwned(AIPlayer currentPlayer) {
        int cardID = currentPlayer.playCard();
        currentPlayer.optionPlayOwnedCard(cardID, true);
        String prevCardColor = ruler.getPreviousCard().split(" ")[0];
        if (prevCardColor.equals("NA")) { // a wild card is played by AI
            String colorDecision = currentPlayer.pickColor();
            ruler.updateGameStateBasedOnPickedColor(colorDecision);
        }
        ruler.setPreviousAction("Play Owned (1)");  // (1) because one card is played
    }


    /**
     * Getter for game rounds.
     *
     * @return rounds of game already proceeded as integer
     */
    public int getRounds() {
        return rounds;
    }


    /**
     * Get game state (rule controller) info.
     *
     * @return game state information wrapper (ruleController)
     */
    public RuleController getRuler() {
        return ruler;
    }

    /**
     * Get game card manager of the game.
     * @return game card manager object of current game
     */
    public CardManager getCardManager() {
        return gameCardManager;
    }

    /**
     * Getter for all players in a game.
     *
     * @return arraylist<Player> representing all players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Get the current player ID. Built for GUI (Viewer in MVC).
     * @return the ID of currentPlayer
     */
    public int getCurrentPlayerID() {
        return currentPlayerID;
    }

    /**
     * Get next player ID. Built for GUI.
     * @return the ID of nextPlayer, following current game state
     */
    public int getNextPlayerID() {
        if (ruler.getIsClockwise()) return (currentPlayerID + 1) % players.size();
        else return (currentPlayerID - 1 + players.size()) % players.size();
    }

    /**
     * Get the number of cards of one player
     * @return the number of cards left in the player's hand
     */
    public int getPlayerCardNumber(int playerID) {
        return players.get(playerID).getCards().size();
    }

    /**
     * Get the winner ID of the game.
     *
     * @return winner ID.
     */
    public int getWinnerID() {
        return winnerID;
    }

    /**
     * actionID - { 1 : play, 2 : draw & play, 3 : skip}
     */
    public void setUserAction(int actionID) {
        currentPlayerAction = actionID;
    }

    /**
     * Set color is picked as TRUE.
     */
    public void setColorIsPicked() {
        colorIsPicked = true;
    }

    /**
     * Interface for manually add players into the game.
     * @param playerList ArrayList<Player> of players to add
     */
    public void setPlayers(ArrayList<Player> playerList) {
        players = playerList;
    }

    /**
     * Create a pop-up window for users who played wild cards to pick color.
     * TESTED MANUALLY!
     */
    private void promptPlayerChooseColor() {
        ChooseColorPopUp colorPage = new ChooseColorPopUp(this);
        while (!colorIsPicked) {
            colorPage.setAsVisible(true); // prevent clicking on "X" by chance
            if (colorIsPicked) colorPage.setAsVisible(false); // prevent race condition
        }
    }

    /**
     * Set the number of player numbers.
     * @param humanPlayerNum the number of human players
     * @param AIPlayerNum the number of AI players
     */
    public void setPlayerNumbers(int humanPlayerNum, int AIPlayerNum) {
        humanNum = humanPlayerNum;
        AINum = AIPlayerNum;
    }

    /**
     * Prepared for GUI - player number page (view) to inform the game controller that set up is ready.
     */
    public void setSetupDone() {
        setupDone = true;
    }

    /**
     * Whether the game initialization set-up is ready.
     */
    public boolean isSetupDone() {
        return setupDone;
    }

    /**
     * Prepared for GUI - winner page (view) to inform the game controller to start a new game.
     */
    public void setStartNewGame() {
        startNewGame = true;
    }

    /**
     * Whether to start a new game after this game is finished
     * @return Whether to start a new game after this game is finished
     */
    public boolean toStartNewGame() {
        return startNewGame;
    }

    /**
     * To set a time gap after each AI player made a decision.
     * @param t time for sleeping (as 1/1000 seconds)
     */
    public void setGapTime(int t) {
        gapTime = t;
    }

    /**
     * Whether or not to start a game with GUI.
     * @param b a boolean value
     */
    public void setUseGUI(boolean b) {
        useGUI = b;
    }

    /**
     * Whether to set up a game manually for debugging
     * @param b a boolean value
     */
    public void setManualSetup(boolean b) {
        manualSetup = b;
    }

    /**
     * Get the previous played cards from RuleController(Model in MVC) for GUI (viewer).
     * @return previous card played
     */
    public String getPreviousCard() {
        return ruler.getPreviousCard();
    }

    /**
     * Get action performed by previous player from RuleController(Model in MVC) for GUI(viewer).
     * @return action of previous player
     */
    public String getPreviousAction() {
        return ruler.getPreviousAction();
    }

    /**
     * Get penalty draw information from RuleController (Model in MVC) for GUI (Viewer).
     * @return stacked penalty draw
     */
    public int getPenaltyDraw() {
        return ruler.getPenaltyDraw();
    }

    /**
     * Get current skip level from RuleController (Model in MVC) for GUI (Viewer).
     * @return the skip level for next player.
     */
    public int getNextPlayerSkiplevel() {
        return ruler.getNextPlayerSkiplevel();
    }

    /**
     * Handle request by viewer (GUI) to update game state stored in Model (RuleController),
     * when player plays a wild/wildDraw4 card.
     * @param color color picked by the player
     */
    public void updateGameStateBasedOnPickedColor(String color) {
        ruler.updateGameStateBasedOnPickedColor(color);
    }

    /**
     * Get the hand cards of a given player (Model in MVC) for the Viewer.
     * @param playerID the target player
     * @return hand cards of a player
     */
    public ArrayList<Integer> getPlayerCards(int playerID) {
        return players.get(playerID).getCards();
    }

    /**
     * Helps Viewer (GUI) know whether a player is human.
     * @param playerID the target player
     * @return whether the player is human
     */
    public boolean isHuman(int playerID) {
        return players.get(playerID).isHuman();
    }

    /**
     * Helps Viewer (GUI) judge whether a chosen card is legal play.
     * @param currentPlayer the player playing the card
     * @param cardID the card being played
     * @return whether the card is legal or not
     */
    public boolean isChosenCardLegalCaseOneCard(Player currentPlayer, int cardID) {
        return currentPlayer.optionPlayOwnedCard(cardID, false);
    }

    /**
     * Helps Viewer (GUI) judge whether two chosen cards are legal play using addition /subtraction rules.
     * @param currentPlayer the player playing the card
     * @param cardID1,cardID2 two cards chosen by the player
     * @return whether the composed play is legal or not
     */
    public boolean isChosenCardLegalCaseTwoCards(Player currentPlayer, int cardID1, int cardID2) {
        boolean addLegal = currentPlayer.optionPlayTwoOwnedCard_Add(cardID1, cardID2, false);
        boolean subLegal = currentPlayer.optionPlayTwoOwnedCard_Sub(cardID1, cardID2, false);
        return addLegal || subLegal;
    }

}


