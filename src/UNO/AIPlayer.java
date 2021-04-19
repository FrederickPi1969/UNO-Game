package UNO;

public abstract class AIPlayer extends Player {
    /**
     * Constructor for player objects
     *
     * @param ID   An unique integer identifier for a player
     * @param game An game controller object representing the game player being in
     */
    public AIPlayer(int ID, Game game) {
        super(ID, game);

    }

    /**
     * AI's behavior of picking color. Any AI classes should implement this method.
     * @return the color (string) he picks.
     */
    public abstract String pickColor();

    /**
     * AI's behavior of deciding cards to play. Any AI classes should implement this method.
     * @return the card (as ID) he picks.
     */
    public abstract int playCard();

    /**
     * AI's behavior of picking actions. Any AI class should implement this method.
     * @return the action (int) he picks.
     */
    public abstract int makeActionDecision();

    /**
     * Convert color (ID) to Color (String)).
     * @param colorID 0, 1, 2, 3, 4
     * @return color "NA", "red", "green", "blue", or "yellow"
     */
    public String colorID2color(int colorID) {
        return switch (colorID) {
            case 0 -> "NA";
            case 1 -> "red";
            case 2 -> "green";
            case 3 -> "blue";
            case 4 -> "yellow";
            default -> null; // should not be reached
        };
    }

    /**
     * Convert color (String) to Color ID.
     * @param color "NA", "red", "green", "blue", or "yellow"
     * @return colorID 0, 1, 2, 3, 4
     */
    public int color2colorID(String color) {
        return switch (color) {
            case "NA" -> 0;
            case "red" -> 1;
            case "green" -> 2;
            case "blue" -> 3;
            case "yellow" -> 4;
            default -> -1; // should not be reached
        };
    }

    @Override
    public boolean isHuman() {
        return false;
    }

}
