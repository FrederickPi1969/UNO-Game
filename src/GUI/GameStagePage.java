package GUI;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import UNO.*;

/**
 * !!!!!!!!!!!!!!!!!!!!!! Viewer IN MVC !!!!!!!!!!!!!!!!!!!!!!!
 * The Game UI. (Viewer)
 * Allows users to choose actions in their rounds,
 * show them their owned cards (can be hidden),
 * the current game state, and card pile status.
 *
 * MVC Design: This class won't employ any interface provided by Model (Player & ruleController).
 * It will only interact with Game (Controller).
 */

public class GameStagePage extends Frames {
    private final int W = 1080; // Width of window
    private final int H = 720; // Height of window
    private final int currentPlayerID; // the id of current player
    private final Player currentPlayer; // the cards info of this player will be on this window
    private final Game gameController; //Game controller (Controller in MVC)
    private final JFrame window; // the main frame of game stage page
    private JButton hideCardButton; // GUI element - hide
    private JButton showCardButton; // GUI element - show cards button
    private ArrayList<JButton> cardFrontImgs; // GUI element - card images on the central panel
    private ArrayList<JButton> cardBackImgs; // GUI element - card images on the central panel
    private ArrayList<Boolean> cardClickStatus; // keep track of whether cards has been clicked
    private ArrayList<Integer> playerCards;

