package jumpgame;

/**
 * Action
 * Represents an action performed on the grid, including the direction of
 * movement, the cell number, the initial position of the cell before the
 * action, and the color.
 */
public class Action {
    private final Direction direction;
    private final int cellColor;
    private final int initialPosX;
    private final int initialPosY;
    private final int eliminatedColor;

    private final int jumpedCount;

    // -------------- Construter ---------------

    public Action(
            Direction direction, int cellColor, int initialPosX,
            int initialPosY, int eliminatedColor, int jumpedCount)
            throws IllegalArgumentException {

        validate(direction, cellColor, initialPosX, initialPosY,
                eliminatedColor, jumpedCount);

        this.direction = direction;
        this.cellColor = cellColor;
        this.initialPosX = initialPosX;
        this.initialPosY = initialPosY;
        this.eliminatedColor = eliminatedColor;
        this.jumpedCount = jumpedCount;
    }

    // --------------- Methods ------------------

    public boolean isValid() {
        return direction != null;
    }

    /**
     * Validates the parameters of the action to ensure they meet the required
     * conditions.
     * Throws IllegalArgumentException if any of the conditions are not met.
     * 
     * @param direction
     * @param cellColor
     * @param posX
     * @param posY
     * @param eliminatedColor
     * @param jumpedCount
     */
    private static void validate(
            Direction direction, int cellColor,
            int posX, int posY,
            int eliminatedColor, int jumpedCount) {

        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }

        if (cellColor <= 0 || eliminatedColor <= 0) {
            throw new IllegalArgumentException("Colors must be positive");
        }

        if (cellColor == eliminatedColor) {
            throw new IllegalArgumentException(
                    "Cannot jump over a token of the same color");
        }

        if (jumpedCount <= 0) {
            throw new IllegalArgumentException(
                    "Jumped count must be positive");
        }

        if (posX < 0 || posY < 0) {
            throw new IllegalArgumentException(
                    "Starting coordinates cannot be negative");
        }
    }

    // ----------- Getters----------------

    public Direction getDirection() {
        return direction;
    }

    public int getCellColor() {
        return cellColor;
    }

    public int[] getPos() {
        return new int[] { initialPosX, initialPosY };
    }

    public int getJumpedCount() {
        return jumpedCount;
    }

    public int getEliminatedColor() {
        return eliminatedColor;
    }
}
