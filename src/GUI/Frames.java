package GUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * An abstract class for all frames to be built and unify their behaviors.
 * Provides implementation basic image icon loading & string JLabel constructions.
 */
public abstract class Frames {
    /**
     * Each frame in the game should add buttons by implementing this method.
     * @param window JFrame containing the buttons
     */
    public abstract void addButtons(JFrame window);

    /**
     * Each frame in the game should add prompts/inputBoxes/JLabels by implementing this method.
     * @param window JFrame containing the prompts
     */
    public abstract void addPrompts(JFrame window);

    /**
     * Add background image to frames (with adaptable size).
     * @param window JFrame containing the background image
     * @param  W, H the W of H of window
     */
    public void addBackground(JFrame window, int W, int H) {
        String backgroundPath = "./src/GUI/assets/background.jpg";
        JLabel background = new JLabel(loadImageIcon(backgroundPath, W, H));
        background.setBounds(0,0, W, H);
        window.add(background);
    }


    /**
     * Given the path of an image, transform the image to a ImageIcon with targeted size.
     * @param path path of target image
     * @param width targeted width of transformed image
     * @param height targeted height of transformed image
     * @return ImageIcon to be inserted into JLabel constructions
     */
    public ImageIcon loadImageIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage(); // transform it
        Image newImg = image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        return new ImageIcon(newImg);  // transform it back
    }

    /**
     * Given some words to be prompted, pretty-print the words to a JLabel and return it.
     * Please notice, this function only handles one-line input (without "\n")
     * @param words  words to be prompted (no "\n" is allowed)
     * @param x  center x-coordinate of the words
     * @param y  center y-coordinate of the words
     * @param fontSize font size to be used
     * @return
     */
    public JLabel createLabel(String words, int x, int y, float fontSize) {
        JLabel label = new JLabel(words);
        label.setFont(label.getFont().deriveFont(fontSize));

        // calculate the length & height of input words
        int strLength = label.getFontMetrics(label.getFont()).stringWidth(label.getText()) + 10; // +10 (give more space) because sometimes the words are not properly demonstrated
        int strHeight = label.getFontMetrics(label.getFont()).getHeight();
        label.setBounds(x - (strLength-10) / 2, y - strHeight / 2, strLength, strHeight); // centering, and set size
        return label;
    }

    /**
     * Creating an image button.
     * @param path the path of image to be used
     * @param x,y,w,h same set of parameters used in JButton.setBounds(x,y,w,h)
     * @return Image button with bounds settled
     */
    public JButton createImageButton(String path, int x, int y, int w, int h) {
        JButton button = new JButton(loadImageIcon(path, w, h));
        button.setBounds(x, y, w, h);
        return button;
    }

    /**
     * Creating an image button and add it to the given frame.
     * @param window the frame where the button to be inserted
     * @param x,y,w,h same set of parameters used in JButton.setBounds(x,y,w,h)
     * @param path the path of image to be used
     */
    public void createImageButtonAndAdd(JFrame window, String path, int x, int y, int w, int h) {
        JButton button = new JButton(loadImageIcon(path, w, h));
        button.setBounds(x, y, w, h);
        window.add(button);
    }

    /**
     * Create a empty JLabel wrapper with titled border box with assigned size.
     * @param window the frame where the button to be inserted
     * @param x, y, w, h same set of parameters used in JButton.setBounds(x,y,w,h)
     * @return Image button with bounds settled
     */
    public void createTitledBorderBoxAdd(JFrame window, String title, int x, int y, int w, int h) {
        Border borderline = BorderFactory.createLineBorder(Color.red);
        TitledBorder borderBox = BorderFactory.createTitledBorder(borderline, title);
        JLabel promptGameStateBoarder = new JLabel();
        promptGameStateBoarder.setBounds(x, y, w, h);
        promptGameStateBoarder.setBorder(borderBox);
        window.add(promptGameStateBoarder);
    }

    /**
     * Build a dialogue with input information and add it to the given JFrame window.
     * @param window the window where this dialogue will be inserted
     * @param title  the upper-left corner title for the dialogue
     * @param prompt the prompt string on main body of the dialogue
     * @param x  x coordinate where this dialogue would occur
     * @param y  y coordinate where this dialogue would occur
     * @param w  the width of dialogue
     * @param h  the height of dialogue
     */
    public void createDialogue(JFrame window, String title, String prompt, int x, int y, int w, int h) {
        JDialog d = new JDialog(window, title);
        JLabel p = new JLabel(prompt);
        d.setBounds(x, y, w, h);
        d.add(p);
        d.setVisible(true);
        p.setVisible(true);
    }

}
