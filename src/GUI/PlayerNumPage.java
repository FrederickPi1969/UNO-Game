package GUI;

import UNO.Game;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;
import javax.swing.*;
import javax.swing.undo.AbstractUndoableEdit;

/**
 * !!!!!!!!!!!!!!!!!!!!!! Viewer IN MVC !!!!!!!!!!!!!!!!!!!!!!!
 * Page immediately follows welcome page.
 * Prompt users to declare number of players for a game.
 *
 * MVC Design: This class won't employ any interface provided by Model (Player & ruleController).
 * It will only interact with Game (Controller).
 */
public class PlayerNumPage extends Frames {
    private final int W = 1080; // Width
    private final int H = 720; // Height
    private JTextField inputBoxHuman;
    private JTextField inputBoxAI;
    private final Game gameController;
    private final JFrame window;

    public PlayerNumPage(Game game) {
        gameController = game;
        window = new JFrame("UNO - Player Number");
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
     * Add start & exit button into the page.
     * @param window JFrame containing the buttons
     */
    public void addButtons(JFrame window) {
        // start button
        String startButtonPath = "./src/GUI/assets/startButton.png";
        JButton startButton = createImageButton(startButtonPath, (int) (W / 2 - W * 0.1), (int) (H * 0.55), W / 5, H / 10);
        startButton.addActionListener(new startButtonEventListener(window));
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
     * Add two input boxes (for setting human & AI numbers), and corresponding prompts to the window
     * @param window JFrame containing the prompts
     */
    public void addPrompts(JFrame window) {
        //Prompt for human player:
        JLabel promptHuman = createLabel("Number of human players:", (int) (W * 0.45), (int) (H * 0.15), 22);
        window.add(promptHuman);
        // input box
        Font font = new Font("SansSerif", Font.BOLD, 22);
        inputBoxHuman = new JTextField("0");
        inputBoxHuman.setFont(font);
        inputBoxHuman.setBounds((int) (W * 0.615), (int) (H * 0.12), (int) (W / 20), (int) (W / 20));
        window.add(inputBoxHuman);
        // Prompt for AI player:
        JLabel promptAI = createLabel("Number of AI players:", (int) (W * 0.475), (int) (H * 0.3), 22);
        window.add(promptAI);
        inputBoxAI = new JTextField("0");
        inputBoxAI.setFont(font);
        inputBoxAI.setBounds((int) (W * 0.615), (int) (H * 0.27), (int) (W / 20), (int) (W / 20));
        window.add(inputBoxAI);
    }

    private class startButtonEventListener implements ActionListener {
        private JFrame window;
        public startButtonEventListener(JFrame frame) {
            window = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int humanNum, AINum;
            try {
                humanNum = Integer.parseInt(inputBoxHuman.getText());
                AINum = Integer.parseInt(inputBoxAI.getText());
                if ( humanNum + AINum < 2 ||  humanNum + AINum >= 10) throw new ArithmeticException();
                window.dispose();
                gameController.setPlayerNumbers(humanNum, AINum);
                gameController.setSetupDone();

            } catch (ArithmeticException e1) {
                JOptionPane.showMessageDialog(window,"Total player numbers should be more than 1 and less than 10.",
                        "Illegal Players Numbers", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e2) {
                JOptionPane.showMessageDialog(window, "Please input [0-9] in the input boxes.",
                        "Illegal Input", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}


