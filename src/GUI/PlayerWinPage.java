package GUI;

import UNO.Game;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * !!!!!!!!!!!!!!!!!!!!!! Viewer IN MVC !!!!!!!!!!!!!!!!!!!!!!!
 * Viewer for ending scene where a winner has been decided.
 *
 * MVC Design: This class won't employ any interface provided by Model (Player & ruleController).
 * It will only interact with Game (Controller).
 */
public class PlayerWinPage extends Frames {
    private final int W = 1080; // Width of window
    private final int H = 720; // Height of window
    private final int winnerID;
    Game gameController;

    /**
     * @param game game Controller (Controller in MVC)
     */
    public PlayerWinPage(Game game) {
        gameController = game;
        winnerID = game.getWinnerID();  // should be the correct winner passed by game controller!
        JFrame window = new JFrame("UNO - Player Won");
        addButtons(window);
        addPrompts(window);
        addBackground(window, W, H);
        window.setSize(W, H);
        window.setLayout(null);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    /**
     * All magic numbers are manually adjustable positionable information.
     * They are targeted for visual-understanding.
     *
     * Add "new game" button to the page
     * @param window JFrame containing the buttons
     */
    public void addButtons(JFrame window) {
        // new start button
        String startButtonPath = "./src/GUI/assets/newGameButton.png";
        JButton startButton = createImageButton(startButtonPath, (int) (W / 2 - W * 0.1), (int) (H * 0.55), (int) (W / 5), (int)(H / 10));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameController.setStartNewGame();
                window.dispose();
            }
        });
        window.add(startButton);


    }

    /**
     * All magic numbers are manually adjustable positionable information.
     * They are targeted for visual-understanding.
     *
     * Add two lines for winner-prompt into the page.
     * @param window JFrame containing the prompts
     */
    public void addPrompts(JFrame window) {
        // Prompt
        String AIMark = gameController.getPlayers().get(winnerID).isHuman() ? "" : " (AI)";
        String promptWord = "Player " + winnerID + AIMark + " wins!";
        JLabel prompt1 = createLabel(promptWord, (int) (W * 0.5), (int) (H * 0.25),  30);
        JLabel prompt2 = createLabel("Congratulations!!!", (int) (W * 0.5), (int) (H * 0.35), 30);
        window.add(prompt1);
        window.add(prompt2);
    }

}