    public GameStagePage(Game game) {
        gameController = game;  //Game controller (Controller in MVC)
        currentPlayerID = gameController.getCurrentPlayerID();
        playerCards = gameController.getPlayerCards(currentPlayerID);
        currentPlayer = gameController.getPlayers().get(currentPlayerID);

        window = new JFrame("UNO - Game Stage");
        addButtons(window);
        addPrompts(window);
        addBackground(window, W, H);
        window.setSize(W, H);
        window.setLayout(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    /**
     * Add i. "Hide Cards", ii. "Show Cards", iii. "Play owned", iv. "Draw & Play",
     * v. "Skip" to the page.
     * @param window JFrame containing the buttons
     */
    public void addButtons(JFrame window) {
        addShowCardButton(); // show card button
        addHideCardButton(); // hide card button
        addPlayOwnedButton(); // Play Owned button
        addDrawAndPlayButton(); // Draw&Play button
        addSkipButton(); // skip button
    }

    /**
     * All magic numbers are manually adjustable positionable information.
     * They are targeted for visual-understanding.
     *
     * Prompt the round info, game state info, card info, card info onto the window.
     * Refactored version from 1.1.
     * @param window JFrame containing the prompts
     */
    public void addPrompts(JFrame window) {
        /* Prompt on the Top */
        String AImark = gameController.isHuman(currentPlayerID)? "":" (AI)";
        String promptTopStr = "It's Player " + gameController.getCurrentPlayerID() + AImark + "'s round!";
        JLabel promptTop = createLabel(promptTopStr, (int) (W * 0.5), (int) (H * 0.05), 30);
        window.add(promptTop);

        /* Prompt Game State (title) */
        createTitledBorderBoxAdd(window, "Game State", (int) (0.05 * W), (int) (0.15 * H), (int) (W * 0.25), (int) (H * 0.35));

        /* Prompt Game State - last played card. */
        String prevCard = gameController.getPreviousCard();
        createTwoLinePrompts("Last Card:", prevCard, "", (int) (H * 0.2));

        /* Prompt Game State - prev player action */
        String prevPlayerAct = gameController.getPreviousAction();
        createTwoLinePrompts("Prev Player Action:", prevPlayerAct, "", (int) (H * 0.275));

        /* Prompt Game State - stacked penalty draw */
        int penaltyDraw = gameController.getPenaltyDraw();
        createTwoLinePrompts("Stacked Draw: ", ""+ penaltyDraw, "", (int) (H * 0.35));

        /* prompt next player following current game state*/
        int nextPlayerID = gameController.getNextPlayerID();
        createTwoLinePrompts("Next Player: ", ""+ nextPlayerID, "", (int) (H * 0.425));

        /* Prompt card pile information */
        createTitledBorderBoxAdd(window, "Card Piles", (int) (0.05 * W), (int) (0.5 * H), (int) (W * 0.25), (int) (H * 0.25));
        int numDrawPile = gameController.getCardManager().numCardLeft(); // add number of draw pile
        createTwoLinePrompts("Draw Pile: ", ""+ numDrawPile, "", (int) (H * 0.575));

        /* prompt next player following current game state */
        int numDiscardPile = gameController.getCardManager().numLeftDiscardPile(); // add number of discard pile
        createTwoLinePrompts("Discard Pile: ", ""+ numDiscardPile, "", (int) (H * 0.65));


        /* Prompt player hand cards */
        createTitledBorderBoxAdd(window, "Player Cards", (int) (0.3 * W), (int) (0.15 * H), (int) (W * 0.6), (int) (H * 0.6));
        cardFrontImgs = loadCardPrompts(window, true, (int) (0.3 * W), (int) (0.15 * H), (int) (W * 0.6), (int) (H * 0.6));
        cardBackImgs = loadCardPrompts(window, false, (int) (0.3 * W), (int) (0.15 * H), (int) (W * 0.6), (int) (H * 0.6));
    }

    /**
     * Create two line prompts - helper for addPrompts.
     * The line gap is set to 0.025H as default (manually adjusted).
     * @param prefix First line prompt
     * @param content the major prompting content
     * @param suffix  second lien suffix (e.g. units, or "Left")
     * @param firstLineY the y coordinate of first line.
     */
    private void createTwoLinePrompts(String prefix, String content, String suffix, int firstLineY) {
        String prevCard = gameController.getPreviousCard();
        JLabel promptLine1 = createLabel(prefix, (int) (W * 0.175), firstLineY, 15);
        JLabel promptLine2 = createLabel(content + suffix, (int) (W * 0.175),
                (int) (firstLineY + 0.025 * H), 15);
        window.add(promptLine1);
        window.add(promptLine2);
    }

    /**
     * All magic numbers are manually adjustable positionable information.
     * They are targeted for visual-understanding.
     * This function is not quite refactor-able as its inner variable has strong dependency.
     * i.e. If extract some part of it, there might be 8 parameters, which lowers readability.
     *
     * Add player's hand cards to promptHand area.
     * @param cardFront if true, load card front images, else back images
     * @param window  frame
     * @param x,y,w,h the four attributes of promptHand
     */
    private ArrayList<JButton> loadCardPrompts(JFrame window,boolean cardFront, int x, int y, int w, int h) {

        if (cardFront) cardClickStatus = new ArrayList<>();  // we don't want to track click status when cards are hidden
        ArrayList<JButton> cardImgs = new ArrayList<>();
        CardParser parser = new CardParser();
        assert (playerCards.size() <= 40); // It's hardly possible for any player to hold more than 40 cards.
        int cardBorderWidth = (int) (w / 12);
        int cardBorderHeight = (int) (h / 4.5);
        y = y + (int) (0.02 * h); // add some gap from top
        int xGap = (int) (0.01 * w); // x gap between cards
        int yGap = (int) (0.01 * h); // y gap between cards

        for (int i = 0; i < playerCards.size(); i++) {
            int cardID = playerCards.get(i);
            int xBorder = x + xGap + cardBorderWidth * (i % 10);
            int yBoarder = y + yGap + cardBorderHeight * ((int) (i / 10));
            String cardDesc = parser.parseCardID(cardID);
            String cardPath;
            if (cardFront) cardPath = "./src/GUI/assets/cards/" + cardDesc.replace(" ", "_") + ".jpg";
            else cardPath = "./src/GUI/assets/cards/back.jpg";

            ImageIcon cardImgIcon = loadImageIcon(cardPath, (int) (cardBorderWidth * 0.9), (int) (cardBorderHeight * 0.9));
            JButton cardImg = new JButton(cardImgIcon);
            cardClickStatus.add(false); // initialize the button as not clicked

            if (cardFront) { // hidden cards (back) don't need event handler
                cardImg.addActionListener(new cardClickedListener(cardImgs, cardImg));
            }
            cardImg.setBounds((int) (xBorder + xGap * 0.5), (int) (yBoarder + yGap * 1.5), (int) (cardBorderWidth * 0.9), (int) (cardBorderHeight * 0.9));
            cardImgs.add(cardImg);
            cardImg.setVisible(!cardFront); // card back images should be shown on the first hand to prevent others see the cards
            window.add(cardImg);
        }
        return cardImgs;
    }

    /**
     * All magic numbers are manually adjustable positionable information.
     * They are targeted for visual-understanding.
     *
     * Add hide card button to the window.
     */
    private void addHideCardButton() {
        // hide card button
        String hideCardButtonPath = "./src/GUI/assets/hideCardButton.png";
        hideCardButton = createImageButton(hideCardButtonPath, (int) (0.85 * W), (int) (0.05 * H), W / 10, H / 12);
        hideCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JButton cardImg : cardBackImgs) {
                    cardImg.setVisible(true);
                }
                for (JButton cardImg : cardFrontImgs) {
                    cardImg.setVisible(false);
                }
                showCardButton.setVisible(true);
                hideCardButton.setVisible(false);
            }
        });
        window.add(hideCardButton);
    }

    /**
     * All magic numbers are manually adjustable positionable information.
     * They are targeted for visual-understanding.
     *
     * Add show card button to the page.
     * Notice the magic blanks are for centering.
     */
    private void addShowCardButton() {
        // hide card button
        String hideCardButtonPath = "./src/GUI/assets/showCardButton.png";
        showCardButton = createImageButton(hideCardButtonPath, (int) (0.85 * W), (int) (0.05 * H), W / 10, H / 12);
        showCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameController.isHuman(currentPlayerID)) { // Player cannot view AI's cards
                    JOptionPane.showMessageDialog(window,"Sorry, you can't view AI's cards.","Illegal Action", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                for (JButton cardImg : cardFrontImgs) cardImg.setVisible(true);
                for (JButton cardImg : cardBackImgs) cardImg.setVisible(false);
                hideCardButton.setVisible(true);
                showCardButton.setVisible(false);
            }
        });
        window.add(showCardButton);
    }

    /**
     * All magic numbers are manually adjustable positionable information.
     * They are targeted for visual-understanding.
     *
     * Add play owned card button to the page.
     * Although quite complicated, none of the parts can be extracted.
     * Notice the magic blanks are for centering.
     */
    private void addPlayOwnedButton() {
        String playOwnedButtonPath = "./src/GUI/assets/playOwnedButton.png";
        JButton playOwnedButton = createImageButton(playOwnedButtonPath, (int) (0.2 * W) - (int) (0.065 * W), (int) (0.8 * H), W / 8, H / 10);
        playOwnedButton.addActionListener(new playOwnedButtonActionListener());
        playOwnedButton.setVisible(true);
        window.add(playOwnedButton);
    }

    /**
     * Add draw&play button to the window.
     * Notice the magic blanks are for centering.
     */
    private void addDrawAndPlayButton() {
        String drawAndPlayPath = "./src/GUI/assets/drawAndPlayButton.png";
        JButton drawAndPlayButton = createImageButton(drawAndPlayPath, (int) (0.5 * W) - (int) (0.065 * W), (int) (0.8 * H), W / 8, H / 10);
        drawAndPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameController.isHuman(currentPlayerID)) return; // human cannot help AI to make decision
                if (gameController.getNextPlayerSkiplevel() != 0) { // If there is pending stacked draw, player cannot draw&play
                    JOptionPane.showMessageDialog(window,"You can't Draw&Play because you are being skipped.","Illegal Action", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                gameController.setUserAction(2);
            }
        });
        window.add(drawAndPlayButton);
    }

    /**
     * All magic numbers are manually adjustable positionable information.
     * They are targeted for visual-understanding.
     *
     * Add skip button to the window.
     */
    private void addSkipButton() {
        String skipPath = "./src/GUI/assets/skipButton.png";
        JButton skipButton = createImageButton(skipPath, (int) (0.8 * W) - (int) (0.065 * W), (int) (0.8 * H), (int)(W / 8), (int)(H / 10));
        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameController.isHuman(currentPlayerID)) return; // human cannot help AI to make decision
                if (gameController.getNextPlayerSkiplevel() == 0)  {
                    JOptionPane.showMessageDialog(window,"You are not being skipped this round!","Illegal Action", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                gameController.setUserAction(3); // will succeed in any conditions
            }
        });
        window.add(skipButton);
    }

    /**
     * Close the game stage page of one player
     */
    public void dispose() {
        window.dispose();
    }

    /**
     * Get all selected cards in the current GUI.
     * @return all cards (as ID) selected by the user as an ArrayList<Integer>
     */
    public ArrayList<Integer> getSelectedCards() {
        ArrayList<Integer> selectedCards = new ArrayList<>();
        ArrayList<Integer> cards = playerCards;
        for (int i = 0; i < cardClickStatus.size(); i++) {
            if (cardClickStatus.get(i)) {
                selectedCards.add(cards.get(i));
            }
        }
        return selectedCards;
    }

    /**
     * When players played illegally, help them clear their previous choice.
     */
    private void clearSelectedCards() {
        for (int i = 0; i < cardFrontImgs.size(); i++) {
            cardFrontImgs.get(i).setBorder(null);
            cardClickStatus.set(i, false);
        }
    }

    /**
     * All magic numbers are manually adjustable positionable information for centering string prompts.
     * They are targeted for visual-understanding.
     *
     * The Event listener for "play owned" button.
     * Although this is a long function, it's not separable because it needs to
     * block many illegal inputs and return from the function.
     */
    private class playOwnedButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!gameController.isHuman(currentPlayerID)) return; // human cannot help AI to make decision
            ArrayList<Integer> selectedCards = getSelectedCards();
            if (gameController.getNextPlayerSkiplevel() == 3) { // previous player played "skip" card
                JOptionPane.showMessageDialog(window,"You can't Play any cards because you are being skipped.","Illegal Action", JOptionPane.INFORMATION_MESSAGE);
                clearSelectedCards(); return;
            }
            if (selectedCards.size() == 0 || selectedCards.size() > 2) { // card chosen == 0 or > 2
                JOptionPane.showMessageDialog(window,"Sorry, your play is illegal","No/Too many Selected", JOptionPane.INFORMATION_MESSAGE);
                clearSelectedCards(); return;
            }
            if (selectedCards.size() == 1) { // 1 card chosen
                int cardID = selectedCards.get(0);
                if (!gameController.isChosenCardLegalCaseOneCard(currentPlayer, cardID)) {
                    JOptionPane.showMessageDialog(window,"Sorry, your play is illegal","Illegal Play", JOptionPane.INFORMATION_MESSAGE);
                    clearSelectedCards(); return;
                }
            } else { // 2 cards chosen
                int cardID1 = selectedCards.get(0);
                int cardID2 = selectedCards.get(1);
                if (!gameController.isChosenCardLegalCaseTwoCards(currentPlayer, cardID1, cardID2)) {
                    JOptionPane.showMessageDialog(window,"Sorry, your play is illegal","Illegal Play", JOptionPane.INFORMATION_MESSAGE);
                    clearSelectedCards(); return;
                }
            }
            gameController.setUserAction(1); // the play was successful if the function arrives here
        }
    }

    private class cardClickedListener implements ActionListener {
        private ArrayList<JButton> cardImgs;  // the list of card buttons
        private JButton cardImg; // the button of a card
        public cardClickedListener(ArrayList<JButton> cardButtons, JButton cardButton) {
            cardImgs = cardButtons;
            cardImg = cardButton;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int buttonIndex = cardImgs.indexOf(cardImg);
            if (!cardClickStatus.get(buttonIndex)) { // currently the card is not clicked
                cardClickStatus.set(buttonIndex, true); // set as clicked
                cardImg.setBorder(BorderFactory.createLineBorder(Color.GREEN, 6)); // set the border as green
            } else {
                /* if the card has already been clicked
                / un-click the card, and cancel the border*/
                cardClickStatus.set(buttonIndex, false); // set as clicked
                cardImg.setBorder(null); // set the border as green
            }
        }
    }
}




