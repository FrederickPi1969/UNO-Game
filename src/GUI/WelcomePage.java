package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import UNO.*;

/**
 * !!!!!!!!!!!!!!!!!!!!!! Viewer IN MVC !!!!!!!!!!!!!!!!!!!!!!!
 *  Welcome page for UNO (Viewer).
 *
 * MVC Design: This class won't employ any interface provided by Model (Player & ruleController).
 * It will only interact with Game (Controller).
 */
public class WelcomePage extends Frames {
    private final int W = 1080; // Width of window
    private final int H = 720; // Height of window
    private Game gameController; // Game Controller (Controller in MVC)

    /**
     * @param game the game controller in the MVC
     */
    public WelcomePage(Game game) {
        gameController = game;
        JFrame window = new JFrame("UNO - Welcome");
        addPrompts(window);
        addButtons(window);
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
     * Add welcome, exit buttons to the page.
     * @param window JFrame containing the buttons
     */
    public void addButtons(JFrame window) {
        // start button
        String startButtonPath = "./src/GUI/assets/startButton.png";
        JButton startButton = createImageButton(startButtonPath, (int) (W / 2 - W * 0.1), (int) (H * 0.55), W / 5, H / 10);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PlayerNumPage(gameController);
                window.dispose();
            }
        });
        window.add(startButton);

        // exit button
        String exitButtonPath = "./src/GUI/assets/exitButton.png";
        JButton exitButton = createImageButton(exitButtonPath, (int) (W / 2 - W * 0.1), (int) (H * 0.55 + H * 0.15), W / 5, H / 10);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        window.add(exitButton);
    }

    /**
     * All magic numbers are manually adjustable positionable information.
     * They are targeted for visual-understanding.
     *
     * Add UNO logo to the page.
     * @param window JFrame containing the prompts
     */
    public void addPrompts(JFrame window) {
        // logo
        String logoPath = "./src/GUI/assets/logo.png";
        JLabel logo = new JLabel(loadImageIcon(logoPath, W / 4, W / 4));
        logo.setBounds((int) (W * 0.5 - W / 8), (int) (H * 0.05), W / 4, W / 4);
        window.add(logo);
    }

}


