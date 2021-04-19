package GUI;
import UNO.Game;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * !!!!!!!!!!!!!!!!!!!!!! Viewer IN MVC !!!!!!!!!!!!!!!!!!!!!!!
 * Viewer for Scenario where players plays a wild car.
 * Prompt the player to choose a color and pass this info to Controller.
 *
 * MVC Design: This class won't employ any interface provided by Model (Player & ruleController).
 * It will only interact with Game (Controller).
 */
public class ChooseColorPopUp extends Frames {
    private final int W = 540; // Width of window
    private final int H = 360; // Height of window
    private Game gameController;
    private JFrame window;

    /**
     * @param game the game controller in the MVC
     */
    public ChooseColorPopUp(Game game) {
        gameController = game;
        window = new JFrame("UNO - Choose Color");
        addButtons(window);
        addPrompts(window);
        addBackground(window, W, H);
        window.setSize(W, H);
        window.setLayout(null);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }


    /**
     * All magic numbers are manually adjustable positionable information.
     * They are targeted for visual-understanding.
     *
     * Add red, green, blue, yellow buttons to the page.
     * @param window JFrame containing the buttons
     */
    public void addButtons(JFrame window) {
        // red
        String redButtonPath = "./src/GUI/assets/redButton.png";
        JButton redButton = createImageButton(redButtonPath, (int) (W * 0.25 - W / 10), (int) (H * 0.55), W / 5, H / 8);
        window.add(redButton);
        redButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameController.updateGameStateBasedOnPickedColor("red");
                window.dispose();
                window.setVisible(false);
                gameController.setColorIsPicked(); // inform controller color picking is done
            }
        });

        // green
        String greenButtonPath = "./src/GUI/assets/greenButton.png";
        JButton greenButton = createImageButton(greenButtonPath, (int) (W * 0.25 - W / 10), (int) (H * 0.75), W / 5, H / 8);
        window.add(greenButton);
        greenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameController.updateGameStateBasedOnPickedColor("green");
                window.dispose();
                window.setVisible(false);
                gameController.setColorIsPicked();  // inform controller color picking is done
            }
        });

        // blue
        String blueButtonPath = "./src/GUI/assets/blueButton.png";
        JButton blueButton = createImageButton(blueButtonPath, (int) (W * 0.75 - W / 10), (int) (H * 0.55), W / 5, H / 8);
        window.add(blueButton);
        blueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameController.updateGameStateBasedOnPickedColor("blue");
                window.dispose();
                window.setVisible(false);
                gameController.setColorIsPicked();  // inform controller color picking is done
            }
        });

        // yellow
        String yellowButtonPath = "./src/GUI/assets/yellowButton.png";
        JButton yellowButton = createImageButton(yellowButtonPath, (int) (W * 0.75 - W / 10), (int) (H * 0.75), W / 5, H / 8);
        window.add(yellowButton);
        yellowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameController.updateGameStateBasedOnPickedColor("yellow");
                window.dispose();
                window.setVisible(false);
                gameController.setColorIsPicked();  // inform controller color picking is done
            }
        });
    }

    /**
     * All magic numbers are manually adjustable positionable information for string prompts.
     * They are targeted for visual-understanding.
     *
     * Add two lines of string prompts into the page.
     * @param window JFrame containing the prompts
     */
    public void addPrompts(JFrame window) {
        // Prompt
        JLabel prompt1 = createLabel( "You played a wild card", (int) (W * 0.5), (int) (H * 0.25),  20);
        JLabel prompt2 = createLabel("Please declare a color...", (int) (W * 0.5), (int) (H * 0.35), 20);
        window.add(prompt1);
        window.add(prompt2);

    }

    /**
     * Set the ChooseColor window as visible or invisible. This is to prevent user click "X" by chance.
     */
    public void setAsVisible(boolean bool) {
        window.setVisible(bool);
    }

}
