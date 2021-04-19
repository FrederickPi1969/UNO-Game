package UNO;

import GUI.*;

import javax.swing.*;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Enter point for the whole game.
 */
public class Main {
    /**
     * The enter for running the whole game. It eases the interaction between Game (Controller in MVC),
     * and the GUI (Viewer in MVC). Note that update of game state stored in RuleController class (Model in MVC),
     * should not be updated here.
     */
    public static void main(String[] args) throws InterruptedException {
        while(true) {
            Game game = new Game(0, 0); // declare an uninitialized game
            new WelcomePage(game);
            PrintStream original = System.out;
            System.setOut(new PrintStream(new OutputStream() {
                public void write(int b) {
                }
            })); // we don't want garbage print-out information
            while (!game.isSetupDone()) { // see the comment for this variable on top for details
                System.out.println("looping..."); // do not delete this line or code won't run!
            }
            System.setOut(original);
            game.initializeGame();
            game.gameStart();
            System.setOut(new PrintStream(new OutputStream() {
                public void write(int b) {
                }
            })); // we don't want garbage print-out information
            while (!game.toStartNewGame()) { // see the comment for this variable on top for details
                System.out.println("looping..."); // do not delete this line or code won't run!
            }
            System.setOut(original);
        }
    }
}
